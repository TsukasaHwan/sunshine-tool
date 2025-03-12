package org.sunshine.core.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.sunshine.core.cache.annotation.DistributedLock;
import org.sunshine.core.cache.redisson.util.RedissonLockUtils;
import org.sunshine.core.tool.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Teamo
 * @since 2020/6/9
 */
@Aspect
public class DistributedLockAspect {

    private static final Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Around("@annotation(org.sunshine.core.cache.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributedLock distributedLock = ClassUtils.getAnnotation(method, DistributedLock.class);

        String lockKey = buildLockKey(pjp, method, distributedLock);
        return executeWithLock(pjp, distributedLock, lockKey);
    }

    /**
     * 构建分布式锁的key
     *
     * @param pjp             ProceedingJoinPoint
     * @param method          Method
     * @param distributedLock DistributedLock
     * @return String
     */
    private String buildLockKey(ProceedingJoinPoint pjp, Method method, DistributedLock distributedLock) {
        StringBuilder sb = new StringBuilder(distributedLock.value());

        if (distributedLock.key() != null && !distributedLock.key().isBlank()) {
            EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding()
                    .withRootObject(pjp.getTarget())
                    .withInstanceMethods()
                    .build();

            setMethodParameters(context, method, pjp.getArgs());

            String dynamicPart = PARSER.parseExpression(distributedLock.key())
                    .getValue(context, String.class);
            sb.append(dynamicPart);
        }
        return sb.toString();
    }

    /**
     * 设置方法参数
     *
     * @param context EvaluationContext
     * @param method  Method
     * @param args    Object[]
     */
    private void setMethodParameters(EvaluationContext context, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isNamePresent()) {
                context.setVariable(param.getName(), args[i]);
            }
        }
    }

    /**
     * 执行分布式锁
     *
     * @param pjp             ProceedingJoinPoint
     * @param distributedLock DistributedLock
     * @param lockKey         String
     * @return Object
     * @throws Throwable Throwable
     */
    private Object executeWithLock(ProceedingJoinPoint pjp, DistributedLock distributedLock, String lockKey) throws Throwable {
        if (distributedLock.tryLock()) {
            return handleTryLock(pjp, distributedLock, lockKey);
        } else {
            return handleBlockingLock(pjp, lockKey);
        }
    }

    /**
     * 处理尝试获取锁
     *
     * @param pjp             ProceedingJoinPoint
     * @param distributedLock DistributedLock
     * @param lockKey         String
     * @return Object
     * @throws Throwable Throwable
     */
    private Object handleTryLock(ProceedingJoinPoint pjp, DistributedLock distributedLock, String lockKey) throws Throwable {
        boolean locked = false;
        try {
            locked = RedissonLockUtils.tryLock(
                    lockKey,
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );

            if (locked) {
                return pjp.proceed();
            }
            logger.warn("Failed to acquire lock: {}", lockKey);
            return null;

        } finally {
            RedissonLockUtils.unlock(locked, lockKey);
        }
    }

    /**
     * 处理阻塞获取锁
     *
     * @param pjp     ProceedingJoinPoint
     * @param lockKey String
     * @return Object
     * @throws Throwable Throwable
     */
    private Object handleBlockingLock(ProceedingJoinPoint pjp, String lockKey) throws Throwable {
        try {
            RedissonLockUtils.lock(lockKey);
            return pjp.proceed();
        } finally {
            if (RedissonLockUtils.isHeldByCurrentThread(lockKey)) {
                RedissonLockUtils.unlock(lockKey);
            }
        }
    }
}
