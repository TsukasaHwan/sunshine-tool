package org.sunshine.core.cache.redisson;

import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2019/9/29
 */
public interface Locker {

    /**
     * 获取锁，如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。
     *
     * @param lockKey 锁名称
     */
    void lock(String lockKey);

    /**
     * 释放锁
     *
     * @param lockKey 锁名称
     */
    void unlock(String lockKey);

    /**
     * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
     *
     * @param lockKey 锁名称
     * @param timeout 超时时长（单位：秒）
     */
    void lock(String lockKey, int timeout);

    /**
     * 获取锁,如果锁不可用，则当前线程处于休眠状态，直到获得锁为止。如果获取到锁后，执行结束后解锁或达到超时时间后会自动释放锁
     *
     * @param lockKey 锁名称
     * @param timeout 超时时长
     * @param unit    时间单位
     */
    void lock(String lockKey, int timeout, TimeUnit unit);

    /**
     * 尝试获取锁，获取到立即返回true,未获取到立即返回false
     *
     * @param lockKey 锁名称
     * @return 是否锁定成功
     */
    boolean tryLock(String lockKey);

    /**
     * 尝试使用定义的leaseTime获取锁。如有必要，等待定义的waitTime ，直到锁定可用。锁将在定义的leaseTime间隔后自动释放。
     *
     * @param lockKey   锁名称
     * @param waitTime  等待时长
     * @param leaseTime 超时时长
     * @param unit      时间单位
     * @return 是否锁定成功
     * @throws InterruptedException InterruptedException
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

    /**
     * 锁是否被任意一个线程锁持有
     *
     * @param lockKey 锁名称
     * @return 是否锁定
     */
    boolean isLocked(String lockKey);

    /**
     * 检查此锁是否由当前线程持有
     *
     * @param lockKey 锁名称
     * @return 是否持有
     */
    boolean isHeldByCurrentThread(String lockKey);
}
