package org.sunshine.core.tool.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 邀请码生成util
 *
 * @author Teamo
 * @since 2020/10/24
 */
public class InviteCodeUtils {

    /**
     * 随机字符串
     */
    private static final char[] CHARS = new char[]{'F', 'L', 'G', 'W', '5', 'X', 'C', '3', '9', 'Z', 'M', '6', '7',
            'Y', 'R', 'T', '2', 'H', 'S', '8', 'D', 'V', 'E', 'J', '4', 'K', 'Q', 'P', 'U', 'A', 'N', 'B'};

    private final static int CHARS_LENGTH = 32;

    /**
     * 邀请码长度
     */
    private static final int CODE_LENGTH = 6;

    /**
     * PRIME1 与 CHARS 的长度 L互质，可保证 ( id * PRIME1) % L 在 [0,L)上均匀分布
     */
    private final static int PRIME1 = 3;

    /**
     * PRIME2 与 codeLength 互质，可保证 ( index * PRIME2) % codeLength 在
     * [0，codeLength）上均匀分布
     */
    private final static int PRIME2 = 11;

    /**
     * 字符串与数字的对应关系
     */
    private static final Map<Character, Integer> CHAR_TO_INDEX = new HashMap<>(43);

    static {
        for (int i = 0; i < CHARS_LENGTH; i++) {
            CHAR_TO_INDEX.put(CHARS[i], i);
        }
    }

    /**
     * 生成邀请码
     *
     * @param id   唯一的id主键
     * @param salt 盐（正整数）
     * @return code
     */
    public static String gen(Long id, long salt) {
        // 补位
        long adjustedId = id * PRIME1 + salt;
        // 将 id 转换成32进制的值
        long[] b = new long[CODE_LENGTH];
        // 32进制数
        b[0] = adjustedId;
        for (int i = 0; i < CODE_LENGTH - 1; i++) {
            b[i + 1] = b[i] / CHARS_LENGTH;
            // 按位扩散
            b[i] = (b[i] + i * b[0]) % CHARS_LENGTH;
        }
        b[5] = (b[0] + b[1] + b[2] + b[3] + b[4]) * PRIME1 % CHARS_LENGTH;

        // 进行混淆
        long[] codeIndexArray = new long[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            codeIndexArray[i] = b[i * PRIME2 % CODE_LENGTH];
        }

        StringBuilder buffer = new StringBuilder();
        Arrays.stream(codeIndexArray).boxed().map(Long::intValue).map(t -> CHARS[t]).forEach(buffer::append);
        return buffer.toString();
    }

    /**
     * 将邀请码解密成原来的id
     *
     * @param code 邀请码
     * @param salt 盐
     * @return id
     */
    public static Long decode(String code, long salt) {
        if (code.length() != CODE_LENGTH) {
            return null;
        }
        // 将字符还原成对应数字
        long[] a = new long[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            char c = code.charAt(i);
            Integer index = CHAR_TO_INDEX.get(c);
            if (index == null) {
                // 异常字符串
                return null;
            }
            a[i * PRIME2 % CODE_LENGTH] = index;
        }

        long[] b = new long[CODE_LENGTH];
        for (int i = CODE_LENGTH - 2; i >= 0; i--) {
            b[i] = (a[i] - a[0] * i + (long) CHARS_LENGTH * i) % CHARS_LENGTH;
        }

        long res = 0;
        for (int i = CODE_LENGTH - 2; i >= 0; i--) {
            res += b[i];
            res *= (i > 0 ? CHARS_LENGTH : 1);
        }
        return (res - salt) / PRIME1;
    }

}
