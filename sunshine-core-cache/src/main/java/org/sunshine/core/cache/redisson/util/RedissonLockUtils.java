package org.sunshine.core.cache.redisson.util;

import org.sunshine.core.cache.redisson.Locker;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Teamo
 * @since 2019/9/29
 */
public class RedissonLockUtils {

    private static Locker locker;

    /**
     * 设置工具类使用的locker
     *
     * @param locker locker
     */
    public static void setLocker(Locker locker) {
        RedissonLockUtils.locker = locker;
    }

    /**
     * 获取锁
     *
     * @param lockKey 锁名称
     */
    public static void lock(String lockKey) {
        locker.lock(lockKey);
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁名称
     */
    public static void unlock(String lockKey) {
        locker.unlock(lockKey);
    }

    /**
     * 释放当前线程持有的锁
     *
     * @param isLocked 是否锁上
     * @param lockKey  锁名称
     */
    public static void unlock(boolean isLocked, String lockKey) {
        if (isLocked && isHeldByCurrentThread(lockKey)) {
            unlock(lockKey);
        }
    }

    /**
     * 获取锁，超时释放
     *
     * @param lockKey 锁名称
     * @param timeout 超时时长 （单位：秒）
     */
    public static void lock(String lockKey, int timeout) {
        locker.lock(lockKey, timeout);
    }

    /**
     * 获取锁，超时释放，指定时间单位
     *
     * @param lockKey 锁名称
     * @param timeout 超时时长
     * @param unit    时间单位
     */
    public static void lock(String lockKey, int timeout, TimeUnit unit) {
        locker.lock(lockKey, timeout, unit);
    }

    /**
     * 尝试获取锁，获取到立即返回true,获取失败立即返回false
     *
     * @param lockKey 锁名称
     * @return 是否锁定成功
     */
    public static boolean tryLock(String lockKey) {
        return locker.tryLock(lockKey);
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey       锁名称
     * @param lockConsumer  锁消费者
     * @param errorConsumer 异常消费者
     */
    public static void tryLock(String lockKey, Consumer<Boolean> lockConsumer, Consumer<Throwable> errorConsumer) {
        boolean isLocked = false;
        try {
            isLocked = tryLock(lockKey);
            lockConsumer.accept(isLocked);
        } catch (Throwable e) {
            errorConsumer.accept(e);
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * 尝试获取锁，在给定的waitTime时间内尝试，获取到返回true,获取失败返回false,获取到后再给定的leaseTime时间超时释放
     *
     * @param lockKey   锁名称
     * @param waitTime  等待时长
     * @param leaseTime 超时释放
     * @param unit      时间单位
     * @return 是否锁定成功
     * @throws InterruptedException InterruptedException
     */
    public static boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        return locker.tryLock(lockKey, waitTime, leaseTime, unit);
    }

    /**
     * 尝试获取锁，在给定的waitTime时间内尝试，获取到返回true,获取失败返回false,获取到后再给定的leaseTime时间超时释放
     *
     * @param lockKey       锁名称
     * @param waitTime      等待时长
     * @param leaseTime     超时释放
     * @param unit          时间单位
     * @param lockConsumer  锁消费者
     * @param errorConsumer 异常消费者
     */
    public static void tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Consumer<Boolean> lockConsumer, Consumer<Throwable> errorConsumer) {
        boolean isLocked = false;
        try {
            isLocked = tryLock(lockKey, waitTime, leaseTime, unit);
            lockConsumer.accept(isLocked);
        } catch (Throwable e) {
            errorConsumer.accept(e);
        } finally {
            unlock(isLocked, lockKey);
        }
    }

    /**
     * 检查锁是否被任何线程锁定
     *
     * @param lockKey 锁名称
     * @return 是否锁定
     */
    public static boolean isLocked(String lockKey) {
        return locker.isLocked(lockKey);
    }

    /**
     * 检查此锁是否由当前线程持有
     *
     * @param lockKey 锁名称
     * @return 是否持有
     */
    public static boolean isHeldByCurrentThread(String lockKey) {
        return locker.isHeldByCurrentThread(lockKey);
    }
}
