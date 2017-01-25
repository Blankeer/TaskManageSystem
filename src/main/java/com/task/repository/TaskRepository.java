package com.task.repository;

import com.task.bean.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface TaskRepository extends JpaRepository<Task, Integer> {

}
