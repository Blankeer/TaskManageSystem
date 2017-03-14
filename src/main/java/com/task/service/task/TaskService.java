package com.task.service.task;

import com.task.bean.Field;
import com.task.bean.Task;
import com.task.bean.User;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

/**
 * Created by blanke on 17-1-27.
 */
public interface TaskService {
    DataTablesOutput<Task> getAllTask(DataTablesInput dataTablesInput, User user);

    Task addTask(Task task);

    Task updateTask(int id, Task task);

    boolean deleteTask(int id);

    List<Field> getFields(int tid);
}
