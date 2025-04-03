package org.sunshine.security.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sunshine.security.core.support.SecurityAnnotationPathMatcherExtractor;

import java.io.IOException;
import java.util.List;

/**
 * 抽象认证
 *
 * @author Teamo
 * @since 2023/3/13
 */
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    private List<SecurityAnnotationPathMatcherExtractor> extractors;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.extractors != null) {
            try {
                for (SecurityAnnotationPathMatcherExtractor extractor : this.extractors) {
                    if (extractor.shouldSkipAuthentication(request)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
            } catch (Exception e) {
                unsuccessfulAuthentication(request, response, e);
                return;
            }
        }

        authenticate(request, response, filterChain);
    }

    /**
     * 抽象认证
     *
     * @param request     HttpServletRequest
     * @param response    HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    protected abstract void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    /**
     * 认证失败
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param e        Exception
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    protected abstract void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e) throws ServletException, IOException;

    public List<SecurityAnnotationPathMatcherExtractor> getExtractors() {
        return extractors;
    }

    public void setExtractors(List<SecurityAnnotationPathMatcherExtractor> extractors) {
        this.extractors = extractors;
    }
}
