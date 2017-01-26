package com.task.controller;

import com.task.bean.User;
import com.task.bean.request.LoginRequest;
import com.task.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by blanke on 17-1-25.
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String test() {
        return userService.test();
    }

    @PostMapping("/login")
    public Object login(@RequestBody LoginRequest request) {
        Object obj = userService.login(request.getAccount(), request.getPwd());
        if (obj != null && obj instanceof User) {
            return obj;
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
