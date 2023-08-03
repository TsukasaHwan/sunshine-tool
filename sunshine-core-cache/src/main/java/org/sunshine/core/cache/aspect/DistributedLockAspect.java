package org.sunshine.core.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.Assert;
import org.sunshine.core.cache.annotation.DistributedLock;
import org.sunshine.core.cache.redisson.util.RedissonLockUtils;
import org.sunshine.core.tool.util.ReflectionUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/6/9
 */
@Aspect
public class DistributedLockAspect {

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint pjp, DistributedLock distributedLock) throws Throwable {
        Class<?> targetClass = pjp.getTarget().getClass();
        Signature signature = pjp.getSignature();
        String methodName = signature.getName();
        Class<?>[] parameterTypes = ((MethodSignature) signature).getMethod().getParameterTypes();
        Method method = targetClass.getMethod(methodName, parameterTypes);
        Object[] args = pjp.getArgs();
        final String lockName = getLockName(method, args);
        return lock(pjp, lockName, distributedLock);
    }

    private String getLockName(Method method, Object[] args) {
        Assert.notNull(method, "The method must not be null!");

        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        String lockName = annotation.name();
        String param = annotation.param();

        if (StringUtils.isEmpty(lockName)) {
            if (args.length > 0) {
                if (StringUtils.isNotEmpty(param)) {
                    Object arg;
                    if (annotation.argNum() > 0) {
                        arg = args[annotation.argNum() - 1];
                    } else {
                        arg = args[0];
                    }
                    lockName = String.valueOf(getParam(arg, param));
                } else if (annotation.argNum() > 0) {
                    lockName = args[annotation.argNum() - 1].toString();
                }
            }
        }

        Assert.hasText(lockName, "Can't get or generate lockName accurately!");

        String prefix = annotation.prefix();
        String suffix = annotation.suffix();
        String separator = annotation.separator();
        StringBuffer lName = new StringBuffer();
        if (StringUtils.isNotEmpty(prefix)) {
            lName.append(prefix).append(separator);
        }
        lName.append(lockName);
        if (StringUtils.isNotEmpty(suffix)) {
            lName.append(separator).append(suffix);
        }
        return lName.toString();
    }

    /**
     * 从方法参数获取数据
     *
     * @param param 参数
     * @param arg   方法的参数数组
     * @return 变量
     */
    private Object getParam(Object arg, String param) {
        if (StringUtils.isEmpty(param) || arg == null) {
            return null;
        }
        return ReflectionUtils.getPropertyValue(arg, param);
    }

    private Object lock(ProceedingJoinPoint pjp, final String lockName, DistributedLock distributedLock) throws Throwable {
        boolean tryLock = distributedLock.tryLock();
        if (tryLock) {
            return tryLock(pjp, distributedLock, lockName);
        } else {
            return lock(pjp, lockName);
        }
    }

    /**
     * 普通锁
     *
     * @param pjp      连接点
     * @param lockName 锁名称
     * @return {Object}
     */
    private Object lock(ProceedingJoinPoint pjp, final String lockName) throws Throwable {
        try {
            RedissonLockUtils.lock(lockName);
            return proceed(pjp);
        } finally {
            if (RedissonLockUtils.isHeldByCurrentThread(lockName)) {
                RedissonLockUtils.unlock(lockName);
            }
        }
    }

    /**
     * 尝试锁
     *
     * @param pjp             连接点
     * @param distributedLock 分布式锁参数
     * @param lockName        锁名称
     * @return {Object}
     */
    private Object tryLock(ProceedingJoinPoint pjp, DistributedLock distributedLock, final String lockName) throws Throwable {
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();
        boolean lock = false;
        try {
            lock = RedissonLockUtils.tryLock(lockName, waitTime, leaseTime, timeUnit);
            if (lock) {
                return proceed(pjp);
            }
        } finally {
            RedissonLockUtils.unlock(lock, lockName);
        }
        return null;
    }

    private Object proceed(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }
}
