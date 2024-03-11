package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Teamo
 * @since 2024/3/11
 */
class DelayedQueuePollTask<T> implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(DelayedQueuePollTask.class);

    private final RedissonClient redissonClient;

    private final DelayedQueueListener<T> delayedQueueListener;

    public DelayedQueuePollTask(RedissonClient redissonClient, DelayedQueueListener<T> delayedQueueListener) {
        this.redissonClient = redissonClient;
        this.delayedQueueListener = delayedQueueListener;
    }

    @Override
    public void run() {
        String threadName = "delayed-queue-listener-" + delayedQueueListener.getClass().getSimpleName();
        Thread.currentThread().setName(threadName);
        if (!delayedQueueListener.isEnable()) {
            return;
        }
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(delayedQueueListener.delayedQueueKey());
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T message = blockingDeque.take();
                delayedQueueListener.consume(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                delayedQueueListener.whenExceptionFinally();
            }
        }
    }
}
