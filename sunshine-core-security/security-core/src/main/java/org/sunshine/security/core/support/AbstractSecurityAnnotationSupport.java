package org.sunshine.security.core.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Teamo
 * @since 2023/3/23
 */
public abstract class AbstractSecurityAnnotationSupport implements InitializingBean, ApplicationContextAware {

    protected ApplicationContext context;

    protected List<AntPathRequestMatcher> antPatterns = new ArrayList<>();

    /**
     * Is there any annotation
     *
     * @param requestMappingInfo {@link RequestMappingInfo}
     * @param handlerMethod      {@link HandlerMethod}
     * @return boolean
     */
    protected abstract boolean hasAnnotation(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class);
        mapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            boolean hasAnnotation = hasAnnotation(requestMappingInfo, handlerMethod);
            if (hasAnnotation) {
                mappingRequestMethodUrl(requestMappingInfo);
            }
        });
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    protected void mappingRequestMethodUrl(RequestMappingInfo requestMappingInfo) {
        if (requestMappingInfo == null) {
            return;
        }

        // Handle different path matching strategies
        Set<String> patterns = new LinkedHashSet<>(16);
        PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
        if (pathPatternsCondition == null) {
            PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
            if (patternsRequestCondition == null) {
                return;
            }
            patterns.addAll(patternsRequestCondition.getPatterns());
        } else {
            patterns.addAll(pathPatternsCondition.getPatternValues());
        }

        requestMappingInfo.getMethodsCondition().getMethods().forEach(requestMethod -> {
            HttpMethod httpMethod = HttpMethod.valueOf(requestMethod.name());

            patterns.forEach(pattern -> antPatterns.add(AntPathRequestMatcher.antMatcher(httpMethod, pattern)));
        });
    }

    public List<AntPathRequestMatcher> getAntPatterns() {
        return antPatterns;
    }

    public void setAntPatterns(List<AntPathRequestMatcher> antPatterns) {
        this.antPatterns = antPatterns;
    }
}
