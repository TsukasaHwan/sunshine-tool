package org.sunshine.core.tool.util;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author Teamo
 * @since 2023/5/12
 */
public class FileNameUtils {

    /**
     * 类Unix路径分隔符
     */
    public static final char UNIX_SEPARATOR = StringPool.CHAR_SLASH;
    /**
     * Windows路径分隔符
     */
    public static final char WINDOWS_SEPARATOR = StringPool.BACK_SLASH;

    /**
     * Windows下文件名中的无效字符
     */
    private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|\r\n]");

    /**
     * 特殊后缀
     */
    private static final CharSequence[] SPECIAL_SUFFIX = {"tar.bz2", "tar.Z", "tar.gz", "tar.xz"};


    // -------------------------------------------------------------------------------------------- name start

    /**
     * 返回文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getName(File file) {
        return (null != file) ? file.getName() : null;
    }

    /**
     * 返回文件名<br>
     * <pre>
     * "d:/test/aaa" 返回 "aaa"
     * "/test/aaa.jpg" 返回 "aaa.jpg"
     * </pre>
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 获取文件后缀名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see #extName(File)
     */
    public static String getSuffix(File file) {
        return extName(file);
    }

    /**
     * 获得文件后缀名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     * @see #extName(String)
     */
    public static String getSuffix(String fileName) {
        return extName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     * @see #mainName(File)
     */
    public static String getPrefix(File file) {
        return mainName(file);
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     * @see #mainName(String)
     */
    public static String getPrefix(String fileName) {
        return mainName(fileName);
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return mainName(file.getName());
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     */
    public static String mainName(String fileName) {
        if (null == fileName) {
            return null;
        }
        int len = fileName.length();
        if (0 == len) {
            return fileName;
        }

        for (final CharSequence specialSuffix : SPECIAL_SUFFIX) {
            if (StringUtils.endWith(fileName, "." + specialSuffix)) {
                return StringUtils.subPre(fileName, len - specialSuffix.length() - 1);
            }
        }

        if (isFileSeparator(fileName.charAt(len - 1))) {
            len--;
        }

        int begin = 0;
        int end = len;
        char c;
        for (int i = len - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            if (len == end && StringPool.CHAR_DOT == c) {
                // 查找最后一个文件名和扩展名的分隔符：.
                end = i;
            }
            // 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
            if (isFileSeparator(c)) {
                begin = i + 1;
                break;
            }
        }

        return fileName.substring(begin, end);
    }

    /**
     * 获取文件扩展名（后缀名），扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * 获得文件的扩展名（后缀名），扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        final int index = fileName.lastIndexOf(StringPool.DOT);
        if (index == -1) {
            return StringPool.EMPTY;
        } else {
            final int secondToLastIndex = fileName.substring(0, index).lastIndexOf(StringPool.DOT);
            final String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
            if (StringUtils.containsAny(substr, SPECIAL_SUFFIX)) {
                return substr;
            }

            final String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return StringUtils.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? StringPool.EMPTY : ext;
        }
    }

    /**
     * 清除文件名中的在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 清理后的文件名
     */
    public static String cleanInvalid(String fileName) {
        return StringUtils.isBlank(fileName) ? fileName : RegexUtils.delAll(FILE_NAME_INVALID_PATTERN_WIN, fileName);
    }

    /**
     * 文件名中是否包含在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
     *
     * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
     * @return 是否包含非法字符
     */
    public static boolean containsInvalid(String fileName) {
        return StringUtils.isNotBlank(fileName) && RegexUtils.find(FILE_NAME_INVALID_PATTERN_WIN, fileName);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return UNIX_SEPARATOR == c || WINDOWS_SEPARATOR == c;
    }
}
