package org.sunshine.core.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.sunshine.core.cache.annotation.DistributedLock;
import org.sunshine.core.cache.redisson.util.RedissonLockUtils;
import org.sunshine.core.tool.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

/**
 * @author Teamo
 * @since 2020/6/9
 */
@Aspect
public class DistributedLockAspect {

    @Around("@annotation(org.sunshine.core.cache.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributedLock distributedLock = ClassUtils.getAnnotation(method, DistributedLock.class);
        String value = distributedLock.value();
        StringBuffer sb = new StringBuffer(value);

        String key = distributedLock.key();
        if (key != null && !key.isBlank()) {
            StandardEvaluationContext context = new StandardEvaluationContext(pjp.getTarget());
            String[] parameterNames = getParameterNames(method);
            if (parameterNames != null) {
                Object[] args = pjp.getArgs();
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            ExpressionParser parser = new SpelExpressionParser();
            sb.append(parser.parseExpression(key).getValue(context, String.class));
        }

        return lock(pjp, sb.toString(), distributedLock);
    }

    /**
     * 加锁操作
     *
     * @param pjp             连接点
     * @param lockName        锁名称
     * @param distributedLock 分布式锁参数
     * @return {Object}
     */
    private Object lock(ProceedingJoinPoint pjp, final String lockName, DistributedLock distributedLock) throws Throwable {
        if (distributedLock.tryLock()) {
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
            return pjp.proceed();
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
                return pjp.proceed();
            }
        } finally {
            RedissonLockUtils.unlock(lock, lockName);
        }
        return null;
    }

    /**
     * 获取方法参数名称
     *
     * @param method 方法
     * @return {String[]}
     */
    private String[] getParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] parameterNames = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                return null;
            }
            parameterNames[i] = param.getName();
        }
        return parameterNames;
    }
}
