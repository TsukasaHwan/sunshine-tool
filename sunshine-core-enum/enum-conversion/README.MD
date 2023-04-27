# spring-boot-starter-jpa-conversion

## 介绍
轻量级 spring-boot-starter,基于编译时注解 annotationProcessor 生成的全局jpa持久化框架,实现 entity 实体类中
的枚举对象与数据库数字自动转换,以及对自定义对象和数据库 json 类型的自动转换,转换类基础至jpa转换接口 AttributeConverter

## 软件架构
 本项目的核心架构是使用 javax 中的编译时注解,然后配合 google 的 autoService 自动注册processor,
 以及 javapoet 框架的apt代码插桩功能,在项目执行 mvn compile 的时候,会扫描对应的加了标记注解 @EnumAutoConverter 
 或者 @JsonAutoConverter 注解的枚举或对象,然后生成一个对应的 AutoConverter 类,并开启自动转换,


## 安装教程

```mxml
       <dependency>
             <groupId>org.sunshine</groupId>
             <artifactId>spring-boot-starter-jpa-conversion</artifactId>
             <version>1.0.0-RELEASE</version>
       </dependency>
```

## 使用说明

#### 1. 项目中的枚举类 实现 CodeEnum 接口,并重写获取 code 和 value 的方法,并加上枚举扫描的注解 @EnumAutoConverter 然后在实体类中就可直接使用,存入数据库的会是此枚举的code值
```java
/**
 * @author: Teamo
 * @date: 2020/7/13 12:02
 * @description:
 */
@Getter
@EnumAutoConverter
@AllArgsConstructor
public enum  ElementStatusEnum implements ExtensionEnum {

  /**
   * 删除
   */
  DELETION(1,"删除"),

  /**
   * 标准
   */
  NORM(2,"标准"),

  /**
   * 定制
   */
  CUSTOMIZATION(3,"定制化");

  private final Integer code;
  private final String name;

}
```


#### 2. 在自定义对象上加上注解 @JsonAutoConverter,然后就可在 entity 中使用本类, 会自动转换为数据库的json类型,并支持 List<T> 的集合形式

```java
/**
 * @author: Teamo
 * @date: 2020/7/13 11:43
 * @description:
 */
@Data
@JsonAutoConverter
public class TagInformation {

  private String name;

  private String url;

}
```

#### 3. 本 starter 中内置了对 List<String> List<Integer> List<Double> 等通用类型的默认转换 




#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
