package org.sunshine.core.cloud;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.sunshine.core.cloud.http.LbRestTemplate;
import org.sunshine.core.cloud.http.RestTemplateHeaderInterceptor;
import org.sunshine.core.cloud.http.client.OkHttp3ClientHttpRequestFactory;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;

import javax.net.ssl.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
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

    private static final Log LOG = LogFactory.getLog(FeignAutoConfiguration.class);

    private final FeignHeadersProperties feignHeadersProperties;

    public RestTemplateAutoConfiguration(FeignHeadersProperties feignHeadersProperties) {
        this.feignHeadersProperties = feignHeadersProperties;
    }

    /**
     * okhttp3 链接池配置
     *
     * @param httpClientProperties httpClient配置
     * @return okhttp3.ConnectionPool
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
    public okhttp3.ConnectionPool httpClientConnectionPool(FeignHttpClientProperties httpClientProperties) {
        int maxTotalConnections = httpClientProperties.getMaxConnections();
        long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return new ConnectionPool(maxTotalConnections, timeToLive, ttlUnit);
    }

    /**
     * 配置OkHttpClient
     *
     * @param connectionPool       链接池配置
     * @param httpClientProperties httpClient配置
     * @return OkHttpClient
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient httpClient(
            okhttp3.ConnectionPool connectionPool,
            FeignHttpClientProperties httpClientProperties) {
        boolean followRedirects = httpClientProperties.isFollowRedirects();
        int connectTimeout = httpClientProperties.getConnectionTimeout();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (httpClientProperties.isDisableSslValidation()) {
            disableSsl(builder);
        }
        return builder
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

    private void disableSsl(okhttp3.OkHttpClient.Builder builder) {
        try {
            X509TrustManager disabledTrustManager = new DisableValidationTrustManager();
            TrustManager[] trustManagers = new TrustManager[1];
            trustManagers[0] = disabledTrustManager;
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            SSLSocketFactory disabledSslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(disabledSslSocketFactory, disabledTrustManager);
            builder.hostnameVerifier(new TrustAllHostnames());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOG.warn("Error setting SSLSocketFactory in OKHttpClient", e);
        }
    }

    /**
     * A {@link X509TrustManager} that does not validate SSL certificates.
     */
    static class DisableValidationTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    /**
     * A {@link HostnameVerifier} that does not validate any hostnames.
     */
    static class TrustAllHostnames implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }

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
