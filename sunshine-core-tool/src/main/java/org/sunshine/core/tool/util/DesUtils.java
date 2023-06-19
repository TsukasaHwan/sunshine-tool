package org.sunshine.core.tool.util;

import org.springframework.lang.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * DES加解密处理工具
 *
 * @author L.cm
 */
public class DesUtils {

    /**
     * 数字签名，密钥算法
     */
    public static final String DES_ALGORITHM = "DES";

    /**
     * 生成 des 密钥
     *
     * @return 密钥
     */
    public static String genDesKey() {
        return StringUtils.random(16);
    }

    /**
     * DES加密
     *
     * @param data     byte array
     * @param password 密钥
     * @return des hex
     */
    public static String encryptToHex(byte[] data, String password) {
        return HexUtils.encodeToString(encrypt(data, password));
    }

    /**
     * DES加密
     *
     * @param data     字符串内容
     * @param password 密钥
     * @return des hex
     */
    @Nullable
    public static String encryptToHex(@Nullable String data, String password) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        return encryptToHex(dataBytes, password);
    }

    /**
     * DES解密
     *
     * @param data     字符串内容
     * @param password 密钥
     * @return des context
     */
    @Nullable
    public static String decryptFormHex(@Nullable String data, String password) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] hexBytes = HexUtils.decode(data);
        return new String(decrypt(hexBytes, password), StandardCharsets.UTF_8);
    }

    /**
     * DES加密
     *
     * @param data     byte array
     * @param password 密钥
     * @return des hex
     */
    public static String encryptToBase64(byte[] data, String password) {
        return Base64.getEncoder().encodeToString(encrypt(data, password));
    }

    /**
     * DES加密
     *
     * @param data     字符串内容
     * @param password 密钥
     * @return des hex
     */
    @Nullable
    public static String encryptToBase64(@Nullable String data, String password) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        return encryptToBase64(dataBytes, password);
    }

    /**
     * DES解密
     *
     * @param data     字符串内容
     * @param password 密钥
     * @return des context
     */
    public static byte[] decryptFormBase64(byte[] data, String password) {
        byte[] dataBytes = Base64.getDecoder().decode(data);
        return decrypt(dataBytes, password);
    }

    /**
     * DES解密
     *
     * @param data     字符串内容
     * @param password 密钥
     * @return des context
     */
    @Nullable
    public static String decryptFormBase64(@Nullable String data, String password) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        byte[] dataBytes = Base64.getDecoder().decode(data);
        return new String(decrypt(dataBytes, password), StandardCharsets.UTF_8);
    }

    /**
     * DES加密
     *
     * @param data   内容
     * @param desKey 密钥
     * @return byte array
     */
    public static byte[] encrypt(byte[] data, byte[] desKey) {
        return des(data, desKey, Cipher.ENCRYPT_MODE);
    }

    /**
     * DES加密
     *
     * @param data   内容
     * @param desKey 密钥
     * @return byte array
     */
    public static byte[] encrypt(byte[] data, String desKey) {
        return encrypt(data, Objects.requireNonNull(desKey).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * DES解密
     *
     * @param data   内容
     * @param desKey 密钥
     * @return byte array
     */
    public static byte[] decrypt(byte[] data, byte[] desKey) {
        return des(data, desKey, Cipher.DECRYPT_MODE);
    }

    /**
     * DES解密
     *
     * @param data   内容
     * @param desKey 密钥
     * @return byte array
     */
    public static byte[] decrypt(byte[] data, String desKey) {
        return decrypt(data, Objects.requireNonNull(desKey).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * DES加密/解密公共方法
     *
     * @param data   byte数组
     * @param desKey 密钥
     * @param mode   加密：{@link Cipher#ENCRYPT_MODE}，解密：{@link Cipher#DECRYPT_MODE}
     * @return des
     */
    private static byte[] des(byte[] data, byte[] desKey, int mode) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DES_ALGORITHM);
            DESKeySpec desKeySpec = new DESKeySpec(desKey);
            cipher.init(mode, keyFactory.generateSecret(desKeySpec), Holder.SECURE_RANDOM);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

}
