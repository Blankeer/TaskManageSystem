package com.task.repository;

import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

/**
 * Created by blanke on 17-1-25.
 */
public interface TaskRepository extends PagingAndSortingRepository<Task, Integer> {
    Page<Task> findByUsersAndTitleContainingAndPublishTimeGreaterThanEqual
            (Pageable pageable, User user, String title, Date nowDate);

    //admin 调用,查看所有任务
    Page<Task> findByTitleContaining(Pageable pageable, String title);

    Page<Task> findByLikeUsers(Pageable pageable, User user);

}
