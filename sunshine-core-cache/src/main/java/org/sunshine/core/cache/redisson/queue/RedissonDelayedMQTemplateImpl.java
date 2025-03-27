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
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        try {
            delayedQueue.offer(message, delay, timeUnit);
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean remove(String queueName, T message) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        try {
            return delayedQueue.remove(message);
        } finally {
            delayedQueue.destroy();
        }
    }

    @Override
    public <T> boolean removeAll(String queueName, Collection<T> messages) {
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(queueName);
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        try {
            return delayedQueue.removeAll(messages);
        } finally {
            delayedQueue.destroy();
        }
    }
}
