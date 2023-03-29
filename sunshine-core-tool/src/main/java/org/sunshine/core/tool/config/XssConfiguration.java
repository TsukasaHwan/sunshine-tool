package org.sunshine.core.tool.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.sunshine.core.tool.enums.WebFilterOrderEnum;
import org.sunshine.core.tool.request.RequestFilter;
import org.sunshine.core.tool.request.RequestProperties;
import org.sunshine.core.tool.request.XssProperties;

import javax.servlet.DispatcherType;

/**
 * Xss配置类
 *
 * @author Teamo
 * @since 2020/9/17
 */
@AutoConfiguration
@ConditionalOnProperty(value = "xss.enabled", havingValue = "true")
@EnableConfigurationProperties({XssProperties.class, RequestProperties.class})
public class XssConfiguration {

    private final XssProperties xssProperties;
    private final RequestProperties requestProperties;

    public XssConfiguration(XssProperties xssProperties, RequestProperties requestProperties) {
        this.xssProperties = xssProperties;
        this.requestProperties = requestProperties;
    }

    /**
     * 全局过滤器
     */
    @Bean
    public FilterRegistrationBean<RequestFilter> xssFilterBean() {
        FilterRegistrationBean<RequestFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new RequestFilter(requestProperties, xssProperties));
        registration.addUrlPatterns("/*");
        registration.setOrder(WebFilterOrderEnum.XSS_FILTER.getOrder());
        return registration;
    }
}
