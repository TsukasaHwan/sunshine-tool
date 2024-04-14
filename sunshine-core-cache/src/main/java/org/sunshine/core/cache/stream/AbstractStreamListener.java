package org.sunshine.core.cache.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamListener;
import org.sunshine.core.cache.RedisClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public abstract class AbstractStreamListener<T> implements StreamListener<String, ObjectRecord<String, T>> {

    private final static Logger log = LoggerFactory.getLogger(AbstractStreamListener.class);

    private final Class<T> clazz;

    protected final RedisClient redisClient;

    protected AbstractStreamListener(Class<T> clazz,
                                     RedisClient redisClient) {
        this.clazz = clazz;
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
    protected void clearStream() {
        // 定时的清理stream中的数据
        String stream = redisStreamKey().stream();
        String group = redisStreamKey().group();
        redisClient.streamPending(stream, group).ifPresent(pendingMessagesSummary -> {
            long count = pendingMessagesSummary.getTotalPendingMessages();
            redisClient.streamTrim(stream, count);
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
        if (optionalPendingMessagesSummary.isEmpty()) {
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
        // 遍历每个消费者中的pending消息
        pendingMessagesPerConsumer.forEach((consumer, consumerTotalPendingMessages) -> {
            // 待重试的 RecordId
            if (consumerTotalPendingMessages > 0) {
                // 读取消费者pending队列的前10条记录，从ID=0的记录开始，一直到ID最大值
                PendingMessages pendingMessages = redisClient.streamPending(streamName, Consumer.from(streamGroupName, consumer), Range.closed("0", "+"), 10);
                // 遍历所有pending消息的详情
                pendingMessages.forEach(this::retry);
            }
        });
    }

    /**
     * 默认重试机制
     *
     * @param pendingMessage PendingMessage
     */
    protected void retry(PendingMessage pendingMessage) {
        // 消息的ID
        RecordId recordId = pendingMessage.getId();
        // 消息从消费组中获取，到此刻的时间
        Duration elapsedTimeSinceLastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery();
        // 消息被获取的次数
        long deliveryCount = pendingMessage.getTotalDeliveryCount();
        if (elapsedTimeSinceLastDelivery.getSeconds() > 20 && deliveryCount >= 1) {
            List<ObjectRecord<String, T>> result = redisClient.streamRange(clazz, redisStreamKey().stream(), Range.just(recordId.getValue()));
            if (result != null && result.size() > 0) {
                // 重试消息
                ObjectRecord<String, T> objectRecord = result.get(0);
                log.info("RedisStream消息重试:流:{},组:{},消费者:{},消息ID:{}", redisStreamKey().stream(), redisStreamKey().group(), redisStreamKey().consumer(), recordId);
                onMessage(objectRecord);
            }
        }
    }
}
