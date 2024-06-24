package org.sunshine.core.log.aspect;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.sunshine.core.tool.util.ReflectionUtils;
import org.sunshine.core.tool.util.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Teamo
 * @since 2021/12/20
 */
@Aspect
@AutoConfiguration
@Profile({"dev", "test"})
public class RequestLogAspect {

    public static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final String STANDARD_MULTIPART_FILE = "StandardMultipartFile";

    @Around("execution(!static org.sunshine.core.tool.api.response.Result *(..)) && " +
            "(within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController))")
    @SuppressWarnings("unchecked")
    public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        final Map<String, Object> paramMap = new HashMap<>(16);
        MethodParameter methodParameter = null;
        for (int i = 0; i < args.length; i++) {
            methodParameter = new SynthesizingMethodParameter(method, i);
            methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
            PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
            if (Objects.nonNull(pathVariable)) {
                continue;
            }
            RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
            String parameterName = methodParameter.getParameterName();
            RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
            if (Objects.nonNull(requestParam) && StringUtils.isNotEmpty(requestParam.value())) {
                parameterName = requestParam.value();
            }
            Object value = args[i];
            if (Objects.nonNull(requestBody) && Objects.nonNull(value)) {
                if (value instanceof List) {
                    paramMap.put(parameterName, value);
                } else {
                    paramMap.putAll(BeanMap.create(value));
                }
                continue;
            }
            if (value instanceof HttpServletRequest) {
                paramMap.putAll(((HttpServletRequest) value).getParameterMap());
            } else if (value instanceof WebRequest) {
                paramMap.putAll(((WebRequest) value).getParameterMap());
            } else if (value instanceof MultipartFile || value instanceof MultipartFile[]) {
                MultipartFile[] multipartFiles = new MultipartFile[1];
                if (value instanceof MultipartFile[]) {
                    multipartFiles = (MultipartFile[]) value;
                } else {
                    multipartFiles[0] = (MultipartFile) value;
                }
                for (MultipartFile multipartFile : multipartFiles) {
                    String name = multipartFile.getName();
                    String fileName = multipartFile.getOriginalFilename();
                    paramMap.put(name, fileName);
                }
            } else if (value instanceof HttpServletResponse) {
                // ignore
            } else if (value instanceof InputStream) {
                // ignore
            } else if (value instanceof InputStreamSource) {
                // ignore
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                AtomicBoolean isSkip = new AtomicBoolean(false);
                for (Object o : list) {
                    if (STANDARD_MULTIPART_FILE.equalsIgnoreCase(o.getClass().getSimpleName())) {
                        isSkip.set(true);
                        break;
                    }
                }
                if (isSkip.get()) {
                    paramMap.put(parameterName, "此参数不能序列化为json");
                } else {
                    paramMap.put(parameterName, list);
                }
            } else {
                Field field = null;
                if (Objects.nonNull(value)) {
                    field = ReflectionUtils.findField(value.getClass(), null, MultipartFile.class);
                }
                if (Objects.nonNull(field)) {
                    paramMap.put(parameterName, "此参数不能序列化为json");
                } else {
                    paramMap.put(parameterName, value);
                }
            }
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();

        // 构建成一条长 日志，避免并发下日志错乱
        long startNs = this.printBeforeReqLog(request, paramMap);

        return this.printAfterReqLog(point, request, startNs);
    }

    /**
     * 打印请求前日志
     *
     * @param request  {HttpServletRequest}
     * @param paramMap {Map}
     * @return 执行开始时间
     */
    private long printBeforeReqLog(HttpServletRequest request, Map<String, Object> paramMap) {
        StringBuilder beforeReqLog = new StringBuilder(300);
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        // 日志参数
        List<Object> beforeReqArgs = new ArrayList<>();
        beforeReqLog.append("\n\n================  Request Start  ================\n");
        // 打印路由
        beforeReqLog.append("===> {}: {}");
        beforeReqArgs.add(requestMethod);
        beforeReqArgs.add(requestUri);
        // 请求参数
        if (paramMap.isEmpty()) {
            beforeReqLog.append("\n");
        } else {
            beforeReqLog.append(" Parameters: {}\n");
            beforeReqArgs.add(JSON.toJSONString(paramMap));
        }
        // 打印请求头
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            String headerValue = request.getHeader(headerName);
            beforeReqLog.append("===Headers===  {} : {}\n");
            beforeReqArgs.add(headerName);
            beforeReqArgs.add(headerValue);
        }
        beforeReqLog.append("================  Request End   ================\n");
        // 打印执行时间
        long startNs = System.nanoTime();
        log.info(beforeReqLog.toString(), beforeReqArgs.toArray());
        return startNs;
    }

    /**
     * 打印请求后日志
     *
     * @param point   {ProceedingJoinPoint}
     * @param request {HttpServletRequest}
     * @param startNs 执行开始时间
     * @return {Object}
     * @throws Throwable {Throwable}
     */
    private Object printAfterReqLog(ProceedingJoinPoint point, HttpServletRequest request, long startNs) throws Throwable {
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();
        // aop 执行后的日志
        StringBuilder afterReqLog = new StringBuilder(200);
        // 日志参数
        List<Object> afterReqArgs = new ArrayList<>();
        afterReqLog.append("\n\n================  Response Start  ================\n");
        try {
            Object result = point.proceed();
            // 打印返回结构体
            afterReqLog.append("===Result===  {}\n");
            afterReqArgs.add(JSON.toJSONString(result));
            return result;
        } finally {
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            afterReqLog.append("<=== {}: {} ({} ms)\n");
            afterReqArgs.add(requestMethod);
            afterReqArgs.add(requestUri);
            afterReqArgs.add(tookMs);
            afterReqLog.append("================  Response End   ================\n");
            log.info(afterReqLog.toString(), afterReqArgs.toArray());
        }
    }
}
