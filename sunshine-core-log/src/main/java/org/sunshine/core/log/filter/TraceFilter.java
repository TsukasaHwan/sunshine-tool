package org.sunshine.core.log.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sunshine.core.tool.util.IdUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.io.IOException;

/**
 * @author Teamo
 * @since 2022/11/18
 */
public class TraceFilter extends OncePerRequestFilter {
    public static final String TRACE_ID = "requestId";

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID);
        if (StringUtils.isNotBlank(traceId)) {
            MDC.put(TRACE_ID, traceId);
        } else if (StringUtils.isNotBlank(MDC.get(TRACE_ID))) {
            MDC.put(TRACE_ID, MDC.get(TRACE_ID));
        } else {
            MDC.put(TRACE_ID, IdUtils.simpleUUID());
        }

        filterChain.doFilter(request, response);

        MDC.remove(TRACE_ID);
    }
}
