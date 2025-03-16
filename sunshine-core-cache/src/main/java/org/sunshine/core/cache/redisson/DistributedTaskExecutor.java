package org.sunshine.core.cache.redisson;

/**
 * 分布式任务执行器
 *
 * @author Teamo
 * @since 2025/3/14
 */
public class DistributedTaskExecutor {

    /**
     * RedissonLockTemplate
     */
    private final RedissonLockTemplate template;

    public DistributedTaskExecutor(RedissonLockTemplate template) {
        this.template = template;
    }

    /**
     * 构建器
     *
     * @param template RedissonLockTemplate
     * @return DistributedTaskExecutor
     */
    public static DistributedTaskExecutor builder(RedissonLockTemplate template) {
        return new DistributedTaskExecutor(template);
    }

    /**
     * 执行分布式任务
     *
     * @param task    分布式任务
     * @param lockKey 锁键
     */
    public synchronized void execute(DistributedTask task, String lockKey) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
        task.runWithLock(lockKey, template);
    }
}
