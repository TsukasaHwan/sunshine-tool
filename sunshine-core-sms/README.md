# ✨sunshine-core-sms

## *💎*模块简介

短信、邮件通信模块。

短信支持阿里云SMS，腾讯SMS。

## 💫使用说明

1. **SMS使用**

   - 引入相关厂商sdk

     ```xml
     <!-- 阿里云SDK -->
     <dependency>
         <groupId>com.aliyun</groupId>
         <artifactId>tea-openapi</artifactId>
         <version>0.2.8</version>
     </dependency>
     <dependency>
         <groupId>com.aliyun</groupId>
         <artifactId>dysmsapi20170525</artifactId>
         <version>2.0.23</version>
     </dependency>
     
     <!-- 腾讯云SDK -->
     <dependency>
         <groupId>com.tencentcloudapi</groupId>
         <artifactId>tencentcloud-sdk-java</artifactId>
         <version>3.1.786</version>
     </dependency>
     ```

   - 配置yml

     ```yaml
     sms:
        enabled: true
        # 短信客户端类型：ALIYUN（阿里云），TENCENT（腾讯云）
        client-type: ALIYUN
        key-id: 'key-id'
        key-secret: 'key-secret'
        # 腾讯云无需配置，阿里云必须配置，有关信息请看各短信厂商文档
        endpoint: 'endpoint'
        # 阿里云无需配置，腾讯云必须配置，有关信息请看各短信厂商文档
        regionId: 'regionId'
     ```

   - 注入[SmsTemplate](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fsms%2Ftemplate%2FSmsTemplate.java)Bean，使用sendSms方法即可发送短信消息

     ```java
     @Autowired
     private SmsTemplate smsTemplate;
     
     public void testSms() {
         // 阿里云
         SmsSender.AliYun aliYun = SmsSender.AliYun.builder().templateParam("").outId("").build();
         SmsSender smsSender = SmsSender.builder().phoneNumbers("13888888888").signName("").templateCode("").build(aliYun);
         boolean isSuccess = smsTemplate.sendSms(smsSender);
     }
     ```

2. **邮件使用**

   - 引入SpringMail依赖

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-mail</artifactId>
     </dependency>
     ```

   - 注入[MailTemplate](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fsms%2Ftemplate%2FMailTemplate.java)Bean，发送邮件

     ```java
     @Autowired
     private MailTemplate mailTemplate;
     ```

     支持文件，和普通邮件。相关使用请看源码