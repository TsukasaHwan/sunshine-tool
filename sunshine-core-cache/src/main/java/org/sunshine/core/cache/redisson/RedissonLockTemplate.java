package org.sunshine.core.cache.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.sunshine.core.tool.util.Exceptions;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Teamo
 * @since 2025/3/13
 */
public class RedissonLockTemplate implements RedissonLockOperations, InitializingBean {

    private final RedissonClient redissonClient;

    public RedissonLockTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redissonClient == null) {
            throw new IllegalArgumentException("Property 'redissonClient' is required");
        }
    }

    @Override
    public void lock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.lock();
    }

    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.unlock();
    }

    @Override
    public void unlock(boolean isLocked, String key) {
        if (isLocked && isHeldByCurrentThread(key)) {
            this.unlock(key);
        }
    }

    @Override
    public void lock(String key, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(leaseTime, unit);
    }

    @Override
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock();
    }

    @Override
    public <R> R tryLock(String key, Function<Boolean, R> callback, Consumer<Throwable> exConsumer) {
        boolean isLocked = false;
        R r = null;
        try {
            isLocked = tryLock(key);
            r = callback.apply(isLocked);
        } catch (Throwable e) {
            exConsumer.accept(e);
        } finally {
            if (isLocked) {
                unlock(key);
            }
        }
        return r;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            RLock lock = redissonClient.getLock(key);
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            // 恢复中断状态
            Thread.currentThread().interrupt();
            throw Exceptions.unchecked(e);
        }
    }

    @Override
    public <R> R tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, Function<Boolean, R> callback, Consumer<Throwable> exConsumer) {
        boolean isLocked = false;
        R r = null;
        try {
            isLocked = tryLock(key, waitTime, leaseTime, unit);
            r = callback.apply(isLocked);
        } catch (Throwable e) {
            exConsumer.accept(e);
        } finally {
            unlock(isLocked, key);
        }
        return r;
    }

    @Override
    public boolean isLocked(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.isLocked();
    }

    @Override
    public boolean isHeldByCurrentThread(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.isHeldByCurrentThread();
    }
}
