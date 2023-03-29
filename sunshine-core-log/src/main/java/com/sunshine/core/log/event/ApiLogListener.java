package com.sunshine.core.log.event;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunshine.core.log.LogExecutor;
import com.sunshine.core.log.model.LogApi;
import com.sunshine.core.log.util.LogAbstractUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.sunshine.core.tool.config.ServerInfo;

import java.util.Map;

/**
 * @author Teamo
 * @since 2021/06/02
 */
public class ApiLogListener {

    private final ServerInfo serverInfo;

    private final BaseMapper<LogApi> baseMapper;

    public ApiLogListener(ServerInfo serverInfo, BaseMapper<LogApi> baseMapper) {
        this.serverInfo = serverInfo;
        this.baseMapper = baseMapper;
    }

    @Async
    @Order
    @EventListener(ApiLogEvent.class)
    public void saveApiLog(ApiLogEvent event) {
        @SuppressWarnings("unchecked")
        Map<String, Object> source = (Map<String, Object>) event.getSource();
        LogApi logApi = (LogApi) source.get(LogExecutor.EVENT_LOG);
        LogAbstractUtils.addOtherInfoToLog(logApi, serverInfo);
        baseMapper.insert(logApi);
    }
}
