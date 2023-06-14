package org.sunshine.core.cloud.header;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;

/**
 * @author Teamo
 * @since 2023/6/14
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    private final FeignHeadersProperties properties;

    public FeignRequestInterceptor(FeignHeadersProperties properties) {
        this.properties = properties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpHeaders headers = HttpHeadersContextHolder.get();
        // 考虑2中情况 1. RestTemplate 不是用 hystrix 2. 使用 hystrix
        if (headers == null) {
            headers = HttpHeadersContextHolder.toHeaders(properties);
        }
        if (headers != null && !headers.isEmpty()) {
            headers.forEach((key, values) -> values.forEach(value -> requestTemplate.header(key, value)));
        }
    }
}
