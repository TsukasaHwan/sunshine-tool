# ✨sunshine-core-captcha

## *💎*模块简介

滑块验证码模块

## 💫使用说明

1. **引入依赖**

   ```xml
   <dependency>
       <groupId>org.sunshine</groupId>
       <artifactId>sunshine-core-captcha</artifactId>
       <version>2.0-SNAPSHOT</version>
   </dependency>
   ```

   

2. **配置yml，更多请查看https://ajcaptcha.beliefteam.cn/captcha-doc/captchaDoc/java.html#springboot**

   - ```yaml
     aj:
       captcha:
         # 右下角水印文字
         water-mark: 我的水印
         # # 验证码类型default两种都实例化
         type: default
         # # 缓存local/redis... 已自动集成redis
         cache-type: redis
         # 滑动验证，底图路径，不配置将使用默认图片
         # 支持全路径
         jigsaw: classpath:images/jigsaw
         pic-click: classpath:images/pic-click
     ```

   - **编写controller**

     ```java
     @Autowired
     private CaptchaService captchaService;
     
     @PostMapping("/captcha")
     public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
         String browserInfo = WebUtils.getIP() + request.getHeader(WebUtils.USER_AGENT_HEADER);
         data.setBrowserInfo(browserInfo);
         return captchaService.get(data);
     }
     
     @PostMapping("/check")
     public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
         String browserInfo = WebUtils.getIP() + request.getHeader(WebUtils.USER_AGENT_HEADER);
         data.setBrowserInfo(browserInfo);
         return captchaService.check(data);
     }
     ```

   - **请求接口**

     - 获取验证码接口：http://:/captcha/get

       请求参数：

       ```json
       {
           "captchaType": "blockPuzzle" //验证码类型 clickWord
           "clientUid": "唯一标识"  //客户端UI组件id,组件初始化时设置一次，UUID（非必传参数）
       }
       ```

       响应参数：

       ```json
       {
           "repCode": "0000",
           "repData": {
               "originalImageBase64": "底图base64",
               "point": {    //默认不返回的，校验的就是该坐标信息，允许误差范围
                   "x": 205,
                   "y": 5
               },
               "jigsawImageBase64": "滑块图base64",
               "token": "71dd26999e314f9abb0c635336976635", //一次校验唯一标识
               "secretKey": "16位随机字符串", //aes秘钥，开关控制，前端根据此值决定是否加密
               "result": false,
               "opAdmin": false
           },
           "success": true,
           "error": false
       }
       ```

     - 核对验证码接口接口：http://:/captcha/check

       请求参数：

       ```json
       {
       	 "captchaType": "blockPuzzle",
       	 "pointJson": "QxIVdlJoWUi04iM+65hTow==",  //aes加密坐标信息
       	 "token": "71dd26999e314f9abb0c635336976635"  //get请求返回的token
       }
       ```

       响应参数：

       ```json
       {
           "repCode": "0000",
           "repData": {
               "captchaType": "blockPuzzle",
               "token": "71dd26999e314f9abb0c635336976635",
               "result": true,
               "opAdmin": false
           },
           "success": true,
           "error": false
       }
       ```

       

