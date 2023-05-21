package org.sunshine.core.tool.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Base64工具
 *
 * @author L.cm
 */
public class Base64Utils extends org.springframework.util.Base64Utils {

    /**
     * 编码
     *
     * @param value 字符串
     * @return {String}
     */
    public static String encode(String value) {
        return encode(value, StandardCharsets.UTF_8);
    }

    /**
     * 编码
     *
     * @param value   字符串
     * @param charset 字符集
     * @return {String}
     */
    public static String encode(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        return new String(encode(val), charset);
    }

    /**
     * 编码URL安全
     *
     * @param value 字符串
     * @return {String}
     */
    public static String encodeUrlSafe(String value) {
        return encodeUrlSafe(value, StandardCharsets.UTF_8);
    }

    /**
     * 编码URL安全
     *
     * @param value   字符串
     * @param charset 字符集
     * @return {String}
     */
    public static String encodeUrlSafe(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        return new String(encodeUrlSafe(val), charset);
    }

    /**
     * 解码
     *
     * @param value 字符串
     * @return {String}
     */
    public static String decode(String value) {
        return decode(value, StandardCharsets.UTF_8);
    }

    /**
     * 解码
     *
     * @param value   字符串
     * @param charset 字符集
     * @return {String}
     */
    public static String decode(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        byte[] decodedValue = decode(val);
        return new String(decodedValue, charset);
    }

    /**
     * 解码URL安全
     *
     * @param value 字符串
     * @return {String}
     */
    public static String decodeUrlSafe(String value) {
        return decodeUrlSafe(value, StandardCharsets.UTF_8);
    }

    /**
     * 解码URL安全
     *
     * @param value   字符串
     * @param charset 字符集
     * @return {String}
     */
    public static String decodeUrlSafe(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        byte[] decodedValue = decodeUrlSafe(val);
        return new String(decodedValue, charset);
    }
}
