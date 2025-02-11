package org.sunshine.core.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sunshine.core.mp.repository.IBatchRepository;

/**
 * @author Teamo
 * @since 2023/5/6
 */
public interface IBatchService<T> extends IService<T>, IBatchRepository<T> {

}
