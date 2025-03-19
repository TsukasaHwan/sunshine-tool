# ✨sunshine-core-cache

## *💎*模块简介

缓存相关功能模块，集成了SpringRedis，SpringCache，Redisson

## *💫*使用说明

1. **@[DistributedLock](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fannotation%2FDistributedLock.java)
   分布式锁注解，以及分布式锁模板类[RedissonLockTemplate](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fredisson%2FRedissonLockTemplate.java)
   使用**
   
    - SpringRedis开启Redisson(单机模式)
      ```yaml
      spring:
         redis:
         host: 127.0.0.1
         database: 0
         password: password
         lettuce:
           pool:
             max-active: 8
             max-wait: 5S
             max-idle: 5
             min-idle: 0
             timeout: 3S
             port: 6379
         # 启用redisson
         redisson:
           enable: true
      ```
    - ```java
       /**
        * 分布式锁注解
        * value属性作为锁键值，而key属性可以使用SPEL表达式，如#testPojo.id，如若设置了key属性，则锁的键值为value + key
        * tryLock是否使用尝试锁默认为false
        * 在指定时间内如果未获取到锁则不执行方法，最长等待时间默认为5秒，锁释放时间默认为10秒
        */
       @Service
       public class Test {

           private final RedissonLockTemplate redissonLockTemplate;
      
           public Test(RedissonLockTemplate redissonLockTemplate) {
               this.redissonLockTemplate = redissonLockTemplate;
           }
         
           /**
            * 如果testPojo的id为1，则锁的名称为'lock:1'
            *
            * @param testPojo
            * @return
            */
           @DistributedLock(value = "lock:", key = "#testPojo.id", tryLock = true)
           public Result<Void> lock(TestPojo testPojo) {
               return Result.ok();
           }
         
           public Result<Void> codeLock() {
               // 普通锁
               redissonLockTemplate.lock("");
               // 尝试锁
               redissonLockTemplate.tryLockWithoutResult("", (isLocked) -> {
                   if (isLocked) {
                       // do something
                   }
               });
               // ... 更多请看源码
               return Result.ok();
           }
         
           @Data
           public static class TestPojo {
               private Long id;
           }
       }
      ```
   
2. **@[RateLimit](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fannotation%2FRateLimit.java)限流注解**
   - ```java
     public class Test {
     
         /**
          * 以方法名称作为key在10秒之类只能请求一次
          * 可以修改limitKeyType指定key值的限定规则支持方法名称和ip
          * limitType为限流类型支持固定窗口、滑动窗口
          *
          * @return
          */
         @RateLimit(prefix = "limit:", limit = 1, windowSize = 10, keyType = RateLimit.RateLimitKeyType.METHOD, type = RateLimit.RateLimitType.FIXED_WINDOW)
         public Result<Void> rateLimit() {
             return Result.ok();
         }
     }
     ```

3. **[RedisClient](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2FRedisClient.java)简化Spring Data Redis中的RedisTemplate操作只需注入即可使用**

4. **[RedisKey](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2FRedisKey.java)接口，由于在高并发时使用字符串构建key值会出现线程安全问题，所以使用SpEL表达式+Guava ImmutableMap进行构建**

   - ```java
      @Getter
      public enum TestRedisKey implements RedisKey {
       
          /**
           * 测试
           */
          TEST("test:#{#id}", 3600L);
       
          private final String template;
       
          private final Long expire;
       
          TestRedisKey(String template, Long expire) {
              this.template = template;
              this.expire = expire;
          }
      }
       
      public class Test {
          public static void main(String[] args) {
              // test:1
              String testKey = TestRedisKey.TEST.buildKey(ImmutableMap.<String, String>builder().put("id", "1").build());
          }
      }
     ```

5. **Redis5.0 Stream新特性支持，自动处理无效的stream，以及处理死信问题**
   ```yaml
      spring:
        stream:
          # 批量处理消息的数量。默认为10。
          batch-size: 10
          # 死信任务执行cron表达式，默认30秒执行一次扫描
          dead-letter-task-cron: '30 * * * * ?'
          thread-pool:
            # 是否启用线程池（默认true）。
            enable: true
            # 核心线程数。默认值为为核心处理器的数量。
            core-pool-size: 5
            # 最大线程数。默认值为核心线程数的两倍。
            max-pool-size: 10
            # 队列容量。默认：500。
            queue-capacity: 300
            # 线程存活时间（单位：秒）。默认60秒。
            keep-alive-seconds: 60
            # 是否在关机时等待计划任务完成，不中断正在运行的任务和执行队列中的所有任务。默认true。
            wait-for-jobs-to-complete-on-shutdown: true
            # 线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁。（单位：秒）。默认120秒。
            await-termination-seconds: 120
            # 线程名称的前缀。默认值为：redis-stream-thread-
            thread-name-prefix: 'redis-stream-thread-'
            # 拒绝策略。默认值为CallerRunsPolicy。
            rejected-execution-handler: java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
   ```

   ```java
      @Slf4j
      @Component
      public class Test1StreamListener extends AbstractStreamListener<Test1> {
   
          @Override
          protected String getStreamKey() {
              return "test1";
          }
   
          @Override
          public void onMessage(Test1 message) {
              // do something
          }
   
          @Override
          public TrimConfig getTrimConfig() {
              TrimConfig trimConfig = new TrimConfig();
              // 流修剪执行cron表达式（默认每10分钟执行一次）
              trimConfig.setCron("0 */10 * * * ?");
              // 保留最新的500条（默认500条）
              trimConfig.setMaxCount(500L);
              return trimConfig;
          }
   
          @Override
          public DeadLetterConfig getDeadLetterConfig() {
              DeadLetterConfig deadLetterConfig = new DeadLetterConfig();
              // 是否启用专用调度器（默认false。注意只有启用了专用调度器cron参数才生效，否则共用一个调度器）
              deadLetterConfig.setEnableDedicatedScheduler();
              // 死信任务执行cron表达式（默认每30秒执行一次）
              deadLetterConfig.setCron();
              // 当消息超过多少时间后当作死信处理（默认5分钟。可单独配置）
              deadLetterConfig.setPendingProcessingTimeout();
              return deadLetterConfig;
          }
      }
   ```

6. **默认开启SpringCache的二级缓存，使用caffeine作为本地缓存，Redis作为远程缓存。使用SpringCache注解即可。**

7. **可扩展的Redisson延迟队列**
   
   - 定义消息类
   ```java
   public class UserMessage implements Serializable {
        private String username;
   
        private String password;
   
        public String getUsername() {
            return username;
        }
   
        public void setUsername(String username) {
            this.username = username;
        }
   
        public String getPassword() {
            return password;
        }
   
        public void setPassword(String password) {
            this.password = password;
        }
   }
   ```
   - 实现[DelayedQueueListener](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fredisson%2Fqueue%2FDelayedQueueListener.java)接口
   ```java
   @Component
   public class Test implements DelayedQueueListener<UserMessage> {
   
       @Override
       public String delayedQueueKey() {
           //队列key
           return "test";
       }
   
       @Override
       public void consume(UserMessage message) throws Exception {
           // 执行消费逻辑
       }
   
       @Override
       public ThreadPoolExecutor getThreadPoolExecutor() {
          // 是否为当前延迟队列使用线程池，默认不配置
          return DelayedQueueListener.super.getThreadPoolExecutor();
       }
   
       @Override
       public void handleException(Exception e) {
          // 处理异常逻辑，当出现异常调用此方法，默认不做任何处理
          DelayedQueueListener.super.handleException(e);
       }
   }
   ```
   可定义多个，更多使用方法请看源码