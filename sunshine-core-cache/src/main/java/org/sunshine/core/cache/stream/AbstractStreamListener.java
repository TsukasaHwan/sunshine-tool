package org.sunshine.core.cache.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamListener;
import org.sunshine.core.cache.RedisClient;

import java.time.Duration;
import java.util.*;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public abstract class AbstractStreamListener<T> implements StreamListener<String, ObjectRecord<String, T>> {

    private final static Logger log = LoggerFactory.getLogger(AbstractStreamListener.class);

    protected final RedisClient redisClient;

    protected AbstractStreamListener(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * Redis Stream
     *
     * @return {@link RedisStreamKey}
     */
    protected abstract RedisStreamKey redisStreamKey();

    /**
     * 清理消息队列
     */
    protected void trimStream(long count) {
        // 定时的清理stream中的数据
        redisClient.streamTrim(redisStreamKey().stream(), count);
    }

    /**
     * 清理所有消息队列
     */
    protected void trimAllStream() {
        String stream = redisStreamKey().stream();
        String group = redisStreamKey().group();
        redisClient.streamPending(stream, group).ifPresent(pendingMessagesSummary -> {
            long count = pendingMessagesSummary.getTotalPendingMessages();
            trimStream(count);
        });
    }

    /**
     * 处理死信
     */
    protected void handleDeadLetter() {
        // 处理死信队列
        // 获取group中的pending消息信息
        String streamName = redisStreamKey().stream();
        String streamGroupName = redisStreamKey().group();
        Optional<PendingMessagesSummary> optionalPendingMessagesSummary = redisClient.streamPending(streamName, streamGroupName);
        if (!optionalPendingMessagesSummary.isPresent()) {
            return;
        }
        PendingMessagesSummary pendingMessagesSummary = optionalPendingMessagesSummary.get();
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
            // 待重试的 RecordId
            if (consumerTotalPendingMessages > 0) {
                // 读取消费者pending队列的前10条记录，从ID=0的记录开始，一直到ID最大值
                PendingMessages pendingMessages = redisClient.streamPending(streamName, Consumer.from(streamGroupName, consumer), Range.closed("0", "+"), 10);
                // 遍历所有pending消息的详情
                List<RecordId> transferGroupMessageList = new ArrayList<>();
                pendingMessages.forEach(pendingMessage -> handleTransferGroupList(pendingMessage, transferGroupMessageList));
                if (transferGroupMessageList.size() > 0) {
                    oldConsumerTransferMessageMap.put(consumer, transferGroupMessageList);
                }
            }
        });

        changeConsumer(oldConsumerTransferMessageMap);
    }

    /**
     * 处理待转移消息列表
     *
     * @param pendingMessage           挂起的消息
     * @param transferGroupMessageList 待转移的RecordId
     */
    protected void handleTransferGroupList(PendingMessage pendingMessage, List<RecordId> transferGroupMessageList) {
        // 消息的ID
        RecordId recordId = pendingMessage.getId();
        // 消息从消费组中获取，到此刻的时间
        Duration elapsedTimeSinceLastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery();
        // 消息被获取的次数
        long deliveryCount = pendingMessage.getTotalDeliveryCount();
        if (elapsedTimeSinceLastDelivery.getSeconds() > 20 && deliveryCount == 1) {
            transferGroupMessageList.add(recordId);
        } else {
            redisClient.streamAck(redisStreamKey().stream(), redisStreamKey().group(), recordId);
        }
    }

    /**
     * 改变消费者
     *
     * @param oldConsumerTransferMessageMap 旧消费者待转移消息Map
     */
    protected void changeConsumer(Map<String, List<RecordId>> oldConsumerTransferMessageMap) {
        oldConsumerTransferMessageMap.forEach((oldConsumer, recordIds) -> {
            // 根据当前consumer去获取另外一个consumer
            StreamInfo.XInfoConsumers consumers = redisClient.streamConsumers(redisStreamKey().stream(), redisStreamKey().group());
            List<StreamInfo.XInfoConsumer> newConsumers = consumers.stream().filter(consumer -> !consumer.consumerName().equals(oldConsumer)).toList();
            newConsumers.forEach(consumer -> {
                List<MapRecord<String, Object, Object>> mapRecords = redisClient.streamClaim(redisStreamKey().stream(), redisStreamKey().group(), oldConsumer, RedisStreamCommands.XClaimOptions.minIdle(Duration.ofSeconds(10)).ids(recordIds));
                if (log.isDebugEnabled()) {
                    mapRecords.forEach(entries -> log.debug("转移消息 id: {}, value: {}", entries.getId(), entries.getValue()));
                }
            });
        });
    }
}
