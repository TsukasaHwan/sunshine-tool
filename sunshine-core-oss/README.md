# ✨sunshine-core-oss

## *💎*模块简介

对象存储模块，目前支持阿里云、minio

## 💫使用说明

1. **使用**

   - 引入依赖，选择一种aliyun或minio

     ```xml
     <dependency>
         <groupId>com.aliyun.oss</groupId>
         <artifactId>aliyun-sdk-oss</artifactId>
         <version>3.16.0</version>
     </dependency>
     
     <dependency>
         <groupId>io.minio</groupId>
         <artifactId>minio</artifactId>
         <version>8.5.4</version>
     </dependency>
     ```
   
   - 配置yml

     ```yaml
     oss:
       enabled: true
       # client-type: minio
       client-type: aliyun
       endpoint: endpoint
       bucket-name: bucketName
       access-key: accessKey
       secret-key: secretKey
       # 自定义参数 可不配置
       args: 
         # 大小限制
         contentLengthRange: 10485760
         # 过期时间
         expireTime: 3600
     ```
   
   - 配置上传文件名称规则，默认以upload/yyyy/MM/dd/{UUID}.{extName}此格式进行文件上传。如需自定义请看以下方式：
   
     ```java
     public class CustomRule implements OssRule {
         
         @Override
         public String bucketName(String bucketName) {
             return bucketName;
         }
     
         @Override
         public String fileName(String originalFilename) {
             // 自定义文件名规则
             return "";
         }
     }
     
     /**
      * 注入bean
      */
     @Bean
     public OssRule ossRule() {
         return new CustomRule();
     }
     ```
   
   - 注入 [AliOssTemplate](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Foss%2FAliOssTemplate.java) bean即可使用阿里云对象存储进行文件上传
   
     ```java
     @Autowired
     private AliOssTemplate aliOssTemplate;
     ```
   
   - 如若使用minio，注入 [MinioTemplate](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Foss%2FMinioTemplate.java) bean即可使用minio对象存储进行文件上传
   
     ```java
     @Autowired
     private MinioTemplate minioTemplate;
     ```
   
     