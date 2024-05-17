package org.sunshine.core.mp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.sunshine.core.mp.mapper.BatchBaseMapper;
import org.sunshine.core.mp.service.IBaseService;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public class BaseServiceImpl<M extends BatchBaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    @Override
    public boolean saveBatchSomeColumn(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        return SqlHelper.retBool(getBaseMapper().insertBatchSomeColumn(entityList));
    }
}
