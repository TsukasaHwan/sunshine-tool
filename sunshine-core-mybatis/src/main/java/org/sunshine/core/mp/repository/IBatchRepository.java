package org.sunshine.core.mp.repository;

import com.baomidou.mybatisplus.extension.repository.IRepository;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2025/2/11
 */
public interface IBatchRepository<T> extends IRepository<T> {

    /**
     * 插入（批量）仅适用于mysql
     *
     * @param entityList 实体对象集合
     * @return 是否成功
     */
    boolean saveBatchSomeColumn(Collection<T> entityList);
}
