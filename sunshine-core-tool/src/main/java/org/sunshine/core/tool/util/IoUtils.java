package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * IoUtils
 *
 * @author L.cm
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

    public static byte[] toByteArray(@Nullable InputStream input) {
        try {
            return copyToByteArray(input);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            closeQuietly(input);
        }
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
            output.write(data.getBytes(encoding));
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

    /**
     * 序列化<br>
     * 对象必须实现Serializable接口
     *
     * @param <T> 对象类型
     * @param obj 要被序列化的对象
     * @return 序列化后的字节码
     */
    public static <T> byte[] serialize(T obj) {
        if (!(obj instanceof Serializable)) {
            return null;
        }
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        writeObjects(byteOut, false, (Serializable) obj);
        return byteOut.toByteArray();
    }

    /**
     * 反序列化<br>
     * 对象必须实现Serializable接口
     *
     * @param <T>   对象类型
     * @param bytes 反序列化的字节码
     * @return 反序列化后的对象
     */
    public static <T> T deserialize(byte[] bytes) {
        return readObj(new ByteArrayInputStream(bytes));
    }
}
