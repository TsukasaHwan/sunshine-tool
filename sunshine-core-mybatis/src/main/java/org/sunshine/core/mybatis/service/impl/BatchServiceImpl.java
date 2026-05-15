package org.sunshine.core.mybatis.service.impl;

import org.sunshine.core.mybatis.mapper.BatchBaseMapper;
import org.sunshine.core.mybatis.repository.BatchCrudRepository;
import org.sunshine.core.mybatis.service.IBatchService;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public class BatchServiceImpl<M extends BatchBaseMapper<T>, T> extends BatchCrudRepository<M, T> implements IBatchService<T> {

}
