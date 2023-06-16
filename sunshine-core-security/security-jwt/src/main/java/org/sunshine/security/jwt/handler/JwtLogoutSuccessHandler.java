package org.sunshine.security.jwt.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.sunshine.security.jwt.userdetails.JwtUserDetailsService;
import org.sunshine.security.jwt.util.JwtClaimsUtils;

import java.io.IOException;

/**
 * @author Teamo
 * @since 2023/3/16
 */
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(JwtLogoutSuccessHandler.class);

    private final JwtUserDetailsService jwtUserDetailsService;

    public JwtLogoutSuccessHandler(JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = JwtClaimsUtils.getToken(request);
        String username = null;
        try {
            username = JwtClaimsUtils.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("getting username exception from jwt token", e);
        }
        jwtUserDetailsService.onLogoutSuccess(username);
    }
}
