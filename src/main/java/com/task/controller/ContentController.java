package com.task.controller;

import com.task.annotation.TokenValid;
import com.task.bean.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by blanke on 2017/3/19.
 */
@RestController
public class ContentController {

    @TokenValid
    @GetMapping("/content")
    public ResponseEntity getAllTasks(@RequestParam("task_id") int taskId, User user) {
//        Content content = mContentService.getContent(taskId, user);
//        if (content == null) {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok("");
    }
}
