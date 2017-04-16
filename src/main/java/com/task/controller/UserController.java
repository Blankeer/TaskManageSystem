package com.task.controller;

import com.task.annotation.TokenValid;
import com.task.bean.User;
import com.task.bean.request.ChangePwdRequest;
import com.task.bean.request.LoginRequest;
import com.task.bean.response.BaseMessageResponse;
import com.task.repository.UserRepository;
import com.task.service.user.UserService;
import com.task.utils.Md5Utils;
import com.task.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * Created by blanke on 17-1-25.
 */
@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    public String getToken(User user) {
        return Md5Utils.md5(new Random().nextInt(1024) + user.getEmail()
                + System.currentTimeMillis());
    }

    public void clearToken(User user) {
        user.setToken("");
        userRepository.save(user);
    }

    @PostMapping("/login/")
    public ResponseEntity login(@RequestBody LoginRequest request) {
//        Object obj = userService.login(request.getEmail(), request.getPwd());
        if (TextUtils.isEmpty(request.getEmail())
                || TextUtils.isEmpty(request.getPwd())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户名或密码不能为空"), HttpStatus.BAD_REQUEST);
        }
        String account = request.getEmail().trim();
        String pwd = request.getPwd().trim();
        User user = userRepository.findByEmail(account);
        if (user != null) {
            if (user.getPwd().equals(pwd)) {
                user.setToken(getToken(user));
                userRepository.save(user);
                user.setPwd("");
                return ResponseEntity.ok(user);
            } else {
                return new ResponseEntity<>(
                        new BaseMessageResponse("密码不正确"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户不存在"), HttpStatus.NOT_FOUND);
        }
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

    @TokenValid
    @GetMapping("/logout")
    public ResponseEntity logout(User user) {
        userService.logout(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @TokenValid
    @PutMapping("/change_pwd")
    public ResponseEntity changePwd(User user, @RequestBody ChangePwdRequest request) {
        if (TextUtils.isEmpty(request.getOldPwd())
                || TextUtils.isEmpty(request.getNewPwd())) {
            // TODO: 17-1-27 密码验证
            return ResponseEntity.badRequest().body("旧密码或新密码为空");
        }
        boolean res = userService.changePwd(user, request.getOldPwd().trim(),
                request.getNewPwd().trim());
        if (!res) {
            return ResponseEntity.badRequest().body("旧密码错误");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @TokenValid
    @PutMapping("/change_nickname")
    public ResponseEntity changeNickname(User user,
                                         @RequestParam(name = "nickname")
                                                 String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            // TODO: 17-1-27 昵称验证
            return ResponseEntity.badRequest().body("昵称不规范");
        }
        userService.setNiceName(user, nickname);
        return ResponseEntity.ok().build();
    }
}
