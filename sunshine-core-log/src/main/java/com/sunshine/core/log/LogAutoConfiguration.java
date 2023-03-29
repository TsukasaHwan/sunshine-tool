package com.sunshine.core.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sunshine.core.log.aspect.LogApiAspect;
import com.sunshine.core.log.event.ApiLogListener;
import com.sunshine.core.log.filter.TraceFilter;
import com.sunshine.core.log.model.LogApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
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
    @ConditionalOnMissingBean(LogApiAspect.class)
    public LogApiAspect logApiAspect(LogExecutor logExecutor) {
        return new LogApiAspect(logExecutor);
    }

    @Bean
    @ConditionalOnMissingBean(ApiLogListener.class)
    @ConditionalOnBean(value = LogApi.class, parameterizedContainer = BaseMapper.class)
    public ApiLogListener apiLogListener(ServerInfo serverInfo, BaseMapper<LogApi> logApiMapper) {
        return new ApiLogListener(serverInfo, logApiMapper);
    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterBean() {
        FilterRegistrationBean<TraceFilter> filter = new FilterRegistrationBean<>(new TraceFilter());
        filter.setOrder(WebFilterOrderEnum.TRACE_FILTER.getOrder());
        return filter;
    }
}
