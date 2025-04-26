package org.sunshine.core.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.sunshine.core.log.annotation.LogOperation;
import org.sunshine.core.log.event.OperateLogEvent;
import org.sunshine.core.log.model.OperateLog;
import org.sunshine.core.log.util.LogAbstractUtils;
import org.sunshine.core.tool.util.SpringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author Teamo
 * @since 2021/04/20
 */
public class SimpleLogExecutor implements LogExecutor {

    @Override
    public void execute(ProceedingJoinPoint point, LogOperation logOperation, long time) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();

        String value = logOperation.value();
        OperateLog operateLog = new OperateLog();
        operateLog.setMethodClass(className);
        operateLog.setMethodName(methodName);
        operateLog.setTime(String.valueOf(time));
        operateLog.setTitle(value);

        if (principal instanceof UserDetails userDetails) {
            operateLog.setCreateBy(userDetails.getUsername());
        }

        LogAbstractUtils.addRequestInfoToLog(WebUtils.getRequest(), operateLog);
        Map<String, Object> event = Collections.singletonMap(EVENT_LOG, operateLog);
        SpringUtils.publishEvent(new OperateLogEvent(event));
    }
}
