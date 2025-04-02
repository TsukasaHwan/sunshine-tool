package org.sunshine.security.jwt.support;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import io.jsonwebtoken.io.AbstractSerializer;
import io.jsonwebtoken.lang.Assert;

import java.io.OutputStream;

/**
 * @author Teamo
 * @since 2025/4/2
 */
public class FastJson2Serializer<T> extends AbstractSerializer<T> {

    private FastJsonConfig config = new FastJsonConfig();

    @Override
    protected void doSerialize(T t, OutputStream out) throws Exception {
        Assert.notNull(out, "OutputStream cannot be null.");
        JSON.writeTo(out, t, config.getDateFormat(), config.getWriterFilters(), config.getWriterFeatures());
    }

    public FastJsonConfig getConfig() {
        return config;
    }

    public void setConfig(FastJsonConfig config) {
        this.config = config;
    }
}
