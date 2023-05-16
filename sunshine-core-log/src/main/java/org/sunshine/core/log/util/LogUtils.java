package org.sunshine.core.log.util;

import org.sunshine.core.log.LogExecutor;
import org.sunshine.core.log.event.OperateLogEvent;
import org.sunshine.core.log.model.OperateLog;
import org.sunshine.core.tool.util.SpringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author Teamo
 * @since 2023/5/16
 */
public class LogUtils extends LogAbstractUtils {

    /**
     * 添加系统日志
     *
     * @param className  类名称
     * @param methodName 方法名称
     * @param title      日志标题
     * @param time       操作时长
     * @param params     参数
     */
    public static void addLog(String username, String className, String methodName, String title, long time, String params) {
        OperateLog log = new OperateLog();
        log.setMethodClass(className);
        log.setMethodName(methodName);
        log.setTime(String.valueOf(time));
        log.setTitle(title);
        log.setCreateBy(username);
        addRequestInfoToLog(WebUtils.getRequest(), log);
        log.setParams(params);
        Map<String, Object> event = Collections.singletonMap(LogExecutor.EVENT_LOG, log);
        SpringUtils.publishEvent(new OperateLogEvent(event));
    }
}
