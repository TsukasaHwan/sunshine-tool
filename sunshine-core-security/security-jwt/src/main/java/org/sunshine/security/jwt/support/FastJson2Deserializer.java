package org.sunshine.security.jwt.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import io.jsonwebtoken.io.AbstractDeserializer;
import io.jsonwebtoken.lang.Assert;

import java.io.Reader;
import java.util.Collections;
import java.util.Map;

/**
 * @author Teamo
 * @since 2025/4/2
 */
public class FastJson2Deserializer<T> extends AbstractDeserializer<T> {

    private final Class<T> type;

    private FastJsonConfig config = new FastJsonConfig();

    private Map<String, Class<?>> claimTypeMap;

    @SuppressWarnings("unchecked")
    public FastJson2Deserializer() {
        this((Class<T>) Object.class);
    }

    public FastJson2Deserializer(Class<T> type) {
        Assert.notNull(type, "Type cannot be null.");
        this.type = type;
    }

    public FastJson2Deserializer(Map<String, Class<?>> claimTypeMap) {
        this();
        Assert.notNull(claimTypeMap, "Claim type map cannot be null.");
        this.claimTypeMap = Collections.unmodifiableMap(claimTypeMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doDeserialize(Reader reader) throws Exception {
        JSONObject obj = JSON.parseObject(reader, this.type, config.getReaderFeatures());
        if (this.claimTypeMap != null) {
            this.claimTypeMap.keySet()
                    .stream()
                    .filter(obj::containsKey)
                    .forEach(k -> obj.put(k, JSON.to(this.claimTypeMap.get(k), obj.get(k))));
        }
        return (T) obj;
    }

    public FastJsonConfig getConfig() {
        return config;
    }

    public void setConfig(FastJsonConfig config) {
        this.config = config;
    }
}
