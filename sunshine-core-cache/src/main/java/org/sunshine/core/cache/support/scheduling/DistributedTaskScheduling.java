package org.sunshine.core.cache.support.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.cache.redisson.util.RedissonLockUtils;
import org.sunshine.core.tool.support.Try;

/**
 * 分布式定时任务接口
 *
 * @author Teamo
 * @since 2021/11/10
 */
@FunctionalInterface
public interface DistributedTaskScheduling {

    Logger log = LoggerFactory.getLogger(DistributedTaskScheduling.class);

    /**
     * 任务
     *
     * @throws Exception Exception
     */
    void task() throws Exception;

    /**
     * 执行
     *
     * @param lockKey 锁名称
     */
    default void execute(String lockKey) {
        RedissonLockUtils.tryLock(lockKey, Try.accept(lock -> {
            if (lock) {
                this.task();
            }
        }), throwable -> log.error(throwable.getMessage(), throwable));
    }
}
