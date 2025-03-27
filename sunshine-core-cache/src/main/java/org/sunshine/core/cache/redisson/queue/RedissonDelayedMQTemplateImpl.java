package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2025/3/27
 */
public record RedissonDelayedMQTemplateImpl(RedissonClient redissonClient)
        implements RedissonDelayedMQTemplate {

    @Override
    public <T> void send(String queueName, T message, long delay, TimeUnit timeUnit) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queueName);
        try {
            delayedQueue.offer(message, delay, timeUnit);
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean remove(String queueName, T message) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queueName);
        try {
            return delayedQueue.remove(message);
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean removeAll(String queueName, Collection<T> messages) {
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queueName);
        try {
            return delayedQueue.removeAll(messages);
        } finally {
            delayedQueue.destroy();
        }
    }

    /**
     * 获取延迟队列
     *
     * @param queueName 队列名称
     * @param <T>       泛型
     * @return 延迟队列
     */
    private <T> RDelayedQueue<T> getDelayedQueue(String queueName) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queueName);
        return redissonClient.getDelayedQueue(blockingDeque);
    }
}
