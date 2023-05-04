package org.sunshine.core.cache.support.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.cache.redission.util.RedissionLockUtils;
import org.sunshine.core.tool.util.Try;

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
     * 运行业务逻辑
     *
     * @throws Exception Exception
     */
    void taskLogic() throws Exception;

    /**
     * 执行分布式任务
     *
     * @param lockKey 锁名称
     */
    default void executeLogic(String lockKey) {
        RedissionLockUtils.tryLock(lockKey, Try.accept(lock -> {
            if (lock) {
                this.taskLogic();
            }
        }), throwable -> log.error(throwable.getMessage(), throwable));
    }
}
