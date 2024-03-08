package org.sunshine.core.cache.redisson.queue;

import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Teamo
 * @since 2022/11/09
 */
public interface DelayedQueueJob<T> {

    Logger log = LoggerFactory.getLogger(DelayedQueueJob.class);

    /**
     * 是否启用
     *
     * @return boolean
     */
    default boolean isEnable() {
        return true;
    }

    /**
     * 启动
     *
     * @param redissonClient RedissonClient
     */
    default void start(RedissonClient redissonClient) {
        String threadName = "DelayedQueueJob-" + this.getClass().getSimpleName();
        Thread.currentThread().setName(threadName);
        if (!isEnable()) {
            return;
        }
        RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(dequeKey());
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T take = blockingDeque.take();
                consume(take);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                whenExceptionFinally();
            }
        }
    }

    /**
     * 队列键
     *
     * @return String
     */
    String dequeKey();

    /**
     * 消费
     *
     * @param take Object
     * @throws Exception Exception
     */
    void consume(T take) throws Exception;

    /**
     * 发生异常时最终处理
     */
    default void whenExceptionFinally() {
    }
}
