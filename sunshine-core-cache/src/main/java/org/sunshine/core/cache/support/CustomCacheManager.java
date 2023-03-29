package org.sunshine.core.cache.support;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Teamo
 * @since 2023/3/10
 */
public class CustomCacheManager implements CacheManager {

    private final ConcurrentMap<String, Cache> cacheConcurrentMap = new ConcurrentHashMap<>();

    private final CacheManager localCacheManager;

    private final CacheManager remoteCacheManager;

    public CustomCacheManager(CacheManager localCacheManager, CacheManager remoteCacheManager) {
        this.localCacheManager = localCacheManager;
        this.remoteCacheManager = remoteCacheManager;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Cache getCache(String name) {
        Cache cache = cacheConcurrentMap.get(name);
        if (cache == null) {
            cache = cacheConcurrentMap.put(name, new CustomCache(name, localCacheManager.getCache(name), remoteCacheManager.getCache(name)));
        }
        return cache;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheConcurrentMap.keySet());
    }
}
