package org.sunshine.core.common.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.sunshine.core.tool.api.code.CommonCode;

import java.util.Arrays;

/**
 * open api配置
 *
 * @author Teamo
 * @since 2023-03-03
 */
public interface OpenApiConfiguration {

    /**
     * 配置信息
     *
     * @return GroupedOpenApiConfig
     */
    GroupedOpenApiConfig groupedOpenApiConfig();

    /**
     * 默认实现
     *
     * @param openApiCustomiser   OpenApiCustomiser
     * @param operationCustomizer OperationCustomizer
     * @return GroupedOpenApi
     */
    @Bean
    default GroupedOpenApi groupedOpenApi(OpenApiCustomiser openApiCustomiser, OperationCustomizer operationCustomizer) {
        GroupedOpenApiConfig groupedOpenApiConfig = groupedOpenApiConfig();
        Assert.notNull(groupedOpenApiConfig, "grouped OpenApi config must not be null!");

        return GroupedOpenApi.builder()
                .group(groupedOpenApiConfig.getGroupName())
                .pathsToMatch(groupedOpenApiConfig.getPaths())
                .addOperationCustomizer(operationCustomizer)
                .packagesToScan(groupedOpenApiConfig.getBasePackage())
                .addOpenApiCustomiser(openApiCustomiser)
                .build();
    }

    /**
     * 默认实现
     *
     * @return OpenAPI
     */
    @Bean
    default OpenAPI openAPI() {
        GroupedOpenApiConfig groupedOpenApiConfig = groupedOpenApiConfig();
        Assert.notNull(groupedOpenApiConfig, "grouped OpenApi config must not be null!");

        return new OpenAPI()
                .info(groupedOpenApiConfig.getInfo());
    }

    /**
     * 默认响应消息
     *
     * @return OpenApiCustomiser
     */
    @Bean
    default OpenApiCustomiser openApiCustomiser() {
        return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
                .forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
                    Arrays.stream(CommonCode.values())
                            .forEach(commonCode -> responses.put(String.valueOf(commonCode.code()), new ApiResponse().description(commonCode.msg())));
                });
    }

    /**
     * 默认请求头
     *
     * @return OperationCustomizer
     */
    @Bean
    default OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            // TODO
            Schema stringSchema = new StringSchema()._default("Bearer ").name("Authorization").description("请求接口Authorization");
            Parameter headerParameter = new HeaderParameter().name("Authorization").description("请求接口Authorization").schema(stringSchema);
            return operation.addParametersItem(headerParameter);
        };
    }

    class GroupedOpenApiConfig {
        private final String groupName;

        private final String[] paths;

        private final String[] basePackage;

        private final Info info;

        GroupedOpenApiConfig(String groupName, String[] paths, String[] basePackage, Info info) {
            this.groupName = groupName;
            this.paths = paths;
            this.basePackage = basePackage;
            this.info = info;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getGroupName() {
            return groupName;
        }

        public String[] getPaths() {
            return paths;
        }

        public String[] getBasePackage() {
            return basePackage;
        }

        public Info getInfo() {
            return info;
        }

        public static class Builder {
            private String groupName;
            private String[] paths;
            private String[] basePackage;
            private Info info;

            Builder() {
            }

            public Builder groupName(String groupName) {
                this.groupName = groupName;
                return this;
            }

            public Builder paths(String... paths) {
                this.paths = paths;
                return this;
            }

            public Builder basePackage(String... basePackage) {
                this.basePackage = basePackage;
                return this;
            }

            public Builder info(Info info) {
                this.info = info;
                return this;
            }

            public GroupedOpenApiConfig build() {
                return new GroupedOpenApiConfig(this.groupName, this.paths, this.basePackage, this.info);
            }
        }
    }
}
