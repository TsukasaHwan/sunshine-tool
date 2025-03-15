package org.sunshine.core.cache.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.sunshine.core.cache.RedisMQTemplate;
import org.sunshine.core.cache.redisson.DistributedTask;
import org.sunshine.core.cache.redisson.DistributedTaskExecutor;
import org.sunshine.core.tool.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Teamo
 * @since 2024/7/9
 */
public class StreamDeadLetterQueueProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StreamDeadLetterQueueProcessor.class);

    public static final String TRIM_LOCK_TEMPLATE = "lock:redis-stream:trim:%s";
    public static final String DEAD_LETTER_SUB_LOCK_TEMPLATE = "lock:redis-stream:dead-letter:%s";
    private static final String DEAD_LETTER_MAIN_LOCK = "lock:redis-stream:dead-letter:main";
    private final List<AbstractStreamListener<?>> listeners;
    private final RedisMQTemplate redisMQTemplate;
    private final DistributedTaskExecutor distributedTaskExecutor;

    public StreamDeadLetterQueueProcessor(List<AbstractStreamListener<?>> listeners,
                                          RedisMQTemplate redisMQTemplate,
                                          DistributedTaskExecutor distributedTaskExecutor) {
        this.listeners = listeners.stream().filter(listener -> !listener.getDeadLetterConfig().isEnableDedicatedScheduler()).toList();
        this.redisMQTemplate = redisMQTemplate;
        this.distributedTaskExecutor = distributedTaskExecutor;
    }

    /**
     * 主死信任务
     */
    @Scheduled(cron = "${spring.data.redis.stream.dead-letter-task-cron:30 * * * * ?}")
    public void scheduleMainDeadLetterTask() {
        DistributedTask distributedTask = () -> listeners.forEach(this::processPendingDeadLetters);

        distributedTaskExecutor.task(distributedTask)
                .execute(DEAD_LETTER_MAIN_LOCK);
    }

    /**
     * 流修剪任务，动态创建
     *
     * @param listener 监听器
     */
    public void scheduleTrimTask(AbstractStreamListener<?> listener) {
        DistributedTask distributedTask = () -> redisMQTemplate.redisTemplate()
                .opsForStream()
                .trim(listener.getStreamKey(), listener.getTrimConfig().getMaxCount());

        distributedTaskExecutor.task(distributedTask)
                .execute(String.format(TRIM_LOCK_TEMPLATE, listener.getStreamKey()));
    }

    /**
     * 消费者死信任务，动态创建
     *
     * @param listener 监听器
     */
    public void scheduleDeadLetterTask(AbstractStreamListener<?> listener) {
        DistributedTask distributedTask = () -> this.processPendingDeadLetters(listener);

        distributedTaskExecutor.task(distributedTask)
                .execute(String.format(DEAD_LETTER_SUB_LOCK_TEMPLATE, listener.getStreamKey()));
    }

    /**
     * 处理pending消息
     *
     * @param listener 监听器
     */
    private void processPendingDeadLetters(AbstractStreamListener<?> listener) {
        StreamOperations<String, Object, Object> streamOperations = redisMQTemplate.redisTemplate().opsForStream();
        String streamKey = listener.getStreamKey();
        String group = listener.getGroup();
        PendingMessagesSummary pendingMessagesSummary = streamOperations.pending(streamKey, group);
        if (pendingMessagesSummary == null) {
            return;
        }
        long totalPendingMessages = pendingMessagesSummary.getTotalPendingMessages();
        if (totalPendingMessages == 0) {
            return;
        }

        AbstractStreamMessage.DeadLetterConfig deadLetterConfig = listener.getDeadLetterConfig();
        // 获取每个消费者的pending消息数量
        Map<String, Long> pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
        // 遍历每个消费者中的pending消息
        pendingMessagesPerConsumer.forEach((consumer, consumerTotalPendingMessages) -> {
            if (consumerTotalPendingMessages == 0) {
                return;
            }
            // 读取消费者pending队列
            PendingMessages pendingMessages = streamOperations.pending(streamKey, Consumer.from(group, consumer));
            pendingMessages.forEach(pendingMessage -> {
                // 消息投递到现在的时间
                if (pendingMessage.getElapsedTimeSinceLastDelivery().compareTo(deadLetterConfig.getPendingProcessingTimeout()) < 0) {
                    return;
                }

                RecordId recordId = pendingMessage.getId();

                logger.info("[RETRY_PREPARE] 开始重新投递消息{}，流键：{}，消费者组：{}，超时时间：{}",
                        recordId, streamKey, group, pendingMessage.getElapsedTimeSinceLastDelivery());

                List<MapRecord<String, Object, Object>> records = streamOperations.range(streamKey, Range.just(recordId.getValue()));
                if (CollectionUtils.isEmpty(records)) {
                    logger.warn("[RECORD_NOT_FOUND] 未找到消息记录{}, 流键：{}", recordId, streamKey);
                    return;
                }
                // 重新投递
                streamOperations.add(StreamRecords.newRecord()
                        .ofObject(records.get(0).getValue())
                        .withStreamKey(streamKey));
                streamOperations.acknowledge(group, records.get(0));
                logger.info("[RETRY_SUCCESS] 消息{}重新投递成功，流键：{}", recordId, streamKey);
            });
        });
    }

}
