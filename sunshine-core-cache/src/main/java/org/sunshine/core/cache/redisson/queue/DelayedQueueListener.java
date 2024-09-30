package org.sunshine.core.cache.redisson.queue;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Teamo
 * @since 2022/11/09
 */
public interface DelayedQueueListener<T> {

    /**
     * 是否启用
     *
     * @return boolean
     */
    default boolean isEnable() {
        return true;
    }

    /**
     * 队列键
     *
     * @return String
     */
    String delayedQueueKey();

    /**
     * 消费
     *
     * @param message Object
     * @throws Exception Exception
     */
    void consume(T message) throws Exception;

    /**
     * 默认线程池
     *
     * @return ThreadPoolExecutor
     */
    default ThreadPoolExecutor getThreadPoolExecutor() {
        return null;
    }

    /**
     * 最终处理
     */
    default void whenFinally() {
    }
}
