package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * IoUtils
 *
 * @author Teamo
 */
public class IoUtils extends StreamUtils {

    /**
     * closeQuietly
     *
     * @param closeable 自动关闭
     */
    public static void closeQuietly(@Nullable Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * InputStream to String utf-8
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested String
     */
    public static String toString(InputStream input) {
        return toString(input, StandardCharsets.UTF_8);
    }

    /**
     * InputStream to String
     *
     * @param input   the <code>InputStream</code> to read from
     * @param charset the <code>Charsets</code>
     * @return the requested String
     */
    public static String toString(@Nullable InputStream input, Charset charset) {
        try {
            return copyToString(input, charset);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            closeQuietly(input);
        }
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param input {@link InputStream}
     * @return bytes
     */
    public static byte[] readBytes(@Nullable InputStream input) {
        return readBytes(input, true);
    }

    /**
     * 从流中读取bytes
     *
     * @param input   {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return bytes
     */
    public static byte[] readBytes(@Nullable InputStream input, boolean isClose) {
        try {
            return copyToByteArray(input);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            if (isClose) {
                closeQuietly(input);
            }
        }
    }

    /**
     * 读取指定长度的byte数组，不关闭流
     *
     * @param in     {@link InputStream}，为{@code null}返回{@code null}
     * @param length 长度，小于等于0返回空byte数组
     * @return bytes
     */
    public static byte[] readBytes(@Nullable InputStream in, int length) {
        if (null == in) {
            return null;
        }
        if (length <= 0) {
            return new byte[0];
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            copyRange(in, out, 0, length - 1);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
        return out.toByteArray();
    }

    /**
     * 从流中读取对象，即对象的反序列化
     *
     * @param <T> 读取对象的类型
     * @param in  输入流
     * @return 输出流
     */
    @SuppressWarnings("unchecked")
    public static <T> T readObj(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(in);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw Exceptions.unchecked(e);
        }
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>String</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws IOException if an I/O error occurs
     */
    public static void write(@Nullable final String data, final OutputStream output, final Charset encoding) throws IOException {
        if (data != null) {
            write(data.getBytes(encoding), output);
        }
    }

    /**
     * 将字节数组写到流中
     *
     * @param bytes  要写入的字节数组，忽略 null
     * @param output 要写入的OutputStream
     * @throws IOException 如果发生 I/O 错误
     */
    public static void write(@Nullable final byte[] bytes, final OutputStream output) throws IOException {
        if (bytes != null) {
            output.write(bytes);
        }
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param obj        写入的对象内容
     */
    public static void writeObj(OutputStream out, boolean isCloseOut, Serializable obj) {
        writeObjects(out, isCloseOut, obj);
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容
     */
    public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) {
        ObjectOutputStream osw = null;
        try {
            osw = out instanceof ObjectOutputStream ? (ObjectOutputStream) out : new ObjectOutputStream(out);
            for (Object content : contents) {
                if (content != null) {
                    osw.writeObject(content);
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            if (isCloseOut) {
                closeQuietly(osw);
            }
        }
    }
}
