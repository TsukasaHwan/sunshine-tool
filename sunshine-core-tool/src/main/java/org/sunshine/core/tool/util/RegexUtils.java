package org.sunshine.core.tool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具
 *
 * @author Teamo
 */
public class RegexUtils {

    /**
     * 中文
     */
    public static final String CHINESE = "[\\u4e00-\\u9fa5]+";

    /**
     * 用户名
     */
    public static final String USER_NAME = "^[a-zA-Z\\u4E00-\\u9FA5][a-zA-Z0-9_\\u4E00-\\u9FA5]{1,11}$";

    /**
     * 密码
     */
    public static final String USER_PASSWORD = "^.{6,32}$";

    /**
     * 邮箱
     */
    public static final String EMAIL = "^\\w+([-+.]*\\w+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$";

    /**
     * 手机号
     */
    public static final String PHONE = "^(0|86|17951)?(13[0-9]|15[012356789]|166|17[3678]|18[0-9]|14[57])[0-9]{8}$";

    /**
     * 手机号或者邮箱
     */
    public static final String EMAIL_OR_PHONE = EMAIL + "|" + PHONE;

    /**
     * URL路径
     */
    public static final String URL = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})(:[\\d]+)?([\\/\\w\\.-]*)*\\/?$";

    /**
     * 身份证校验，初级校验，具体规则有一套算法
     */
    public static final String ID_CARD = "^\\d{15}$|^\\d{17}([0-9]|X)$";

    /**
     * 域名校验
     */
    public static final String DOMAIN = "^[0-9a-zA-Z]+[0-9a-zA-Z\\.-]*\\.[a-zA-Z]{2,4}$";

    /**
     * 编译传入正则表达式和字符串去匹配,忽略大小写
     *
     * @param regex        正则
     * @param beTestString 字符串
     * @return {boolean}
     */
    public static boolean match(String regex, String beTestString) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(beTestString);
        return matcher.matches();
    }

    /**
     * 编译传入正则表达式和字符串去匹配
     *
     * @param pattern      正则
     * @param beTestString 字符串
     * @return {boolean}
     */
    public static boolean match(Pattern pattern, String beTestString) {
        Matcher matcher = pattern.matcher(beTestString);
        return matcher.matches();
    }

    /**
     * 编译传入正则表达式在字符串中寻找，如果匹配到则为true
     *
     * @param regex        正则
     * @param beTestString 字符串
     * @return {boolean}
     */
    public static boolean find(String regex, String beTestString) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beTestString);
        return matcher.find();
    }

    /**
     * 编译传入正则表达式在字符串中寻找，如果匹配到则为true
     *
     * @param pattern      正则
     * @param beTestString 字符串
     * @return {boolean}
     */
    public static boolean find(Pattern pattern, String beTestString) {
        if (null == pattern || null == beTestString) {
            return false;
        }
        return pattern.matcher(beTestString).find();
    }

    /**
     * 编译传入正则表达式在字符串中寻找，如果找到返回第一个结果
     * 找不到返回null
     *
     * @param regex         正则
     * @param beFoundString 字符串
     * @return {boolean}
     */
    public static String findResult(String regex, String beFoundString) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beFoundString);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 用给定的替换替换与给定正则表达式模式匹配的文本字符串的每个子字符串
     *
     * @param text        字符串
     * @param regex       正则
     * @param replacement 要替换为每个匹配项的字符串
     * @return 处理过任何替换的文本，如果为null则为 null String 输入
     */
    public static String replaceAll(final String text, final Pattern regex, final String replacement) {
        if (ObjectUtils.hasEmpty(text, regex, replacement)) {
            return text;
        }
        return regex.matcher(text).replaceAll(replacement);
    }

    /**
     * 删除匹配的全部内容
     *
     * @param pattern 正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delAll(Pattern pattern, CharSequence content) {
        if (null == pattern || StringUtils.isEmpty(content)) {
            return StringUtils.toStr(content, null);
        }

        return pattern.matcher(content).replaceAll(StringPool.EMPTY);
    }
}
