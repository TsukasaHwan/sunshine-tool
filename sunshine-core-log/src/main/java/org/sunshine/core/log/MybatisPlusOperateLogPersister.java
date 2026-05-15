package org.sunshine.core.log;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.sunshine.core.log.model.OperateLog;

/**
 * 基于 mybatis-plus 的操作日志持久化实现。
 * 仅当 classpath 存在 {@link SqlHelper} 时使用。
 *
 * @author Teamo
 * @since 2026/05/15
 */
public class MybatisPlusOperateLogPersister implements OperateLogPersister {

    @Override
    public void persist(OperateLog operateLog) {
        SqlHelper.execute(OperateLog.class, baseMapper -> baseMapper.insert(operateLog));
    }
}