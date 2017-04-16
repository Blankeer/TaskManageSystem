package com.task.repository;

import com.task.bean.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface ConfigRepository extends PagingAndSortingRepository<Config, Integer> {
    Page<Config> findNameContaining(Pageable pageable, String name);
}
