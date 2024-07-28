package org.sunshine.core.tool.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunshine.core.tool.util.SpringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Teamo
 * @since 2020/10/23
 */
@AutoConfiguration
public class ToolConfiguration implements WebMvcConfigurer {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory);

        restTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });

        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(RestClient.class)
    public RestClient restClient(ClientHttpRequestFactory clientHttpRequestFactory) {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(clientHttpRequestFactory)
                .messageConverters(converters -> converters.forEach(converter -> {
                    if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
                    }
                }))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setDefaultConnectionConfig(ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(5000))
                .setSocketTimeout(Timeout.ofMilliseconds(10000))
                .build());
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);

        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(10000))
                .setResponseTimeout(Timeout.ofMilliseconds(10000))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}
