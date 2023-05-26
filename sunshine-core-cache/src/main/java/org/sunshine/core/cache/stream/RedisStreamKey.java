package org.sunshine.core.cache.stream;

/**
 * @author Teamo
 * @since 2023/5/26
 */
public interface RedisStreamKey {

    /**
     * Redis Stream
     *
     * @return {String}
     */
    String stream();

    /**
     * Redis Stream Group
     *
     * @return {String}
     */
    String group();

    /**
     * Redis Stream Consumer
     *
     * @return {String}
     */
    String consumer();
}
