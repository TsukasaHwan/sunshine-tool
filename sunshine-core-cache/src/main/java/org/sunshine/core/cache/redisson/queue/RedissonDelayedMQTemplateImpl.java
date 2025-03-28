package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2025/3/27
 */
public record RedissonDelayedMQTemplateImpl(RedissonClient redissonClient)
        implements RedissonDelayedMQTemplate {

    @Override
    public <T> void send(DelayedRecord<T> record) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(record.getQueue());
        try {
            delayedQueue.offer(record.getValue(), record.getDelay(), record.getUnit());
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean remove(String queue, T value) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queue);
        try {
            return delayedQueue.remove(value);
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean removeAll(String queue, Collection<T> values) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queue);
        try {
            return delayedQueue.removeAll(values);
        } finally {
            delayedQueue.destroy();
        }
    }

    /**
     * 获取延迟队列
     *
     * @param queue 队列名称
     * @param <T>   泛型
     * @return 延迟队列
     */
    private <T> RDelayedQueue<T> getDelayedQueue(String queue) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queue);
        return redissonClient.getDelayedQueue(blockingDeque);
    }
}
