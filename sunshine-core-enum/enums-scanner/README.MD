# spring-boot-starter-enums-scanner



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

