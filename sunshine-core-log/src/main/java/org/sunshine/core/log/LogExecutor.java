package org.sunshine.core.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.sunshine.core.log.annotation.LogOperation;

/**
 * @author Teamo
 * @since 2021/04/20
 */
public interface LogExecutor {
    /**
     * 事件
     */
    String EVENT_LOG = "log";

    /**
     * 执行日志逻辑
     *
     * @param point        切点
     * @param logOperation {@link LogOperation}
     * @param time         执行时长（毫秒）
     */
    void execute(ProceedingJoinPoint point, LogOperation logOperation, long time);
}
