package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.Task;
import com.task.bean.User;
import com.task.bean.response.TaskListResponse;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import com.task.service.task.TaskService;
import com.task.utils.DataTableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;

    @TokenValid
    @GetMapping("/tasks")
    public ResponseEntity getAllTasks(DataTablesInput input, User user) {
        List<Column> columns = input.getColumns();
        List<Column> deleteColumns = new ArrayList<>();
        //需要删除data为空的字段，防止报错
        for (Column column : columns) {
            if (column.getData() == null || column.getData().trim().length() == 0) {
                deleteColumns.add(column);
            }
        }
        columns.removeAll(deleteColumns);
        DataTablesOutput<Task> tasks_row = taskRepository.findAll(input, new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Set<User>> users = root.get("users");
                return criteriaBuilder.isMember(user, users);
            }
        });
        //把task转成taskResponse
        List<TaskListResponse> tasks = new ArrayList<>();
        DataTablesOutput result = DataTableUtils.convert(tasks_row);
        if (tasks_row.getData() != null) {
            for (Task task : tasks_row.getData()) {
                tasks.add(TaskListResponse.wrap(task));
            }
        }
        result.setData(tasks);
        return ResponseEntity.ok(result);
    }

    @AdminValid
    @PostMapping("/tasks")
    public ResponseEntity addTask(@RequestBody Task task) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(taskService.addTask(task));
    }

    @AdminValid
    @PostMapping("/tasks/{id}")
    public ResponseEntity updateTask(@PathVariable int id, @RequestBody Task task) {
        // TODO: 17-1-27 验证
        Task newTask = taskService.updateTask(id, task);
        if (newTask != null) {
            return ResponseEntity.ok(newTask);
        }
        return ResponseEntity.notFound().build();
    }

    @AdminValid
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable int id) {
        // TODO: 17-1-27 验证
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @AdminValid
    @GetMapping("/tasks/{id}/fields")
    public ResponseEntity getTaskFields(@PathVariable int id) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(taskService.getFields(id));
    }
}
