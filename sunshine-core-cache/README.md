# ✨sunshine-core-cache

## *💎*模块简介

缓存相关功能模块，集成了SpringRedis，SpringCache，Redisson

## *💫*使用说明

1. **@[DistributedLock](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fannotation%2FDistributedLock.java)
   分布式锁注解，以及分布式锁[RedissonLockUtils](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fcache%2Fredisson%2Futil%2FRedissonLockUtils.java)
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
       public class Test {
         
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
               RedissionLockUtils.lock("");
               // 尝试锁
               RedissionLockUtils.tryLock("", (isLocked) -> {
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

   ```java
      /**
       * 继承AbstractRedisStreamConfiguration简化配置
       * 使用applyListenerContainer()方法配置监听容器
       */
      @Configuration(proxyBeanMethods = false)
      public class RedisStreamConfiguration extends AbstractRedisStreamConfiguration {
   
          public RedisStreamConfiguration(RedisConnectionFactory redisConnectionFactory,
                                          ThreadPoolTaskExecutor threadPoolTaskExecutor,
                                          RedisClient redisClient) {
              super(redisConnectionFactory, threadPoolTaskExecutor, redisClient);
          }
   
          /**
           * 消息1监听容器
           *
           * @param test1StreamListener 消息监听器
           * @return StreamMessageListenerContainer
           */
          @Bean(initMethod = "start", destroyMethod = "stop")
          public StreamMessageListenerContainer<String, ObjectRecord<String, OrderMessage>> orderExpiredConsumerListener(
                  Test1StreamListener test1StreamListener) {
              return applyListenerContainer(Stream.TEST1, Duration.ofSeconds(1L), 10, Test1.class, test1StreamListener);
          }
      }
   
      /**
       * Stream消息队列
       * 实现RedisStreamKey接口定义流信息
       */
      public enum Stream implements RedisStreamKey {
   
          /**
           * test1消息队列
           */
          TEST1("test1", "sunshine", "test1-consumer");
   
          private final String stream;
   
          private final String group;
   
          private final String consumer;
   
          Stream(String stream, String group, String consumer) {
              this.stream = stream;
              this.group = group;
              this.consumer = consumer;
          }
   
          @Override
          public String stream() {
              return this.stream;
          }
   
          @Override
          public String group() {
              return this.group;
          }
   
          @Override
          public String consumer() {
              return this.consumer;
          }
      }
   
      @Slf4j
      @Component
      public class Test1StreamListener extends AbstractStreamListener<Test1> {
   
          protected Test1StreamListener(RedisClient redisClient) {
              super(Test1.class, redisClient);
          }
   
          @Override
          protected RedisStreamKey redisStreamKey() {
              return Stream.TEST1;
          }
   
          @Override
          public void onMessage(ObjectRecord<String, Test1> message) {
              Test1 test1 = message.getValue();
              // do something
          }
   
          /**
           * 定时清理已消费的stream
           */
          @Scheduled(cron = "0 0 0/1 * * ?")
          public void clearTest1Stream() {
              this.trim(500);
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
   }
   ```
   可定义多个，更多使用方法请看源码