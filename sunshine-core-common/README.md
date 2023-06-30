# ✨sunshine-core-common

## *💎*项目简介

sunshine-cache、sunshine-log、sunshine-tool以及集成了springdoc的公共模块

## 💫使用说明

1. **SpringMVC集成fastjon2.x、配置了支持jdk8 Time入参、以及跨域过滤器**

2. **默认开启了异步调用支持只需在方法中使用@Async注解即可异步调用**

3. **默认开启了全局异常拦截，如需手动抛出异常则使用 [ExceptionCast](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcommon%2Fexception%2FExceptionCast.java).cast();进行抛出**

4. **默认集成了tool里面的knife4j和openAPI3**

   实现 [OpenApiConfiguration](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcommon%2Fopenapi%2FOpenApiConfiguration.java) 接口，knife4j扩展功能请查看knife4j官方文档

   - ```java
     @EnableKnife4j
     @Configuration(proxyBeanMethods = false)
     public class TestOpenApiConfiguration implements OpenApiConfiguration {
     
         @Override
         public GroupedOpenApiConfig groupedOpenApiConfig() {
             return GroupedOpenApiConfig.builder()
                     .basePackage("org.example.controller")
                     .paths("/**")
                     .groupName("example")
                     .info(new Info()
                             .title("测试接口文档")
                             .description("测试接口文档")
                             .contact(new Contact().name("Teamo").url("https://gitee.com/TsukasaHwan").email("785415580@qq.com"))
                             .version("v1.0"))
                     .build();
         }
     }
     ```

5. **默认集成了spring-boot-starter-validation，由于校验的时候无法保证顺序和无法判别是添加或更新，所以增加了自定义校验组 [ValidateGroup](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcommon%2Fgroup%2FValidateGroup.java)**

***更多功能请看源码···***