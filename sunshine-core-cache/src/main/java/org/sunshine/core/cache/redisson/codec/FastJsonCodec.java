package org.sunshine.core.cache.redisson.codec;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;

/**
 * @author Teamo
 * @since 2022/01/18
 */
public class FastJsonCodec extends BaseCodec {

    /**
     * 编码器
     */
    private final Encoder encoder = in -> {
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        try {
            ByteBufOutputStream os = new ByteBufOutputStream(out);
            JSON.writeTo(os, in, JSONWriter.Feature.WriteClassName);
            return os.buffer();
        } catch (Exception e) {
            out.release();
            throw new IOException(e);
        }
    };

    /**
     * 解码器
     */
    private final Decoder<Object> decoder = (buf, state) ->
            JSON.parseObject(new ByteBufInputStream(buf), Object.class);

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }
}
