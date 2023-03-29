package org.sunshine.core.tool.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunshine.core.tool.util.SpringUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //是否自动重连
                .retryOnConnectionFailure(true)
                //连接池
                .connectionPool(new ConnectionPool(300, 20, TimeUnit.SECONDS))
                //设置连接超时
                .connectTimeout(20, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(30, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(30, TimeUnit.SECONDS)
                //允许重定向
                .followRedirects(true)
                .build();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        restTemplate.setRequestFactory(factory);

        StringHttpMessageConverter converter = (StringHttpMessageConverter) restTemplate.getMessageConverters().get(1);
        converter.setDefaultCharset(StandardCharsets.UTF_8);

        return restTemplate;
    }
}
