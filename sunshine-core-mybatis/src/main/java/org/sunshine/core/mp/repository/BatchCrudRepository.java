package org.sunshine.core.mp.repository;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.sunshine.core.mp.mapper.BatchBaseMapper;

import java.util.Collection;

/**
 * @author Teamo
 * @since 2025/2/11
 */
public class BatchCrudRepository<M extends BatchBaseMapper<T>, T> extends CrudRepository<M, T> implements IBatchRepository<T> {

    @Override
    public boolean saveBatchSomeColumn(Collection<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        return SqlHelper.retBool(getBaseMapper().insertBatchSomeColumn(entityList));
    }
}
