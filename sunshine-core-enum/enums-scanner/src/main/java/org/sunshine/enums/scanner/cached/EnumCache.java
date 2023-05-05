package org.sunshine.enums.scanner.cached;


import org.sunshine.enums.scanner.model.CodeTable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Teamo
 * @date: 2020/1/16 13:45
 * @description: 缓存枚举的接口
 */
public interface EnumCache {

    /**
     * 写入缓存
     *
     * @param codeEnums 码表实体
     */
    void write(List<CodeTable> codeEnums);

    /**
     * 从缓存中路读取所有的枚举数据
     *
     * @return 码表集合
     */
    List<CodeTable> read();

    /**
     * @author: Teamo
     * @date: 2020/7/9 10:32
     * @description: 默认的枚举缓存实现类, 把枚举数据缓存到本地内存中
     * 可以通过重写一个实现enumCache 的容器类来覆盖默认缓存
     */
    class DefaultMemoryEnumCache implements EnumCache {

        private static final Map<String, List<CodeTable>> CACHE = new ConcurrentHashMap<>();

        private final static String ENUM_CACHE_KEY = "enum_cache_key_1219";

        @Override
        public void write(List<CodeTable> codeEnums) {
            CACHE.put(ENUM_CACHE_KEY, codeEnums);
        }

        @Override
        public List<CodeTable> read() {
            return CACHE.get(ENUM_CACHE_KEY);
        }
    }
}
