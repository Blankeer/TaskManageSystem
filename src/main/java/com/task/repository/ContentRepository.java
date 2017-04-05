package com.task.repository;

import com.task.bean.Content;
import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by blanke on 17-1-25.
 */
public interface ContentRepository extends JpaRepository<Content, Integer> {
    List<Content> findByTaskAndUser(Task task, User user);
}
