package org.sunshine.core.log.util;

import org.sunshine.core.log.model.LogAbstract;
import org.sunshine.core.tool.config.ServerInfo;
import org.sunshine.core.tool.util.StringPool;
import org.sunshine.core.tool.util.UrlUtils;
import org.sunshine.core.tool.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Log 相关工具
 *
 * @author Teamo
 */
public class LogAbstractUtils {

    /**
     * 向log中添加补齐request的信息
     *
     * @param request     请求
     * @param logAbstract 日志基础类
     */
    public static void addRequestInfoToLog(HttpServletRequest request, LogAbstract logAbstract) {
        if (Objects.nonNull(request)) {
            logAbstract.setRemoteIp(WebUtils.getIP(request));
            logAbstract.setUserAgent(request.getHeader(WebUtils.USER_AGENT_HEADER));
            logAbstract.setRequestUri(UrlUtils.getPath(request.getRequestURI()));
            logAbstract.setMethod(request.getMethod());
            logAbstract.setParams(WebUtils.getRequestParamString(request));
        }
    }

    /**
     * 向log中添加补齐其他的信息（eg：server等）
     *
     * @param logAbstract 日志基础类
     * @param serverInfo  服务信息
     */
    public static void addOtherInfoToLog(LogAbstract logAbstract, ServerInfo serverInfo) {
        logAbstract.setServerHost(serverInfo.getHostName());
        logAbstract.setServerIp(serverInfo.getIpWithPort());
        logAbstract.setGmtCreate(LocalDateTime.now());

        //这里判断一下params为null的情况
        if (logAbstract.getParams() == null) {
            logAbstract.setParams(StringPool.EMPTY);
        }
    }
}
