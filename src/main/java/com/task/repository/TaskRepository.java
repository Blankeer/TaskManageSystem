package com.task.repository;

import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface TaskRepository extends PagingAndSortingRepository<Task, Integer> {
    Page<Task> findByUsers(Pageable pageable, User user);
    Page<Task> findByUsersAndTitleContaining(Pageable pageable, User user,String title);
}
