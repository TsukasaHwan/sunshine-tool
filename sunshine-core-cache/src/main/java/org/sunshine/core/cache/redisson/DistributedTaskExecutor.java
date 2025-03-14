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

    /**
     * 分布式任务
     */
    private DistributedTask task;

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
     * 设置分布式任务
     *
     * @param task 分布式任务
     * @return DistributedTaskExecutor
     */
    public DistributedTaskExecutor task(DistributedTask task) {
        this.task = task;
        return this;
    }

    /**
     * 执行分布式任务
     *
     * @param lockKey 锁键
     */
    public void execute(String lockKey) {
        task.runWithLock(lockKey, template);
    }
}
