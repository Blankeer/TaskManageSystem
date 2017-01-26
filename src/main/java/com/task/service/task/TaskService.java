package com.task.service.task;

import com.task.bean.Task;

/**
 * Created by blanke on 17-1-27.
 */
public interface TaskService {
    Task addTask(Task task);

    Task updateTask(int id, Task task);
}
