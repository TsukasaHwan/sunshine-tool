package org.sunshine.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunshine.core.log.model.OperateLog;

/**
 * 默认实现：仅打印日志，不入库。
 * 当 classpath 中没有 mybatis-plus 时使用此实现。
 *
 * @author Teamo
 * @since 2026/05/15
 */
public class LoggingOperateLogPersister implements OperateLogPersister {

    private static final Logger log = LoggerFactory.getLogger(LoggingOperateLogPersister.class);

    @Override
    public void persist(OperateLog operateLog) {
        log.info("operate-log: {}", operateLog);
    }
}