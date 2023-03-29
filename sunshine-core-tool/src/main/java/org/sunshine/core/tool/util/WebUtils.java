package org.sunshine.core.tool.util;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

/**
 * Miscellaneous utilities for web applications.
 *
 * @author Teamo
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    public static final String USER_AGENT_HEADER = "user-agent";

    /**
     * 判断是否ajax请求
     * spring ajax 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     * @return 是否ajax请求
     */
    public static boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = ClassUtils.getAnnotation(handlerMethod, ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 读取cookie
     *
     * @param name cookie name
     * @return cookie value
     */
    @Nullable
    public static String getCookieVal(String name) {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "request from RequestContextHolder is null");
        return getCookieVal(request, name);
    }

    /**
     * 读取cookie
     *
     * @param request HttpServletRequest
     * @param name    cookie name
     * @return cookie value
     */
    @Nullable
    public static String getCookieVal(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie
     *
     * @param response HttpServletResponse
     * @param key      cookie key
     */
    public static void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     *
     * @param response        HttpServletResponse
     * @param name            cookie name
     * @param value           cookie value
     * @param maxAgeInSeconds maxage
     */
    public static void setCookie(HttpServletResponse response, String name, @Nullable String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    /**
     * 返回json
     *
     * @param response HttpServletResponse
     * @param result   结果对象
     */
    public static void renderJson(HttpServletResponse response, Object result) {
        renderJson(response, result, MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * 返回json
     *
     * @param response    HttpServletResponse
     * @param result      结果对象
     * @param contentType contentType
     */
    public static void renderJson(HttpServletResponse response, Object result, String contentType) {
        response.setCharacterEncoding(StringPool.UTF_8);
        response.setContentType(contentType);
        try (PrintWriter out = response.getWriter()) {
            out.append(JSON.toJSONString(result));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取ip
     *
     * @return {String}
     */
    public static String getIP() {
        return getIP(WebUtils.getRequest());
    }

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     * @return {String}
     */
    @Nullable
    public static String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || StringPool.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : ip.split(StringPool.COMMA)[0];
    }

    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request request
     * @return 字符串内容
     */
    public static String getRequestParamString(HttpServletRequest request) {
        try {
            return getRequestStr(request);
        } catch (Exception ex) {
            return StringPool.EMPTY;
        }
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request) throws IOException {
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            return new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("&amp;", "&").replaceAll("%22", "\"");
        }
        return getRequestStr(request, getRequestBytes(request));
    }

    /**
     * 获取 request 请求的 byte[] 数组
     *
     * @param request request
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        ServletInputStream inputStream = request.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        return bos.toByteArray();
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @param buffer  buffer
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = StringPool.UTF_8;
        }
        String str = new String(buffer, charEncoding).trim();
        if (StringUtils.isBlank(str)) {
            StringBuilder sb = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                String value = request.getParameter(key);
                sb.append(sb).append(key).append(StringPool.EQUALS).append(value).append(StringPool.AMPERSAND);
            }
            str = StringUtils.removeSuffix(sb.toString(), StringPool.AMPERSAND);
        }
        return str.replaceAll("&amp;", StringPool.AMPERSAND);
    }

    /**
     * 将map转换成url请求参数
     *
     * @param map map
     * @return 参数
     */
    public static String getUrlParamsByMap(Map<String, ?> map) {
        if (map == null) {
            return StringPool.QUESTION_MARK;
        }
        StringBuilder sb = new StringBuilder();
        Set<? extends Map.Entry<String, ?>> entries = map.entrySet();
        for (Map.Entry<String, ?> entry : entries) {
            sb.append(entry.getKey()).append(StringPool.EQUALS).append(entry.getValue());
            sb.append(StringPool.AMPERSAND);
        }
        String s = sb.toString();
        if (s.endsWith(StringPool.AMPERSAND)) {
            s = StringUtils.subBefore(s, StringPool.AMPERSAND, true);
        }
        return StringPool.QUESTION_MARK + s;
    }

    /**
     * 获取 request 请求体
     *
     * @param servletInputStream servletInputStream
     * @return body
     */
    public static String getRequestBody(ServletInputStream servletInputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(servletInputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (servletInputStream != null) {
                try {
                    servletInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @return {String}
     */
    public static String getRequestContent(HttpServletRequest request) {
        try {
            String queryString = request.getQueryString();
            if (StringUtils.isNotBlank(queryString)) {
                return new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("&amp;", StringPool.AMPERSAND).replaceAll("%22", StringPool.QUOTE);
            }
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null) {
                charEncoding = StringPool.UTF_8;
            }
            byte[] buffer = getRequestBody(request.getInputStream()).getBytes();
            String str = new String(buffer, charEncoding).trim();
            if (StringUtils.isBlank(str)) {
                StringBuilder sb = new StringBuilder();
                Enumeration<String> parameterNames = request.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String key = parameterNames.nextElement();
                    String value = request.getParameter(key);
                    sb.append(key).append(StringPool.EQUALS).append(value).append(StringPool.AMPERSAND);
                }
                str = StringUtils.removeSuffix(sb.toString(), StringPool.AMPERSAND);
            }
            return str.replaceAll("&amp;", StringPool.AMPERSAND);
        } catch (Exception ex) {
            ex.printStackTrace();
            return StringPool.EMPTY;
        }
    }
}
