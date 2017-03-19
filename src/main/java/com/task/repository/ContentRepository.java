package com.task.repository;

import com.task.bean.Content;
import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface ContentRepository extends JpaRepository<Content, Integer> {
    Content findByTaskAndUser(Task task, User user);
}
