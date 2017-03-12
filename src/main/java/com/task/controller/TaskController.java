package com.task.controller;

import com.task.bean.Task;
import com.task.bean.User;
import com.task.repository.UserRepository;
import com.task.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/tasks")
    public ResponseEntity getAllTasks(DataTablesInput input, User user) {
//        return ResponseEntity.ok(taskService.getAllTask(page, size, sort));
        if (user == null) {//test
            user = userRepository.findOne(1);
        }
        Set<Task> tasks = user.getTasks();

        return ResponseEntity.ok(taskService.getAllTask(input));
    }

    //    @AdminValid
    @PostMapping("/tasks/add")
    public ResponseEntity addTask(@RequestBody Task task) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(taskService.addTask(task));
    }

    //    @AdminValid
    @PostMapping("/tasks/{id}")
    public ResponseEntity updateTask(@PathVariable int id, @RequestBody Task task) {
        // TODO: 17-1-27 验证
        Task newTask = taskService.updateTask(id, task);
        if (newTask != null) {
            return ResponseEntity.ok(newTask);
        }
        return ResponseEntity.notFound().build();
    }

    //    @AdminValid
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable int id) {
        // TODO: 17-1-27 验证
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    //    @AdminValid
    @GetMapping("/tasks/{id}/fields")
    public ResponseEntity getTaskFields(@PathVariable int id) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(taskService.getFields(id));
    }
}
