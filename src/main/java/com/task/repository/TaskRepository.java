package com.task.repository;

import com.task.bean.Task;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

/**
 * Created by blanke on 17-1-25.
 */
public interface TaskRepository extends DataTablesRepository<Task, Integer> {
}
