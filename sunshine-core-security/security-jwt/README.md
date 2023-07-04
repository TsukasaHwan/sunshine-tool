# ✨security-jwt

## *💎*模块简介

Spring-Security集成JWT

## 💫使用说明

1. **使用[RSAUtils](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Futil%2FRSAUtils.java).genKeyPair()生成公钥私钥**

   - ```java
     public static void main(String[] args) throws NoSuchAlgorithmException {
         RSAUtils.genKeyPair();
     }
     ```

2. **配置yml**

   - ```yaml
     jwt:
       security:
         # token请求头
       	header: Authorization
         # token过期时间
         expires-in: 2H
         # 刷新token资源路径
         refresh-token-path: /users/refreshToken
         # 刷新token claim
         refresh-token-claim: refresh_token
         # 刷新token过期时间
         refresh-token-expires-in: 1D
         # 无需token可访问资源
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
         # 公钥私钥路径
         secret:
           public-key: classpath:app.pub
           private-key: classpath:app.key
         # 登出路径
         logout-path: /logout
     ```
     当然，默认开启了方法级别的注解权限控制，如需使用动态的接口放行可使用注解@PermitAll进行控制。也可自定义注解，只需继承[AbstractSecurityAnnotationSupport](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Fsupport%2FAbstractSecurityAnnotationSupport.java)类实现hasAnnotation方法并注册Bean到Spring容器即可。

3. **实现[JwtUserDetailsService](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fjwt%2Fuserdetails%2FJwtUserDetailsService.java)接口，编写用户查询逻辑，用户实体类可继承[JwtUserDetails](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fjwt%2Fuserdetails%2FJwtUserDetails.java)也可实现UserDetails接口**

   - ```java
     @Service
     public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {
     
         @Override
         public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
             LoginUser loginUser = new LoginUser();
             return loginUser;
         }
     
         /**
          * 注销成功调用逻辑,username有可能为null,当token错误或过期时username为null
          *
          * @param username 用户名(有可能为null)
          */
         @Override
         public void onLogoutSuccess(String username) {
             JwtUserDetailsService.super.onLogoutSuccess(username);
         }
     }
     
     @Getter
     @Setter
     public class LoginUser extends JwtUserDetails {
     
         private String username;
     
         private String password;
     
         private Set<GrantedAuthority> authorities;
     }
     ```

4. **在启动类上增加注解@[EnableJwtSecurity](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fjwt%2FEnableJwtSecurity.java)即可启用JWT安全**

5. **Security相关信息获取可使用[SecurityUtils](..%2Fsecurity-core%2Fsrc%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fcore%2Futil%2FSecurityUtils.java)**

6. **生成JWT token请使用工具类[JwtClaimsUtils](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fjwt%2Futil%2FJwtClaimsUtils.java)，返回JWT实体类可使用[Jwt](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fsecurity%2Fjwt%2FJwt.java)**
