package org.sunshine.core.cloud;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.sunshine.core.cloud.http.LbRestTemplate;
import org.sunshine.core.cloud.http.RestTemplateHeaderInterceptor;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Http RestTemplateHeaderInterceptor 配置
 *
 * @author Teamo
 */
@AutoConfiguration
@ConditionalOnClass(okhttp3.OkHttpClient.class)
@EnableConfigurationProperties(FeignHeadersProperties.class)
public class RestTemplateAutoConfiguration {

    private final FeignHeadersProperties feignHeadersProperties;

    public RestTemplateAutoConfiguration(FeignHeadersProperties feignHeadersProperties) {
        this.feignHeadersProperties = feignHeadersProperties;
    }

    /**
     * okhttp3 链接池配置
     *
     * @param connectionPoolFactory 链接池配置
     * @param httpClientProperties  httpClient配置
     * @return okhttp3.ConnectionPool
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
    public okhttp3.ConnectionPool httpClientConnectionPool(
            FeignHttpClientProperties httpClientProperties,
            OkHttpClientConnectionPoolFactory connectionPoolFactory) {
        int maxTotalConnections = httpClientProperties.getMaxConnections();
        long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
    }

    /**
     * 配置OkHttpClient
     *
     * @param httpClientFactory    httpClient 工厂
     * @param connectionPool       链接池配置
     * @param httpClientProperties httpClient配置
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient httpClient(
            OkHttpClientFactory httpClientFactory,
            okhttp3.ConnectionPool connectionPool,
            FeignHttpClientProperties httpClientProperties) {
        boolean followRedirects = httpClientProperties.isFollowRedirects();
        int connectTimeout = httpClientProperties.getConnectionTimeout();
        return httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(followRedirects)
                .connectionPool(connectionPool)
                .build();
    }

    @Bean
    public RestTemplateHeaderInterceptor requestHeaderInterceptor() {
        return new RestTemplateHeaderInterceptor(feignHeadersProperties);
    }

    /**
     * 普通的 RestTemplate，不透传请求头，一般只做外部 http 调用
     *
     * @param httpClient OkHttpClient
     * @return RestTemplate
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(okhttp3.OkHttpClient httpClient) {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
        configMessageConverters(restTemplate.getMessageConverters());
        return restTemplate;
    }

    /**
     * 支持负载均衡的 LbRestTemplate
     *
     * @param httpClient  OkHttpClient
     * @param interceptor RestTemplateHeaderInterceptor
     * @return LbRestTemplate
     */
    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean(LbRestTemplate.class)
    public LbRestTemplate lbRestTemplate(okhttp3.OkHttpClient httpClient, RestTemplateHeaderInterceptor interceptor) {
        LbRestTemplate lbRestTemplate = new LbRestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
        lbRestTemplate.setInterceptors(Collections.singletonList(interceptor));
        configMessageConverters(lbRestTemplate.getMessageConverters());
        return lbRestTemplate;
    }

    private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased, JSONReader.Feature.SupportArrayToBean);
        config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue);
        List<MediaType> fastMediaTypes = new ArrayList<>(1);
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonConverter.setFastJsonConfig(config);
        converters.add(fastJsonConverter);
    }
}
