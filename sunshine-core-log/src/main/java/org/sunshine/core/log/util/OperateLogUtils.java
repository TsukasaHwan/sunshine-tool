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
public class OperateLogUtils extends LogAbstractUtils {

    /**
     * 添加系统日志
     *
     * @param log {@link OperateLog}
     */
    public static void addOperateLog(OperateLog log) {
        addRequestInfoToLog(WebUtils.getRequest(), log);
        Map<String, Object> event = Collections.singletonMap(LogExecutor.EVENT_LOG, log);
        SpringUtils.publishEvent(new OperateLogEvent(event));
    }
}
