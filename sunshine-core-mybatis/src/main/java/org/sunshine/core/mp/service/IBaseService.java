package org.sunshine.core.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * 插入（批量）仅适用于mysql
     *
     * @param entityList 实体对象集合
     * @return 是否成功
     */
    boolean saveBatchSomeColumn(Collection<T> entityList);
}
