# ✨sunshine-core-common

## *💎*项目简介

sunshine-cache、sunshine-log、sunshine-tool以及集成了springdoc的公共模块

## 💫使用说明

1. **SpringMVC集成fastjon2.x、配置了支持jdk8 Time入参、以及跨域过滤器**
2. **默认开启了异步调用支持只需在方法中使用@Async注解即可异步调用**
3. **默认开启了全局异常拦截，如需手动抛出异常则使用 [ExceptionCast](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcommon%2Fexception%2FExceptionCast.java).cast();进行抛出**
4. 