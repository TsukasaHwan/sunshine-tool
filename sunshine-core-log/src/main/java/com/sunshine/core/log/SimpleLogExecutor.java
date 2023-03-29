package com.sunshine.core.log;

import com.sunshine.core.log.annotation.ApiLog;
import com.sunshine.core.log.event.ApiLogEvent;
import com.sunshine.core.log.model.LogApi;
import com.sunshine.core.log.util.LogAbstractUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.sunshine.core.tool.util.ObjectUtils;
import org.sunshine.core.tool.util.SpringUtils;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author Teamo
 * @since 2021/04/20
 */
public class SimpleLogExecutor implements LogExecutor {

    @Override
    public void execute(ProceedingJoinPoint point, ApiLog apiLog, long time) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();

        String value = apiLog.value();
        LogApi logApi = new LogApi();
        logApi.setMethodClass(className);
        logApi.setMethodName(methodName);
        logApi.setTime(String.valueOf(time));
        logApi.setTitle(value);

        if (ObjectUtils.isNotEmpty(principal) && StringUtils.isNotEmpty(principal.getUsername())) {
            logApi.setCreateBy(principal.getUsername());
        } else {
            logApi.setCreateBy("anonymous");
        }

        LogAbstractUtils.addRequestInfoToLog(WebUtils.getRequest(), logApi);
        Map<String, Object> event = Collections.singletonMap(EVENT_LOG, logApi);
        SpringUtils.publishEvent(new ApiLogEvent(event));
    }
}
