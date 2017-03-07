package com.task.service.task;

import com.task.bean.Field;
import com.task.bean.Task;
import com.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by blanke on 17-1-27.
 */
@Service
public class TaskServiceDbImpl implements TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Override
    public DataTablesOutput<Task> getAllTask(DataTablesInput dataTablesInput) {
        return taskRepository.findAll(dataTablesInput);
    }

    @Override
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(int id, Task task) {
        Task oldTask = taskRepository.findOne(id);
        if (oldTask != null) {
            oldTask.setTitle(task.getTitle());
            oldTask.setDescription(task.getDescription());
            return taskRepository.save(oldTask);
        }
        return null;
    }

    @Override
    public boolean deleteTask(int id) {
        taskRepository.delete(id);
        return true;
    }

    @Override
    public List<Field> getFields(int tid) {
        Task task = taskRepository.findOne(tid);
        List<Field> fields = new ArrayList<>();
        if (task != null) {
            Set<Field> fields1 = task.getFields();
            for (Field field : fields1) {
                field.setTask(null);
                fields.add(field);
            }
        }
        return fields;
    }
}
