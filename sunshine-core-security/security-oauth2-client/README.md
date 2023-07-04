# ✨security-oauth2-client

## *💎*模块简介

由于spring-security-oauth2客户端配置复杂，此模块减少配置。

当前模块只支持WebFlux。

## 💫使用说明

1. **添加yml配置**

   - ```yaml
     oauth2:
       client: 
         # 禁止访问路径
         forbidden-paths: 
         
     spring:
       security:
         oauth2:
           resourceserver:
             jwt:
               # 认证服务jwks路径
               jwk-set-uri: http://localhost:8081/oauth2/jwks
     ```

2. **启动类上添加注解@[EnableOAuth2Client](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fclient%2FEnableOAuth2Client.java)即可开启OAUTH2客户端**

