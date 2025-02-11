package org.sunshine.core.mp.service.impl;

import org.sunshine.core.mp.mapper.BatchBaseMapper;
import org.sunshine.core.mp.repository.BatchCrudRepository;
import org.sunshine.core.mp.service.IBatchService;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public class BatchServiceImpl<M extends BatchBaseMapper<T>, T> extends BatchCrudRepository<M, T> implements IBatchService<T> {

}
