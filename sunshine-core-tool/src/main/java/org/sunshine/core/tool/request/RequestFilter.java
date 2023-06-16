package org.sunshine.core.tool.request;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * @author Teamo
 * @since 2022/04/11
 */
public class RequestFilter implements Filter {

    private final RequestProperties requestProperties;
    private final XssProperties xssProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public RequestFilter(RequestProperties requestProperties, XssProperties xssProperties) {
        this.requestProperties = requestProperties;
        this.xssProperties = xssProperties;
    }

    @Override
    public void init(FilterConfig config) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        // 跳过 Request 包装
        if (!requestProperties.getEnabled() || isRequestSkip(path)) {
            chain.doFilter(request, response);
        }
        // 默认 Request 包装
        else if (!xssProperties.getEnabled() || isXssSkip(path)) {
            GlobalHttpServletRequestWrapper globalRequest = new GlobalHttpServletRequestWrapper((HttpServletRequest) request);
            chain.doFilter(globalRequest, response);
        }
        // Xss Request 包装
        else {
            XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
            chain.doFilter(xssRequest, response);
        }
    }

    private boolean isRequestSkip(String path) {
        return requestProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private boolean isXssSkip(String path) {
        return xssProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    @Override
    public void destroy() {

    }
}
