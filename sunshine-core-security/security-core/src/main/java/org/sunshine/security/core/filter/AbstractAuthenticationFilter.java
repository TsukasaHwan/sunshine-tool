package org.sunshine.security.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * 抽象认证
 *
 * @author Teamo
 * @since 2023/3/13
 */
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    private Set<String> permitAllAntPatterns;

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isPermitAll(request)) {
            filterChain.doFilter(request, response);
            return;
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
     * 判断请求路径是否包含@PermitAll注解
     *
     * @param request HttpServletRequest
     * @return 是否需要token认证
     */
    private boolean isPermitAll(HttpServletRequest request) {
        boolean isPermitAll = false;
        if (permitAllAntPatterns != null && !permitAllAntPatterns.isEmpty()) {
            if (permitAllAntPatterns.contains(request.getServletPath())) {
                isPermitAll = true;
            }
        }
        return isPermitAll;
    }

    public Set<String> getPermitAllAntPatterns() {
        return permitAllAntPatterns;
    }

    public void setPermitAllAntPatterns(Set<String> permitAllAntPatterns) {
        this.permitAllAntPatterns = permitAllAntPatterns;
    }
}
