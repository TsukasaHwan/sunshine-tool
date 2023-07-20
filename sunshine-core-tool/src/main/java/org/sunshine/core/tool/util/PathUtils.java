package org.sunshine.core.tool.util;

/**
 * @author Teamo
 * @since 2023/7/20
 */
public class PathUtils {

    /**
     * 拼接路径，自动以/分割
     *
     * @param args 路径参数
     * @return 完整路径
     */
    public static String builderPath(String... args) {
        StringBuilder sb = StringUtils.builder();
        for (int i = 0; i < args.length; i++) {
            String backPart = args[i];
            if (i == 0) {
                sb.append(backPart);
                continue;
            }
            if (StringUtils.isEmpty(backPart)) {
                continue;
            }
            String frontPart = sb.toString();
            if (frontPart.endsWith(StringPool.SLASH) && !backPart.startsWith(StringPool.SLASH)) {
                sb.append(backPart);
            } else if (frontPart.endsWith(StringPool.SLASH) && backPart.startsWith(StringPool.SLASH)) {
                sb.append(backPart.substring(1));
            }
            if (!frontPart.endsWith(StringPool.SLASH) && !backPart.startsWith(StringPool.SLASH)) {
                sb.append(StringPool.SLASH).append(backPart);
            } else if (!frontPart.endsWith(StringPool.SLASH) && backPart.startsWith(StringPool.SLASH)) {
                sb.append(backPart);
            }
        }
        return sb.toString();
    }

    /**
     * 拼接路径：以/为结尾,无参返回/
     *
     * @param args 路径参数
     * @return 完整路径
     */
    public static String builderPathEndSlash(String... args) {
        StringBuilder sb = StringUtils.builder(builderPath(args));
        if (!sb.toString().endsWith(StringPool.SLASH)) {
            sb.append(StringPool.SLASH);
        }
        return sb.toString();
    }
}
