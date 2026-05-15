package org.sunshine.core.cloud;

import com.alibaba.cloud.sentinel.feign.SentinelFeign;
import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.BlockExceptionHandler;
import com.alibaba.fastjson2.JSON;
import feign.Feign;
import feign.RequestInterceptor;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.sunshine.core.cloud.header.FeignRequestInterceptor;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;
import org.sunshine.core.tool.api.response.Result;

import java.nio.charset.StandardCharsets;

/**
 * @author Teamo
 * @since 2023/6/14
 */
@AutoConfiguration(before = SentinelFeignAutoConfiguration.class)
@EnableConfigurationProperties(FeignHeadersProperties.class)
@ConditionalOnClass({ServletRequest.class, ServletResponse.class})
public class CloudAutoConfiguration {

    private final FeignHeadersProperties feignHeadersProperties;

    public CloudAutoConfiguration(FeignHeadersProperties feignHeadersProperties) {
        this.feignHeadersProperties = feignHeadersProperties;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "feign.sentinel.enabled", havingValue = "true")
    public Feign.Builder feignSentinelBuilder(RequestInterceptor requestInterceptor) {
        return SentinelFeign.builder().requestInterceptor(requestInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public BlockExceptionHandler blockExceptionHandler() {
        return (request, response, resourceName, e) -> {
            // Return 429 (Too Many Requests) by default.
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().print(JSON.toJSONString(Result.fail(e.getMessage())));
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor requestInterceptor() {
        return new FeignRequestInterceptor(feignHeadersProperties);
    }
}
