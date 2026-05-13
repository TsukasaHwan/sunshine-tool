package org.sunshine.enums.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.sunshine.enums.core.mvc.MvcConfiguration;
import org.sunshine.enums.core.serializer.JsonEnumDeserializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * @author Teamo
 * @since 2025-05-13
 */
@AutoConfiguration
public class EnumsCoreAutoConfiguration {

    @Bean
    @ConditionalOnBean(JsonMapper.class)
    public JsonMapper jsonMapper() {
        SimpleModule simpleModule = new SimpleModule();
        // 为所有Enum类型注册自定义反序列化器
        simpleModule.addDeserializer(Enum.class, new JsonEnumDeserializer());

        return JsonMapper.builder()
                .addModule(simpleModule)
                .build();
    }

    @Bean
    public JacksonJsonHttpMessageConverter jacksonJsonHttpMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonHttpMessageConverter(jsonMapper);
    }

    @Bean
    @ConditionalOnClass(WebMvcConfigurer.class)
    public MvcConfiguration mvcConfiguration() {
        return new MvcConfiguration();
    }
}
