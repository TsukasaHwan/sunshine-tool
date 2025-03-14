package org.sunshine.core.cache.redisson;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Redisson分布式锁操作接口，提供基于key的加锁、解锁及状态查询功能
 *
 * @author Teamo
 * @since 2025/3/13
 */
public interface RedissonLockOperations {

    /**
     * 阻塞式加锁（永久持有直到手动解锁）
     *
     * @param key 分布式锁的键名
     */
    void lock(String key);

    /**
     * 释放指定key对应的锁
     *
     * @param key 要解锁的键名
     */
    void unlock(String key);

    /**
     * 条件释放锁（根据锁定状态决定是否解锁）
     *
     * @param isLocked 当前是否处于锁定状态的标记
     * @param key      要解锁的键名
     */
    void unlock(boolean isLocked, String key);

    /**
     * 加锁并设置自动释放时间
     *
     * @param key       分布式锁的键名
     * @param leaseTime 锁自动释放时间（正数）
     * @param unit      时间单位
     */
    void lock(String key, long leaseTime, TimeUnit unit);

    /**
     * 非阻塞式尝试加锁（立即返回结果）
     *
     * @param key 要尝试加锁的键名
     * @return true-加锁成功，false-加锁失败
     */
    boolean tryLock(String key);

    /**
     * 尝试加锁并执行回调（无返回值版本）
     * 通过将Function适配为Consumer来实现结果回调，忽略最终返回值
     *
     * @param key        要尝试加锁的键名
     * @param callback   加锁结果回调函数（参数为加锁是否成功）
     * @param exConsumer 异常处理回调函数
     */
    default void tryLockWithoutResult(String key, Consumer<Boolean> callback, Consumer<Throwable> exConsumer) {
        tryLock(key, isLocked -> {
            callback.accept(isLocked);
            return null;
        }, exConsumer);
    }

    /**
     * 尝试加锁并执行带返回值的回调
     *
     * @param key        要尝试加锁的键名
     * @param callback   加锁结果处理函数（接收加锁状态并返回业务结果）
     * @param exConsumer 异常处理回调函数
     * @param <R>        回调函数的返回值类型
     * @return 回调函数执行后的返回值
     */
    <R> R tryLock(String key, Function<Boolean, R> callback, Consumer<Throwable> exConsumer);

    /**
     * 尝试获取指定键的锁
     *
     * @param key       锁的唯一标识键
     * @param waitTime  获取锁的最大等待时间（超过该时间未获得锁则失败）
     * @param leaseTime 锁的持有时间（自动释放时间）
     * @param unit      时间单位（用于waitTime和leaseTime）
     * @return true表示成功获取锁，false表示失败
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 异步尝试获取锁并通过回调返回结果（无返回值版本）
     *
     * @param key        锁的唯一标识键
     * @param waitTime   获取锁的最大等待时间
     * @param leaseTime  锁的持有时间
     * @param unit       时间单位
     * @param callback   获取锁结果的回调函数（接收Boolean类型结果）
     * @param exConsumer 异常处理回调函数
     */
    default void tryLockWithoutResult(String key, long waitTime, long leaseTime, TimeUnit unit, Consumer<Boolean> callback, Consumer<Throwable> exConsumer) {
        tryLock(key, waitTime, leaseTime, unit, isLocked -> {
            callback.accept(isLocked);
            return null;
        }, exConsumer);
    }

    /**
     * 尝试获取锁并通过回调函数返回自定义结果（泛型版本）
     *
     * @param key        锁的唯一标识键
     * @param waitTime   获取锁的最大等待时间
     * @param leaseTime  锁的持有时间
     * @param unit       时间单位
     * @param callback   成功获取锁后的回调函数（接收Boolean状态并返回自定义结果）
     * @param exConsumer 异常处理回调函数
     * @param <R>        回调函数返回结果的类型
     * @return 回调函数返回的自定义结果
     */
    <R> R tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, Function<Boolean, R> callback, Consumer<Throwable> exConsumer);

    /**
     * 检查指定键的锁是否已被持有（不区分持有线程）
     *
     * @param key 锁的唯一标识键
     * @return true表示锁已被占用，false表示未上锁
     */
    boolean isLocked(String key);

    /**
     * 检查当前线程是否持有指定键的锁
     *
     * @param key 锁的唯一标识键
     * @return true表示锁由当前线程持有，false表示未持有或锁不存在
     */
    boolean isHeldByCurrentThread(String key);
}
