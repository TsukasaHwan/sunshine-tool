package org.sunshine.core.log.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sunshine.core.log.LogExecutor;
import org.sunshine.core.log.annotation.LogOperation;

/**
 * @author Teamo
 * @since 2021/04/20
 */
@Aspect
public class LogOperationAspect {

    private final LogExecutor logExecutor;

    public LogOperationAspect(LogExecutor logExecutor) {
        this.logExecutor = logExecutor;
    }

    @Around("@annotation(logOperation)")
    public Object doAfter(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object proceed = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        logExecutor.execute(point, logOperation, time);
        return proceed;
    }
}
