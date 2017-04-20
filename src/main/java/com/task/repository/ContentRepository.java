package com.task.repository;

import com.task.bean.Content;
import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by blanke on 17-1-25.
 */
public interface ContentRepository extends PagingAndSortingRepository<Content, Integer> {
    List<Content> findByTaskAndUser(Task task, User user);

    Page<Content> findByTask(Pageable pageable, Task task);
}
