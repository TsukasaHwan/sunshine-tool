package com.sunshine.core.log.aspect;

import cn.hutool.core.date.SystemClock;
import com.sunshine.core.log.LogExecutor;
import com.sunshine.core.log.annotation.ApiLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Teamo
 * @since 2021/04/20
 */
@Aspect
public class LogApiAspect {

    private final LogExecutor logExecutor;

    public LogApiAspect(LogExecutor logExecutor) {
        this.logExecutor = logExecutor;
    }

    @Around("@annotation(apiLog)")
    public Object doAfter(ProceedingJoinPoint point, ApiLog apiLog) throws Throwable {
        long beginTime = SystemClock.now();
        Object proceed = point.proceed();
        long time = SystemClock.now() - beginTime;
        logExecutor.execute(point, apiLog, time);
        return proceed;
    }
}
