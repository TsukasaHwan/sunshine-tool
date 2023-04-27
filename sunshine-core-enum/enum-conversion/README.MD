# spring-boot-starter-jpa-conversion

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



