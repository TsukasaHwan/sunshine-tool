package org.sunshine.core.common.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunshine.core.common.exception.ResponseExceptionHandler;
import org.sunshine.core.tool.datamask.DataMaskJsonFilter;
import org.sunshine.core.tool.enums.WebFilterOrderEnum;
import org.sunshine.core.tool.util.DateUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2019/7/10
 */
@AutoConfiguration
public class DefaultWebConfiguration implements WebMvcConfigurer {

    /**
     * 集成fastJson
     *
     * @param converters 添加消息转换器的列表（最初是一个空列表）
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased, JSONReader.Feature.SupportArrayToBean);
        config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue);
        config.setWriterFilters(new DataMaskJsonFilter());
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>(2);
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastMediaTypes.add(MediaType.valueOf("application/vnd.spring-boot.actuator.v2+json"));
        fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonConverter.setFastJsonConfig(config);
        fastJsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(0, fastJsonConverter);
        converters.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(StringToLocalDateTimeConverter.INSTANCE);
        registry.addConverter(StringToLocalDateConverter.INSTANCE);
        registry.addConverter(StringToLocalTimeConverter.INSTANCE);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean(CorsConfigurationSource corsConfigurationSource) {
        FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        filter.setOrder(WebFilterOrderEnum.CORS_FILTER.getOrder());
        return filter;
    }

    @Bean
    public ResponseExceptionHandler responseExceptionHandler() {
        return new ResponseExceptionHandler();
    }

    enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        INSTANCE;

        @Override
        public LocalDateTime convert(String source) {
            return StringUtils.isBlank(source) ? null : DateUtils.parseDateTime(source);
        }
    }

    enum StringToLocalDateConverter implements Converter<String, LocalDate> {
        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return StringUtils.isBlank(source) ? null : DateUtils.parseDate(source);
        }
    }

    enum StringToLocalTimeConverter implements Converter<String, LocalTime> {
        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return StringUtils.isBlank(source) ? null : DateUtils.parseTime(source);
        }
    }
}
