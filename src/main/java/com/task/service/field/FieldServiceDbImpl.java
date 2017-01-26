package com.task.service.field;

import com.task.bean.Config;
import com.task.bean.Field;
import com.task.bean.Task;
import com.task.repository.ConfigRepository;
import com.task.repository.FieldRepository;
import com.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by blanke on 17-1-27.
 */
@Service
public class FieldServiceDbImpl implements FieldService {
    @Autowired
    FieldRepository fieldRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ConfigRepository configRepository;

    @Override
    public Field addField(Field field, int tid, int cid) {
        Config config = configRepository.findOne(cid);
        if (config != null) {
            Task task = taskRepository.findOne(tid);
            if (task != null) {
                field.setTask(task);
                field.setConfig(config);
                return fieldRepository.save(field);
            }
        }
        return null;
    }

    @Override
    public Field updateField(Field field, int id) {
        Field oldField = fieldRepository.findOne(id);
        if (oldField != null) {
            oldField.setDescription(field.getDescription());
            oldField.setConfig(field.getConfig());
            oldField.setName(field.getName());
            return fieldRepository.save(oldField);
        }
        return null;
    }

    @Override
    public boolean deleteField(int id) {
        fieldRepository.delete(id);
        return true;
    }
}
