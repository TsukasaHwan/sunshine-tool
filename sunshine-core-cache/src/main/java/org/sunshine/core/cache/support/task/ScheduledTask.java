package org.sunshine.core.cache.support.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.cache.redission.util.RedissionLockUtils;

/**
 * 分布式定时任务接口
 *
 * @author Teamo
 * @since 2021/11/10
 */
@FunctionalInterface
public interface ScheduledTask {
    Logger log = LoggerFactory.getLogger(ScheduledTask.class);

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
        boolean lock = false;
        try {
            lock = RedissionLockUtils.tryLock(lockKey);
            if (lock) {
                this.taskLogic();
            }
        } catch (Exception e) {
            log.error("Execution of distributed task exception:", e);
        } finally {
            RedissionLockUtils.unlock(lock, lockKey);
        }
    }
}
