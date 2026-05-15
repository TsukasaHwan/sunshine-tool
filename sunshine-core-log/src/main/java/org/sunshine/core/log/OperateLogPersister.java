package org.sunshine.core.log;

import org.sunshine.core.log.model.OperateLog;

/**
 * 操作日志持久化器。默认提供 noop 与基于 mybatis-plus 的实现，
 * 业务方可通过自定义实现替换为 ES、Kafka 等任意存储。
 *
 * @author Teamo
 * @since 2026/05/15
 */
@FunctionalInterface
public interface OperateLogPersister {

    /**
     * 持久化一条操作日志
     *
     * @param operateLog 操作日志
     */
    void persist(OperateLog operateLog);
}