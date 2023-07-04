# ✨security-oauth2-authorization-server

## *💎*模块简介

由于spring-security-oauth2认证服务配置复杂，此模块减少配置。

OAUTH2.1实施文档删除了password模式，本模块增加了password模式

spring-security-oauth2认证服务默认使用JdbcTemplate，修改为mybatis-plus

## 💫使用说明

1. **执行document/sql/oauth2下的[oauth2.sql](..%2F..%2Fdocument%2Fsql%2Foauth2%2Foauth2.sql)建表语句**

2. **创建Mybatis-Plus数据库映射接口**

   - ```java
     @Repository
     public interface OAuth2AuthConsentMapper extends BaseMapper<OAuth2AuthConsent> {
     }
     
     @Repository
     public interface OAuth2AuthMapper extends BaseMapper<OAuth2Auth> {
     }
     
     @Repository
     public interface OAuth2ClientMapper extends BaseMapper<OAuth2Client> {
     }
     ```

     [OAuth2Auth](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fauthorization%2Fserver%2Fentity%2FOAuth2Auth.java)认证表，[OAuth2AuthConsent](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fauthorization%2Fserver%2Fentity%2FOAuth2AuthConsent.java)授权同意表，[OAuth2Client](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fauthorization%2Fserver%2Fentity%2FOAuth2Client.java)客户端表

3. **使用[RSAUtils](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Futil%2FRSAUtils.java)生成公钥私钥**

   - ```java
     public static void main(String[] args) throws NoSuchAlgorithmException {
         RSAUtils.genKeyPair();
     }
     ```

     保存至resources下

4. **增加yml配置**

   - ```yaml
     oauth2:
       authorization:
         server:
           secret:
             # 公钥
             public-key: classpath:app.pub
             # 私钥
             private-key: classpath:app.key
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
           # 认证确认页面，可不配置
           consent-page-uri: 
     
     spring: 
       security:
         oauth2:
           resourceserver:
             jwt:
               # 认证服务jwks路径
               jwk-set-uri: http://localhost:8081/oauth2/jwks
     ```

5. **编写UserDetailsService实现类，注册到Spring容器**
6. **启动类增加@[EnableOAuth2AuthorizationServer](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Foauth2%2Fauthorization%2Fserver%2FEnableOAuth2AuthorizationServer.java)注解，即可开启OAUTH2认证服务**