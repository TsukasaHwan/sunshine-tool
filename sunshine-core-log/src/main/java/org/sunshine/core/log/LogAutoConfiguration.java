package org.sunshine.core.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.log.aspect.OperateLogAspect;
import org.sunshine.core.log.event.OperateLogListener;
import org.sunshine.core.log.filter.TraceFilter;
import org.sunshine.core.log.model.OperateLog;
import org.sunshine.core.tool.config.ServerInfo;
import org.sunshine.core.tool.enums.WebFilterOrderEnum;

/**
 * @author Teamo
 * @since 2023/01/13
 */
@AutoConfiguration
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LogExecutor.class)
    public LogExecutor logExecutor() {
        return new SimpleLogExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(OperateLogAspect.class)
    public OperateLogAspect operateLogAspect(LogExecutor logExecutor) {
        return new OperateLogAspect(logExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(OperateLogListener.class)
    public OperateLogListener apiLogListener(ServerInfo serverInfo,
                                             @Autowired(required = false) BaseMapper<OperateLog> operateLogMapper) {
        // @ConditionalOnBean(value = OperateLog.class, parameterizedContainer = BaseMapper.class)失效
        return new OperateLogListener(serverInfo, operateLogMapper);
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterBean() {
        FilterRegistrationBean<TraceFilter> filter = new FilterRegistrationBean<>(new TraceFilter());
        filter.setOrder(WebFilterOrderEnum.TRACE_FILTER.getOrder());
        return filter;
    }
}
