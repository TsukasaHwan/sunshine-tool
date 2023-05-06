package org.sunshine.core.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    int insertBatchSomeColumn(Collection<T> entityList);
}
