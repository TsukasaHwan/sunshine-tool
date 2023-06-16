package org.sunshine.core.cloud.header;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.sunshine.core.cloud.properties.FeignHeadersProperties;
import org.sunshine.core.tool.util.CollectionUtils;
import org.sunshine.core.tool.util.StringUtils;
import org.sunshine.core.tool.util.WebUtils;

import java.util.Enumeration;
import java.util.List;

/**
 * HttpHeadersContext
 *
 * @author Teamo
 */
public class HttpHeadersContextHolder {
    private static final ThreadLocal<HttpHeaders> HTTP_HEADERS_HOLDER = new NamedThreadLocal<>("Feign HttpHeaders");

    static void set(HttpHeaders httpHeaders) {
        HTTP_HEADERS_HOLDER.set(httpHeaders);
    }

    @Nullable
    public static HttpHeaders get() {
        return HTTP_HEADERS_HOLDER.get();
    }

    static void remove() {
        HTTP_HEADERS_HOLDER.remove();
    }

    @Nullable
    public static HttpHeaders toHeaders(FeignHeadersProperties properties) {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        List<String> allowHeadsList = properties.getAllowed();
        // 传递请求头
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                // 只支持配置的 header
                if (CollectionUtils.isNotEmpty(allowHeadsList) && allowHeadsList.contains(key)) {
                    String values = request.getHeader(key);
                    // header value 不为空的 传递
                    if (StringUtils.isNotBlank(values)) {
                        headers.add(key, values);
                    }
                }
            }
        }
        return headers.isEmpty() ? null : headers;
    }
}
