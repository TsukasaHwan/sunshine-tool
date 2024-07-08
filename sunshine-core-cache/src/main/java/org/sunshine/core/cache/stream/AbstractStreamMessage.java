package org.sunshine.core.cache.stream;

import com.alibaba.fastjson2.annotation.JSONField;

import java.io.Serializable;

/**
 * @author Teamo
 * @since 2024/7/8
 */
public abstract class AbstractStreamMessage implements Serializable {

    @JSONField(serialize = false)
    public abstract String getStreamKey();

}
