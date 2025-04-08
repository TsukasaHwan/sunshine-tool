package org.sunshine.security.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 抽象认证
 *
 * @author Teamo
 * @since 2023/3/13
 */
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    protected final AuthenticationSuccessHandler successHandler;

    protected final AuthenticationFailureHandler failureHandler;

    protected AbstractAuthenticationFilter(AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            beforeAuthentication(request);
            Authentication authentication = attemptAuthentication(request, response);
            if (authentication == null) {
                filterChain.doFilter(request, response);
                return;
            }
            successfulAuthentication(request, response, filterChain, authentication);
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, e);
        }
    }

    /**
     * 认证前处理
     *
     * @param request 请求
     */
    protected void beforeAuthentication(HttpServletRequest request) {
        // do nothing
    }

    /**
     * 尝试认证
     *
     * @param request  请求
     * @param response 响应
     * @return 认证结果
     */
    protected abstract Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response);

    /**
     * 认证成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param chain          过滤器链
     * @param authentication 认证结果
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        this.successHandler.onAuthenticationSuccess(request, response, chain, authentication);
    }

    /**
     * 认证失败处理
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    protected abstract void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException;
}
