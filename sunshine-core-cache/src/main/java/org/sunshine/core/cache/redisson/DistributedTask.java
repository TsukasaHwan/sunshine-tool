package org.sunshine.core.cache.redisson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.tool.support.Try;

/**
 * 分布式定时任务接口
 *
 * @author Teamo
 * @since 2021/11/10
 */
@FunctionalInterface
public interface DistributedTask {

    Logger log = LoggerFactory.getLogger(DistributedTask.class);

    /**
     * 任务
     *
     * @throws Exception Exception
     */
    void execute() throws Exception;

    /**
     * 带锁执行方法（默认实现）
     */
    default void runWithLock(String lockKey, RedissonLockTemplate template) {
        template.tryLockWithoutResult(lockKey, Try.accept(isLocked -> {
            if (!isLocked) {
                return;
            }
            this.execute();
        }), this::handleException);
    }

    /**
     * 异常处理
     *
     * @param exception Exception
     */
    default void handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
    }

}
