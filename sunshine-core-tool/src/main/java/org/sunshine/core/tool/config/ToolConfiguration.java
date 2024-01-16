package org.sunshine.core.tool.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunshine.core.tool.util.SpringUtils;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
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
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //是否自动重连
                .retryOnConnectionFailure(true)
                //连接池
                .connectionPool(new ConnectionPool(200, 900L, TimeUnit.SECONDS))
                //设置连接超时
                .connectTimeout(2, TimeUnit.SECONDS)
                //设置读超时
                .readTimeout(30, TimeUnit.SECONDS)
                //设置写超时
                .writeTimeout(30, TimeUnit.SECONDS)
                //允许重定向
                .followRedirects(true)
                .build();
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        restTemplate.setRequestFactory(factory);

        restTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });

        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(WebClient.class)
    public WebClient webClient() {
        // 连接池
        ConnectionProvider provider = ConnectionProvider
                .builder("custom")
                // 最大连接数
                .maxConnections(200)
                // 最大空闲时间
                .maxIdleTime(Duration.ofSeconds(20))
                // 连接的最大生命周期时间
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .pendingAcquireMaxCount(400)
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .responseTimeout(Duration.ofSeconds(6))
                .keepAlive(true)
                .compress(true)
                // 连接成功
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(30))
                        .addHandlerLast(new WriteTimeoutHandler(30)))
                // 每次请求后执行flush，防止服务器主动断开连接
                .doAfterRequest((httpClientRequest, connection) -> {
                    connection.channel().alloc().buffer().release();
                    connection.channel().flush();
                    connection.channel().pipeline().flush();
                });

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
