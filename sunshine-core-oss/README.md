# ✨sunshine-core-oss

## *💎*模块简介

对象存储模块，目前仅支持阿里云OSS

## 💫使用说明

1. **阿里云OSS使用**

   - 配置yml

     ```yaml
     aliyun:
       oss:
         enabled: true
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

     