package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.tool.support.Try;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2024/3/11
 */
class DelayedQueuePollTask<T> implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(DelayedQueuePollTask.class);

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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T message = blockingDeque.take();
                consume(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                delayedQueueListener.whenExceptionFinally();
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
            delayedThreadPoolExecutor.execute(Try.run(() -> delayedQueueListener.consume(message), throwable -> log.error(throwable.getMessage(), throwable)));
        } else {
            delayedQueueListener.consume(message);
        }
    }
}
