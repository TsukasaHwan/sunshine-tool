package org.sunshine.core.cache.support;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * @author Teamo
 * @since 2023/3/10
 */
public class CustomCache implements Cache {
    /**
     * 缓存名称
     */
    private final String name;

    /**
     * 本地缓存
     */
    private final Cache localCache;

    /**
     * 远程缓存
     */
    private final Cache remoteCache;

    public CustomCache(String name, Cache localCache, Cache remoteCache) {
        this.name = name;
        this.localCache = localCache;
        this.remoteCache = remoteCache;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = localCache.get(key.toString());
        if (valueWrapper != null) {
            return valueWrapper;
        }

        valueWrapper = remoteCache.get(key);
        if (valueWrapper != null) {
            localCache.put(key, valueWrapper.get());
        }
        return valueWrapper;
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public <T> T get(Object key, Class<T> type) {
        final ValueWrapper valueWrapper = get(key);

        Object value = (valueWrapper != null ? valueWrapper.get() : null);
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @Override
    @SuppressWarnings({"unchecked", "NullableProblems"})
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper result = get(key);
        if (result != null) {
            return (T) result.get();
        }

        T value;
        try {
            value = valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        put(key, value);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        remoteCache.put(key.toString(), value);
        localCache.put(key.toString(), value);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void evict(Object key) {
        remoteCache.evict(key);
        localCache.evict(key);
    }

    @Override
    public void clear() {
        remoteCache.clear();
        localCache.clear();
    }
}
