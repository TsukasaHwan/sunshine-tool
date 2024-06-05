package org.sunshine.core.tool.util;

import java.security.MessageDigest;

/**
 * @author Teamo
 * @since 2024/5/10
 */
public class OpenCodeUtils {

    private final static String[] CHARS = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 生成appId
     *
     * @return 16个字符
     */
    public static String generateAppId() {
        return generateCode(16);
    }

    /**
     * 生成appSecret
     *
     * @return 40个字符
     */
    public static String generateAppSecret(String appId) {
        try {
            String salt = StringUtils.random(8);
            String str = appId + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            StringBuilder builder = new StringBuilder();
            String shaHex;
            for (byte b : digest) {
                shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    builder.append(0);
                }
                builder.append(shaHex);
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 生成code
     *
     * @param length 长度
     * @return code
     */
    public static String generateCode(int length) {
        int count = length / 8;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= count; i++) {
            builder.append(generateCode());
        }
        return builder.substring(0, length).toLowerCase();
    }

    /**
     * 生成8位code
     *
     * @return 8位code
     */
    public static String generateCode() {
        int length = 8;
        StringBuilder builder = new StringBuilder();
        String uuid = IdUtils.simpleUUID();
        for (int i = 0; i < length; i++) {
            String s = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(s, 16);
            builder.append(CHARS[x % 0x24]);
        }
        return builder.toString().toLowerCase();
    }
}
