package com.task.controller;

import com.task.bean.User;
import com.task.bean.request.LoginRequest;
import com.task.bean.response.BaseMessageResponse;
import com.task.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest request) {
        Object obj = userService.login(request.getEmail(), request.getPwd());
        if (obj != null) {
            if (obj instanceof User) {
                return ResponseEntity.ok(obj);
            } else if (obj instanceof Integer) {
                int state = (int) obj;
                ResponseEntity<BaseMessageResponse> res = null;
                if (state == -1) {
                    res = new ResponseEntity<>(
                            new BaseMessageResponse("提交数据不符合规范"), HttpStatus.BAD_REQUEST);
                } else if (state == -2) {
                    res = new ResponseEntity<>(
                            new BaseMessageResponse("密码不正确"), HttpStatus.BAD_REQUEST);
                } else {
                    res = new ResponseEntity<>(
                            new BaseMessageResponse("用户不存在"), HttpStatus.BAD_REQUEST);
                }
                return res;
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LoginRequest request) {
        // TODO: 17-1-26 验证
        boolean res = userService.register(request.getEmail(), request.getPwd());
        if (!res) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户已存在"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
