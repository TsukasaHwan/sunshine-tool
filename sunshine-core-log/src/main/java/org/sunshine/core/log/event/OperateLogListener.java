package org.sunshine.core.log.event;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.sunshine.core.log.LogExecutor;
import org.sunshine.core.log.model.OperateLog;
import org.sunshine.core.log.util.LogAbstractUtils;
import org.sunshine.core.tool.config.ServerInfo;

import java.util.Map;

/**
 * @author Teamo
 * @since 2021/06/02
 */
public class OperateLogListener {

    private final ServerInfo serverInfo;

    public OperateLogListener(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Async
    @Order
    @EventListener(OperateLogEvent.class)
    public void saveOperateLogLog(OperateLogEvent event) {
        @SuppressWarnings("unchecked")
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        OperateLog operateLog = (OperateLog) source.get(LogExecutor.EVENT_LOG);
        LogAbstractUtils.addOtherInfoToLog(operateLog, serverInfo);
        SqlHelper.execute(OperateLog.class, baseMapper -> baseMapper.insert(operateLog));
    }
}
