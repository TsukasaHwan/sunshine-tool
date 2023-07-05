# ✨sunshine-core-tool

## *💎*模块简介

核心工具包模块

## 💫使用说明

1. **api包定义接口返回规范**
   - 统一返回码请实现接口[ResultCode](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fcode%2FResultCode.java)，当然内部已经定义了一些返回码[AdminCode](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fcode%2FAdminCode.java)，[CommonCode](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fcode%2FCommonCode.java)。
   - 统一分页查询请使用[Query](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Frequest%2FQuery.java)类入参。
   - 统一接口结果返回请实现接口[Response](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fresponse%2FResponse.java)，当然内部已经定义了默认的结果返回[Result](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fresponse%2FResult.java)类。
   - 统一分页查询结果返回请使用[QueryResult](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Fresponse%2FQueryResult.java)。

2. **内置了[SpringUtils](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Futil%2FSpringUtils.java)可直接获取Spring容器中的Bean工具类。**

3. **内置了防御XSS攻击**

   - 配置yml

     ```yaml
     xss: 
       enabled: true
       # 要忽略XSS攻击的url
       skip-url: 
         - /
     ```

     经过上述配置即可开启XSS防御

4. **内置了数据脱敏（基于FastJson进行数据脱敏）**

   - 基于Spring的数据脱敏：

     - 在Spring消息转换器中添加FastJson消息转换器并添加数据脱敏过滤器

       ```java
       @Override
       public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
           FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
           FastJsonConfig config = new FastJsonConfig();
           config.setReaderFeatures(JSONReader.Feature.FieldBased, JSONReader.Feature.SupportArrayToBean);
           config.setWriterFeatures(JSONWriter.Feature.WriteMapNullValue);
           // 数据脱敏过滤器
           config.setWriterFilters(new DataMaskJsonFilter());
           //处理中文乱码问题
           List<MediaType> fastMediaTypes = new ArrayList<>(1);
           fastMediaTypes.add(MediaType.APPLICATION_JSON);
           fastJsonConverter.setSupportedMediaTypes(fastMediaTypes);
           fastJsonConverter.setFastJsonConfig(config);
           converters.add(0, fastJsonConverter);
           converters.add(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
       }
       ```

     - 在需要进行数据脱敏的实体类的字段上使用注解@[Sensitive](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fdatamask%2FSensitive.java)即可进行数据脱敏

       ```java
       @Data
       public class Test {
           @Sensitive(type = SensitiveType.PHONE)
           private String phone;
       }
       ```

   - 基于代码层面的数据脱敏

     - 使用[DataMask](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fdatamask%2FDataMask.java)手动进行数据脱敏

       ```java
       public static void main(String[] args) {
           String phone = "13888888888";
           String mask = DataMask.mask(phone);
           String maskPhone = DataMask.mask(phone, SensitiveType.PHONE);
           // 1**********
           System.out.println(mask);
           // 138*****888
           System.out.println(maskPhone);
       }
       ```

   - 可进行通用的配置，在resources下建立配置文件data_mask.properties，添加相应的字段名称即可统一进行数据脱敏

     ```properties
     NAME=
     PHONE=
     ID_CARD=
     BANKCARD=
     ADDRESS=
     EMAIL=
     CAPTCHA=
     PASSPORT=
     ACCOUNT=
     PASSWORD=
     ```

5. **基于easyexcel的excel导入数据**

   - 引入easyexcel依赖

     ```xml
     <dependency>
         <groupId>com.alibaba</groupId>
         <artifactId>easyexcel</artifactId>
         <version>3.3.0</version>
     </dependency>
     ```

     

   - 实现接口[ImportExcelHandler](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fexcel%2FImportExcelHandler.java)中的handle方法，处理对应的导入业务逻辑，在调用方法doConvert即可进行导入，默认为条数到达3000条时执行handle方法，当然你也可以自定义，更多方法请看上述类源码

6. **统一异常处理类[CustomException](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fexception%2FCustomException.java)，需配合sunshine-core-common模块进行使用**

7. **树状结构处理类**

   - 实体类实现接口[INode](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fnode%2FINode.java)。
   - 使用[ForestNodeMerger](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fnode%2FForestNodeMerger.java)森林节点Merger类的merge方法进行节点合并（*时间复杂度为**O(n^2)*），即可构建树状接口菜单。

8. **GIF验证码**

   - 使用[Captcha](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fsupport%2Fvcode%2FCaptcha.java)对象即可生成GIF验证码

     ```java
     public static void main(String[] args) {
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         Captcha captcha = new GifCaptcha(146, 33, 4);
         // 输出到字节数组流中
         captcha.out(os);
         System.out.println(captcha.text());
     }
     ```

9. **分页工具类[Condition](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fsupport%2FCondition.java)**

   - mybatis-plus

     - 入参为[Query](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Frequest%2FQuery.java)时可使用getPage方法转化成mybatis-plus中的分页查询对象

     - pageVo方法可以将mybatis-plus分页查询结果对象转换为vo对象

       ```java
       IPage<Test> page = this.page(Condition.getPage(query), wrapper);
       IPage<TestVo> pageVo = Condition.pageVo(page, TestVo::new);
       ```

       

   - jpa

     - 入参为[Query](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Frequest%2FQuery.java)时可使用getPageRequest方法转化成jpa中的分页查询对象

   - elasticsearch

     - 入参为[Query](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Ftool%2Fapi%2Frequest%2FQuery.java)时可使用getPageSearchSourceBuilder方法转化成es中带分页参数的查询对象

10. **内置了一些实用工具，如需了解请查看源码**



***最后Enjoy it😍***