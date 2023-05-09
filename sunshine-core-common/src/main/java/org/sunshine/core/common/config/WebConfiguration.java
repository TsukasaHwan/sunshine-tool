package org.sunshine.core.common.config;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Teamo
 * @since 2019/7/10
 */
@AutoConfiguration
public class WebConfiguration implements WebMvcConfigurer {

    private final Converter<String, LocalDateTime> localDateTimeConverter = (StringToLocalDateTimeConverter) source -> {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(source, df);
    };

    private final Converter<String, LocalDate> localDateConverter = (StringToLocalDateConverter) source -> {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(source, df);
    };

    private final Converter<String, LocalTime> localTimeConverter = (StringToLocalTimeConverter) source -> {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(source, df);
    };

    /**
     * 集成fastJson
     *
     * @param converters 添加消息转换器的列表（最初是一个空列表）
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setReaderFeatures(JSONReader.Feature.FieldBased);
        config.setWriterFeatures(
                JSONWriter.Feature.WriteNullStringAsEmpty,
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty
        );
        config.setWriterFilters(new DataMaskJsonFilter());
        //处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>(2);
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastMediaTypes.add(MediaType.valueOf("application/vnd.spring-boot.actuator.v2+json"));
        fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonConverter.setFastJsonConfig(config);
        converters.add(0, fastJsonConverter);
        converters.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(localDateTimeConverter);
        registry.addConverter(localDateConverter);
        registry.addConverter(localTimeConverter);
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

    interface StringToLocalDateTimeConverter extends Converter<String, LocalDateTime> {
    }

    interface StringToLocalDateConverter extends Converter<String, LocalDate> {
    }

    interface StringToLocalTimeConverter extends Converter<String, LocalTime> {
    }
}
