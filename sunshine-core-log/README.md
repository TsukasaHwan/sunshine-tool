# ✨sunshine-core-log

## *💎*模块简介

日志模块，安全框架使用的是Spring-Security

## 💫使用说明

1. **支持方法级别的异步日志记录**

   - 先使用document/sql/log下的[sys_operate_log.sql](..%2Fdocument%2Fsql%2Flog%2Fsys_operate_log.sql) 建表语句进行表建立

   - 建立mapper操作类

     ```java
     @Repository
     public interface OperateLogMapper extends BaseMapper<OperateLog> {
     
     }
     ```

      [OperateLog](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Flog%2Fmodel%2FOperateLog.java)已经内置，如需扩展继承即可

   - 在方法上面使用注解@[OperateLog](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Flog%2Fannotation%2FOperateLog.java) 即可记录操作日志

     ```java
     @DeleteMapping("/user/{id}")
     @OperateLog(value = "用户删除")
     public Result<Void> delete(@PathVariable("id") Long id) {
         return Result.ok();
     }
     ```

2. **请求接口链路追踪，支持子线程追踪**

   - 在logback-spring.xml下的日志格式中增加[%X{requestId}]，即可实现接口链路追踪

3. **支持开发以及测试环境的接口日志打印**

   - 默认识别spring.profile，当profile为dev或test时，自动打印日志