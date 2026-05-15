# ✨sunshine-core-mybatis

## *💎*模块简介

mybatis-plus扩展模块

## 💫使用说明

1. **支持myabtis-plus代码生成**

   - 使用 [MyBatisPlusFastAutoGenerator](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Futil%2FMyBatisPlusFastAutoGenerator.java).generate()按提示步骤进行代码生成

     ```java
     public static void main(String[] args) {
         MyBatisPlusFastAutoGenerator.generate("url", "username", "password");
     }
     ```

2. **内置默认自动填充功能（默认创建时间、修改时间、逻辑删除字段名为：gmtCreate，gmtModified，isDelete）**

   - 自定义自动填充字段功能，创建 [AutoFillMetaObjectHandler](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fhandler%2FAutoFillMetaObjectHandler.java) bean

     ```java
     @Bean
     public MetaObjectHandler metaObjectHandler() {
         AutoFillMetaObjectHandler autoFillMetaObjectHandler = new AutoFillMetaObjectHandler();
         // 创建时间字段名
         autoFillMetaObjectHandler.setCreateFiledName("gmtCreate");
         // 修改时间字段名
         autoFillMetaObjectHandler.setModifyFiledName("gmtModified");
         // 逻辑删除字段名
         autoFillMetaObjectHandler.setIsDeleteFiledName("isDelete");
         return autoFillMetaObjectHandler;
     }
     ```

   - 在实体类中添加mybatis-plus注解，即可实现自动填充

     ```java
     @Getter
     @Setter
     @TableName("test")
     public class Test {
         /**
          * 创建时间
          */
         @TableField(fill = FieldFill.INSERT)
         private LocalDateTime gmtCreate;
     
         /**
          * 修改时间
          */
         @TableField(fill = FieldFill.INSERT_UPDATE)
         private LocalDateTime gmtModified;
         
         /**
          * 删除标识（0正常 1删除）
          */
         @TableLogic
         @TableField(fill = FieldFill.INSERT)
         private Boolean isDelete;
     }
     ```

3. **支持动态表名**

   - 创建 [DynamicTableNameHandler](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fextension%2Fplugin%2FDynamicTableNameHandler.java) bean

     ```java
     @Bean
     public DynamicTableNameHandler dynamicTableNameHandler() {
         DynamicTableNameHandler dynamicTableNameHandler = new DynamicTableNameHandler();
         // 表名分隔符，默认为_
         dynamicTableNameHandler.setDelimiter("_");
         return dynamicTableNameHandler;
     }
     ```

   - 在分表查询的位置，使用 [DynamicTableSuffixContextHolder](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fextension%2Fplugin%2FDynamicTableSuffixContextHolder.java).setTableNameSuffix()设置表后缀，即可实现分表查询

4. **支持SQL层面的批量新增。由于mybatis-plus的batchSave为代码层面的循环批量新增，存在性能问题所以提供sql层面的批量新增**

   - Mapper接口继承 [BatchBaseMapper](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fmapper%2FBatchBaseMapper.java)

     ```java
     @Repository
     public interface TestMapper extends BatchBaseMapper<Test> {
     
     }
     ```

   - 注入TestMapper，使用insertBatchSomeColumn即可实现sql层面的批量新增

     ```java
     @Autowired
     private TestMapper testMapper;
     
     public void batchSave(List<Test> tests) {
         testMapper.insertBatchSomeColumn(tests);
     }
     ```

   - service层也可以使用。service接口继承 [BaseService](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fservice%2FBaseService.java) ，实现类继承 [BaseServiceImpl](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Fservice%2Fimpl%2FBaseServiceImpl.java) 

     ```java
     public interface TestService extends BaseService<Test> {
         
     }
     ```

     ```java
     public class TestServiceImpl extends BaseServiceImpl<TestMapper, Test> implements TestService {
         public void batchSave(List<Test> tests) {
             this.saveBatchSomeColumn(tests);
         }
     }
     ```

5. **Ognl表达式工具**

   -  [Ognl](src%2Fmain%2Fjava%2Forg%2Fsunshine%2Fcore%2Fmp%2Futil%2FOgnl.java) 工具类，主要是为了在ognl表达式访问静态方法时可以减少长长的类名称编写 Ognl访问静态方法的表达式：@class@method(args)

     ```xml
     <if test="@org.sunshine.core.mybatis.util.Ognl@isNotEmpty(userId)">
     	and user_id = #{userId}
     </if>
     ```

     更多方法请查看Ognl源码

