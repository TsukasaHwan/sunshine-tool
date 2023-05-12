package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            return IoUtils.copyToString(input, charset);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            IoUtils.closeQuietly(input);
        }
    }

    public static byte[] toByteArray(@Nullable InputStream input) {
        try {
            return IoUtils.copyToByteArray(input);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        } finally {
            IoUtils.closeQuietly(input);
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
}
