package com.task.service.task;

import com.task.bean.Field;
import com.task.bean.Task;

import java.util.List;

/**
 * Created by blanke on 17-1-27.
 */
public interface TaskService {
    Task addTask(Task task);

    Task updateTask(int id, Task task);

    boolean deleteTask(int id);

    List<Field> getFields(int tid);
}
