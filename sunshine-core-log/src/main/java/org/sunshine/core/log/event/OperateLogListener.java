package org.sunshine.core.log.event;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    private final BaseMapper<OperateLog> baseMapper;

    public OperateLogListener(ServerInfo serverInfo, BaseMapper<OperateLog> baseMapper) {
        this.serverInfo = serverInfo;
        this.baseMapper = baseMapper;
    }

    @Async
    @Order
    @EventListener(OperateLogEvent.class)
    public void saveOperateLogLog(OperateLogEvent event) {
        if (baseMapper == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        OperateLog operateLog = (OperateLog) source.get(LogExecutor.EVENT_LOG);
        LogAbstractUtils.addOtherInfoToLog(operateLog, serverInfo);
        baseMapper.insert(operateLog);
    }
}
