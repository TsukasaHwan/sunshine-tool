package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.sunshine.core.tool.support.Try;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2024/3/11
 */
class DelayedQueuePollTask<T> implements Runnable {

    private final static String LOCK_TEMPLATE = "lock:redis-delayed-queue:%s";

    private final RedissonClient redissonClient;

    private final DelayedQueueListener<T> delayedQueueListener;

    private final ThreadPoolExecutor delayedThreadPoolExecutor;

    public DelayedQueuePollTask(RedissonClient redissonClient, DelayedQueueListener<T> delayedQueueListener) {
        this.redissonClient = redissonClient;
        this.delayedQueueListener = delayedQueueListener;
        this.delayedThreadPoolExecutor = delayedQueueListener.getThreadPoolExecutor();
    }

    @Override
    public void run() {
        if (!delayedQueueListener.isEnable()) {
            return;
        }
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(delayedQueueListener.delayedQueueKey());
        // 解决消息丢失问题，发送subscribe命令订阅redis队列
        redissonClient.getDelayedQueue(blockingDeque);

        RLock lock = redissonClient.getLock(String.format(LOCK_TEMPLATE, delayedQueueListener.delayedQueueKey()));
        boolean isLocked = false;
        T message;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                message = blockingDeque.take();
                isLocked = lock.tryLock();
                if (!isLocked) {
                    continue;
                }
                consume(message);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                delayedQueueListener.handleException(e);
            } finally {
                if (isLocked) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 销毁线程池
     */
    public void destroy() {
        if (delayedThreadPoolExecutor == null) {
            return;
        }
        delayedThreadPoolExecutor.shutdown();
    }

    /**
     * 消费消息
     *
     * @param message 消息
     * @throws Exception 异常
     */
    private void consume(T message) throws Exception {
        if (delayedThreadPoolExecutor != null) {
            delayedThreadPoolExecutor.execute(Try.run(() -> delayedQueueListener.consume(message)));
        } else {
            delayedQueueListener.consume(message);
        }
    }
}
