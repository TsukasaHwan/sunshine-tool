package org.sunshine.core.log.filter;

import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.sunshine.core.tool.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            MDC.put(TRACE_ID, StringUtils.randomUUID());
        }

        filterChain.doFilter(request, response);

        MDC.remove(TRACE_ID);
    }
}
