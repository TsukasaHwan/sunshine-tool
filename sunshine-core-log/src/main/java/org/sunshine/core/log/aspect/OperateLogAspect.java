package org.sunshine.core.log.aspect;

import cn.hutool.core.date.SystemClock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.sunshine.core.log.LogExecutor;
import org.sunshine.core.log.annotation.OperateLog;

/**
 * @author Teamo
 * @since 2021/04/20
 */
@Aspect
public class OperateLogAspect {

    private final LogExecutor logExecutor;

    public OperateLogAspect(LogExecutor logExecutor) {
        this.logExecutor = logExecutor;
    }

    @Around("@annotation(operateLog)")
    public Object doAfter(ProceedingJoinPoint point, OperateLog operateLog) throws Throwable {
        long beginTime = SystemClock.now();
        Object proceed = point.proceed();
        long time = SystemClock.now() - beginTime;
        logExecutor.execute(point, operateLog, time);
        return proceed;
    }
}
