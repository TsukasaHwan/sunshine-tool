# ✨sunshine-core-cloud

## *💎*模块简介

SpringCloud组件扩展模块

## 💫使用说明

1. **已自动集成Sentinel、Naocs、Config以及Feign**

2. **扩展Feign组件本地调用支持透传header**

   - ```yaml
     feign: 
       # 要透传的header
       allowed: 
       	- 'X-Real-IP'
       	- 'X-Forwarded-For'
       	- 'Authorization'
     ```

     上述配置为当前端请求带有X-Real-IP、X-Forwarded-For、Authorization时，如使用Fegin进行调用微服务时，会传递上述配置的header

   - **支持负载均衡的LbRestTemplate，并且支持http://服务名进行服务调用**

     ```java
     // 注入支持负载均衡的RestTemplate
     @Autowired
     private LbRestTempalte lbRestTempalte;
     ```

