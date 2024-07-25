package org.sunshine.core.cache.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.cache.support.scheduling.DistributedTaskScheduling;
import org.sunshine.core.tool.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Teamo
 * @since 2024/7/9
 */
@EnableScheduling
public class RedisPendingMessageScheduledTask {

    private final static Logger log = LoggerFactory.getLogger(RedisPendingMessageScheduledTask.class);

    private final static String LOCK_KEY = "lock:scheduled:redis:pending:message";

    private final List<AbstractStreamListener<?>> listeners;

    private final RedisMQTemplate redisMQTemplate;

    public RedisPendingMessageScheduledTask(List<AbstractStreamListener<?>> listeners, RedisMQTemplate redisMQTemplate) {
        this.listeners = listeners;
        this.redisMQTemplate = redisMQTemplate;
    }

    @Scheduled(cron = "30 * * * * ?")
    public void run() {
        DistributedTaskScheduling scheduling = this::handlePendingMessage;
        scheduling.execute(LOCK_KEY);
    }

    private void handlePendingMessage() {
        StreamOperations<String, Object, Object> ops = redisMQTemplate.redisTemplate().opsForStream();
        listeners.forEach(listener -> {
            String streamKey = listener.getStreamKey();
            String group = listener.getGroup();
            // 获取group中的pending消息信息
            PendingMessagesSummary pendingMessagesSummary = ops.pending(streamKey, group);
            if (pendingMessagesSummary == null) {
                return;
            }
            // 所有pending消息的数量
            long totalPendingMessages = pendingMessagesSummary.getTotalPendingMessages();
            if (totalPendingMessages == 0) {
                return;
            }
            // 获取每个消费者的pending消息数量
            Map<String, Long> pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
            Map<String, List<RecordId>> oldConsumerTransferMessageMap = new HashMap<>();
            // 遍历每个消费者中的pending消息
            pendingMessagesPerConsumer.forEach((consumer, consumerTotalPendingMessages) -> {
                if (consumerTotalPendingMessages == 0) {
                    return;
                }
                // 读取消费者pending队列
                PendingMessages pendingMessages = ops.pending(streamKey, Consumer.from(group, consumer));
                List<RecordId> transferGroupMessageList = new ArrayList<>(pendingMessages.size());
                pendingMessages.forEach(pendingMessage -> {
                    RecordId recordId = pendingMessage.getId();
                    // 消息从消费组中获取，到此刻的时间
                    Duration elapsedTimeSinceLastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery();
                    // 消息被获取的次数
                    long deliveryCount = pendingMessage.getTotalDeliveryCount();
                    if (elapsedTimeSinceLastDelivery.getSeconds() > 20 && deliveryCount == 1) {
                        // 转组
                        transferGroupMessageList.add(recordId);
                    } else if (deliveryCount > 1) {
                        // 重新投递并消费
                        List<MapRecord<String, Object, Object>> records = ops.range(streamKey, Range.rightOpen(recordId.getValue(), recordId.getValue()));
                        if (CollectionUtils.isEmpty(records)) {
                            return;
                        }
                        MapRecord<String, Object, Object> entries = records.get(0);
                        ops.add(Record.of(entries.getValue()).withStreamKey(streamKey));
                        ops.acknowledge(streamKey, group, recordId);
                    }
                });

                if (transferGroupMessageList.size() > 0) {
                    oldConsumerTransferMessageMap.put(consumer, transferGroupMessageList);
                }
            });

            oldConsumerTransferMessageMap.forEach((oldConsumer, recordIds) -> {
                // 根据当前consumer去获取另外一个consumer
                StreamInfo.XInfoConsumers consumers = ops.consumers(streamKey, group);
                List<StreamInfo.XInfoConsumer> newConsumers = consumers.stream().filter(consumer -> !consumer.consumerName().equals(oldConsumer)).toList();
                newConsumers.forEach(consumer -> {
                    List<MapRecord<String, Object, Object>> mapRecords = ops.claim(streamKey, group, oldConsumer, RedisStreamCommands.XClaimOptions.minIdle(Duration.ofSeconds(10)).ids(recordIds));
                    mapRecords.forEach(entries -> log.info("转移消息 id: {}, value: {}", entries.getId(), entries.getValue()));
                });
            });
        });
    }
}
