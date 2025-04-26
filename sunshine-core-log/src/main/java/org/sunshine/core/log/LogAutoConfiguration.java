package org.sunshine.core.log;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.log.aspect.LogOperationAspect;
import org.sunshine.core.log.event.OperateLogListener;
import org.sunshine.core.log.filter.TraceFilter;
import org.sunshine.core.tool.config.ServerInfo;
import org.sunshine.core.tool.enums.WebFilterOrderEnum;

/**
 * @author Teamo
 * @since 2023/01/13
 */
@AutoConfiguration
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogExecutor logExecutor() {
        return new SimpleLogExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public LogOperationAspect operateLogAspect(LogExecutor logExecutor) {
        return new LogOperationAspect(logExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    public OperateLogListener apiLogListener(ServerInfo serverInfo) {
        return new OperateLogListener(serverInfo);
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterBean() {
        FilterRegistrationBean<TraceFilter> filter = new FilterRegistrationBean<>(new TraceFilter());
        filter.setOrder(WebFilterOrderEnum.TRACE_FILTER.getOrder());
        return filter;
    }
}
