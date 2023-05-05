package org.sunshine.enums.scanner.handler;

import org.sunshine.enums.core.enums.CodeEnum;
import org.sunshine.enums.scanner.annotation.EnumScan;
import org.sunshine.enums.scanner.cached.EnumCache;
import org.sunshine.enums.scanner.context.ResourcesScanner;
import org.sunshine.enums.scanner.model.CodeItem;
import org.sunshine.enums.scanner.model.CodeTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Teamo
 * @date: 2020/1/16 13:48
 * @description: 使用码表扫描, 就需要创建一个EnumCache的实现类, 并设置为BEAN
 */
public class EnumScanHandlerImpl implements EnumScanHandler {

    /**
     * 码表缓存
     */
    private final EnumCache cache;

    /**
     * 资源扫描器
     */
    private final ResourcesScanner<Class<?>> resourcesScanner;


    public EnumScanHandlerImpl(EnumCache cache, ResourcesScanner<Class<?>> resourcesScanner) {
        this.cache = cache;
        this.resourcesScanner = resourcesScanner;
    }

    /**
     * 通过反射来获取所有需要扫描的枚举属性值,并存入缓存中
     */
    private void cacheHandler() {

        // 扫描所有枚举类
        List<Class<?>> classes = resourcesScanner.classScan();

        List<CodeTable> codeEnums = new ArrayList<>();
        classes.forEach(clazz -> {
            try {
                // 过滤出 继承了CodeEnum 和 标记了EnumScan注解的枚举
                if (CodeEnum.class.isAssignableFrom(clazz) && clazz.isEnum() && clazz.isAnnotationPresent(EnumScan.class)) {
                    // 枚举的所有实例
                    List<CodeEnum> items = Arrays.stream(clazz.getEnumConstants())
                            .map(item -> (CodeEnum) item)
                            .collect(Collectors.toList());

                    //获取默认值
                    int defaultEnumCode = clazz.getAnnotation(EnumScan.class).defaultEnumCode();

                    // 填充数据
                    codeEnums.add(new CodeTable(clazz.getSimpleName(),
                            items.stream().map(CodeItem::new).collect(Collectors.toList()),
                            items.stream().collect(Collectors.toMap(CodeEnum::code, i -> i, (m, n) -> m)).get(defaultEnumCode),
                            clazz.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        cache.write(codeEnums);

    }

    /**
     * 获取系统中的所有枚举
     *
     * @return 码表集合
     */
    @Override
    public List<CodeTable> codeTables() {
        List<CodeTable> read = cache.read();
        if (read == null) {
            // 获取不到数据进行加载
            cacheHandler();
            return cache.read();
        }
        return read;
    }


}
