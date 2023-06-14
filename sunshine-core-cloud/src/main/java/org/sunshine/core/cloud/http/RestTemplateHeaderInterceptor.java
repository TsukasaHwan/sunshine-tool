package org.sunshine.core.cloud.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.sunshine.core.cloud.header.HttpHeadersContextHolder;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;

import java.io.IOException;

/**
 * RestTemplateHeaderInterceptor 传递Request header
 *
 * @author Teamo
 */
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final FeignHeadersProperties properties;

    public RestTemplateHeaderInterceptor(FeignHeadersProperties properties) {
        this.properties = properties;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = HttpHeadersContextHolder.get();
        // 考虑2中情况 1. RestTemplate 不是用 hystrix 2. 使用 hystrix
        if (headers == null) {
            headers = HttpHeadersContextHolder.toHeaders(properties);
        }
        if (headers != null && !headers.isEmpty()) {
            HttpHeaders httpHeaders = request.getHeaders();
            headers.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
        }
        return execution.execute(request, bytes);
    }
}
