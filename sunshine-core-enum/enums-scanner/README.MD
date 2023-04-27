# spring-boot-starter-enums-scanner

## 介绍
轻量级 spring-boot-starter,基于编译时注解 annotationProcessor 生成的全局枚举码表扫描器,并缓存枚举数据.
支持高度的可扩展性,简单易用,一键导入,开箱即用

## 软件架构
 本项目的核心架构是使用 javax 中的编译时注解,然后配合 google 的 autoService 自动注册processor,
 以及 javapoet 框架的apt代码插桩功能,在项目执行 mvn compile 的时候,会扫描对应的枚举,
 并把枚举所在的classPath生成一个静态文件,在项目运行时会通过这个文件执行一次全盘扫描项目枚举,
 通过反射的方式,获取待扫描的所有枚举值,并存放到缓存中,后续查询枚举码表数据都是通过缓存来获取,
 缓存方式支持对外扩展,自己可实现任意存储,如redis,mongo,或者mysql中.


## 安装教程

```mxml
       <dependency>
             <groupId>io.gitee.sneb123</groupId>
             <artifactId>spring-boot-starter-enums-scanner</artifactId>
             <version>1.0.0-RELEASE</version>
       </dependency>
```

## 使用说明

#### 1. 项目中的枚举类 实现 CodeEnum 接口,并重写获取 code 和 value 的方法,并加上枚举扫描的注解 @EnumScan
```java
/**
 * @author: Teamo
 * @date: 2020/1/13 15:23
 * @description:
 */
@EnumScan
@AllArgsConstructor
public enum CompanyType implements CodeEnum {
  /**
   * 自营
   */
  SELF_SUPPORT(1, "自营"),
  
  /**
   * 融资
   */
  FINANCING(2, "融资");

  private final Integer code;
  private final String name;

  @Override
  public Integer code() {
    return code;
  }

  @Override
  public String value() {
    return name;
  }
}
```


#### 2. 获取所有枚举值, 在容器中注入 枚举处理接口 EnumScanHandler,调用其codeTables,即可获取所有枚举值

```java
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class CompanyTest {

  @Autowired
  private EnumScanHandler enumScanHandler;

  @Test
  public void cacheCode() {
    enumScanHandler.codeTables();
  }

}
```

#### 3. 重写自定义枚举缓存(java内存缓存范例),创建一个spring容器并实现enumCache接口,实现 write 和 read ,即可覆盖starter的默认缓存方式

```java
/**
 * @author: Teamo
 * @date: 2020/1/16 14:21
 * @description:
 */
@Component
public class MemoryEnumCache implements EnumCache {

  private static Map<String, Object> cache = new ConcurrentHashMap<>();
  private final static String CACHE_KEY = "code_cache";

  @Override
  public void write(List<CodeTable> codeEnums) {
    cache.put(CACHE_KEY, codeEnums);
  }

  @Override
  public List<CodeTable> read() {
    return (List<CodeTable>) cache.get(CACHE_KEY);
  }
}
```

#### 4. 重写自定义的枚举接口 CodeEnum , 自定义接口虚基础至 CodeEnum ,并使用 default 关键字来覆盖枚举基接口的 code 和 value 方法, 然后项目中的枚举实现自定义接口同样具有码表扫描的功能

```java

/**
 * @author: Teamo
 * @date: 2020/7/9 11:18
 * @description:
 */
public interface ExtensionCodeEnum extends CodeEnum {

  Integer num();

  String field();

  default Integer code(){
    return num();
  }

  default String value(){
    return field();
  }
}

```

#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
