# ✨security-oauth2-resource-server

## *💎*模块简介

由于spring-security-oauth2资源服务配置复杂，此模块减少配置。

## 💫使用说明

1. **配置yml**

   - ```yaml
     oauth2: 
       resource: 
         server: 
           # 允许访问路径
           permit-all-paths:
             - /
             - /*.html
             - /*/*.html
             - /*/*.css
             - /*/*.js
             - /profile/**
             - /swagger-resources/**
             - /webjars/**
             - /*/api-docs/**
             
     spring:
       security:
         oauth2:
           resourceserver:
             jwt:
               # 认证服务jwks路径
               jwk-set-uri: http://localhost:8081/oauth2/jwks
     ```
     当然，默认开启了方法级别的注解权限控制，如需使用动态的接口放行可使用注解@PermitAll进行控制。也可自定义注解，只需继承[AbstractSecurityAnnotationSupport](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Fsupport%2FAbstractSecurityAnnotationSupport.java)类实现hasAnnotation方法并注册Bean到Spring容器即可。

2. **在启动类上面增加注解@[EnableOAuth2ResourceServer](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fresource%2Fserver%2FEnableOAuth2ResourceServer.java)即可开启OAUTH2资源服务**

3. **Security OAuth2相关信息获取可使用[OAuth2SecurityUtils](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Futil%2FOAuth2SecurityUtils.java)**