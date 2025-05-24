package org.sunshine.core.tool.util;

import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * url处理工具类
 *
 * @author L.cm, Teamo
 */
public class UrlUtils extends UriUtils {

    /**
     * 对字符串进行 URI 编码（等同于 JavaScript 的 encodeURIComponent）。
     * <p>
     * 此方法不会将特殊字符如 '+' 特殊对待，在 URI 编码中会被保留或按 RFC3986 标准编码。
     * 如果希望在后续解码时正确还原空格，请确保原始字符串中的空格已转换为 %20 而非 +。
     * </p>
     *
     * @param source  需要编码的字符串
     * @param charset 字符集，例如 UTF-8
     * @return URI 编码后的字符串
     */
    public static String encodeUri(String source, Charset charset) {
        return UrlUtils.encode(source, charset.name());
    }

    /**
     * 对字符串进行 URL 编码，适用于 HTTP 表单提交、查询参数等场景。
     * <p>
     * 此方法会将空格编码为 '+'，符合 application/x-www-form-urlencoded 编码规范。
     * 在使用 decodeUrl 解码时，'+' 会被还原为空格。
     * </p>
     *
     * @param source  需要编码的字符串
     * @param charset 字符集，例如 UTF-8
     * @return URL 编码后的字符串
     */
    public static String encodeUrl(String source, Charset charset) {
        return URLEncoder.encode(source, charset);
    }

    /**
     * 对字符串进行 URI 解码，遵循 RFC3986 标准。
     * <p>
     * 注意：URI 解码不会将 '+' 解释为空格，如果字符串中包含 '+'，
     * 将原样保留 '+' 而不是转换为空格。若需要将 '+' 视为空格，
     * 建议在调用此方法前手动替换 '+' 为 %20。
     * </p>
     *
     * @param source  需要解码的字符串
     * @param charset 字符集，例如 UTF-8
     * @return URI 解码后的字符串
     */
    public static String decodeUri(String source, Charset charset) {
        return UrlUtils.decode(source, charset.name());
    }

    /**
     * 对字符串进行 URL 解码，适用于 HTTP 查询参数、表单提交等场景。
     * <p>
     * 该方法会将 '+' 自动转换为空格，符合 application/x-www-form-urlencoded 编码规则。
     * 因此更适合处理浏览器提交的 URL 参数。
     * </p>
     *
     * @param source  需要解码的字符串
     * @param charset 字符集，例如 UTF-8
     * @return URL 解码后的字符串
     */
    public static String decodeUrl(String source, Charset charset) {
        return URLDecoder.decode(source, charset);
    }

    /**
     * 获取url路径
     *
     * @param uriStr 路径
     * @return url路径
     */
    public static String getPath(String uriStr) {
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri.getPath();
    }

    /**
     * Data URI Scheme封装，数据格式为Base64。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUriBase64(String mimeType, String data) {
        return getDataUri(mimeType, null, "base64", data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUri(String mimeType, String encoding, String data) {
        return getDataUri(mimeType, null, encoding, data);
    }

    /**
     * Data URI Scheme封装。data URI scheme 允许我们使用内联（inline-code）的方式在网页中包含数据，<br>
     * 目的是将一些小的数据，直接嵌入到网页中，从而不用再从外部文件载入。常用于将图片嵌入网页。
     *
     * <p>
     * Data URI的格式规范：
     * <pre>
     *     data:[&lt;mime type&gt;][;charset=&lt;charset&gt;][;&lt;encoding&gt;],&lt;encoded data&gt;
     * </pre>
     *
     * @param mimeType 可选项（null表示无），数据类型（image/png、text/plain等）
     * @param charset  可选项（null表示无），源文本的字符集编码方式
     * @param encoding 数据编码方式（US-ASCII，BASE64等）
     * @param data     编码后的数据
     * @return Data URI字符串
     */
    public static String getDataUri(String mimeType, Charset charset, String encoding, String data) {
        final StringBuilder builder = StringUtils.builder("data:");
        if (StringUtils.isNotBlank(mimeType)) {
            builder.append(mimeType);
        }
        if (null != charset) {
            builder.append(";charset=").append(charset.name());
        }
        if (StringUtils.isNotBlank(encoding)) {
            builder.append(';').append(encoding);
        }
        builder.append(',').append(data);

        return builder.toString();
    }
}
