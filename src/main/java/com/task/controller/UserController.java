package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.User;
import com.task.bean.request.ChangePwdRequest;
import com.task.bean.request.LoginRequest;
import com.task.bean.response.BaseMessageResponse;
import com.task.bean.response.UserListResponse;
import com.task.bean.response.UserLoginResponse;
import com.task.repository.ContentRepository;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import com.task.utils.Md5Utils;
import com.task.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    UserRepository userRepository;
    @Autowired
    ContentRepository contentRepository;
    @Autowired
    TaskRepository taskRepository;

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
                return ResponseEntity.ok(UserLoginResponse.wrap(user));
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
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户已存在"), HttpStatus.BAD_REQUEST);
        }
        user = new User();
        user.setEmail(request.getEmail());
        user.setPwd(Md5Utils.md5(request.getPwd()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @TokenValid
    @GetMapping("/logout")
    public ResponseEntity logout(User user) {
        clearToken(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @TokenValid
    @PutMapping("/change_pwd")
    public ResponseEntity changePwd(User user, @RequestBody ChangePwdRequest request) {
        if (TextUtils.isEmpty(request.getOldPwd())
                || TextUtils.isEmpty(request.getNewPwd())) {
            // TODO: 17-1-27 密码验证
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("旧密码或新密码为空"));
        }
        if (!user.getPwd().equals(Md5Utils.md5(request.getOldPwd()))) {
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("旧密码错误"));
        }
        user.setPwd(Md5Utils.md5(request.getNewPwd()));
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @TokenValid
    @PutMapping("/change_nickname")
    public ResponseEntity changeNickname(User user,
                                         @RequestParam(name = "nickname")
                                                 String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            // TODO: 17-1-27 昵称验证
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("昵称不规范"));
        }
        user.setNickName(nickname);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @AdminValid
    @GetMapping("/users")
    public ResponseEntity getUserList(@RequestParam(value = "page", defaultValue = "0")
                                              int page,
                                      @RequestParam(value = "size", defaultValue = "10")
                                              int size) {
        Pageable pageable = new PageRequest(page, size);
        Page<User> users = userRepository.findAll(pageable);
        Page<UserListResponse> res = users.map(new Converter<User, UserListResponse>() {
            @Override
            public UserListResponse convert(User user) {
                return UserListResponse.wrap(user);
            }
        });
        return ResponseEntity.ok(res);
    }

    @AdminValid
    @GetMapping("/users/{uid}")
    public ResponseEntity getUserDetail(@PathVariable int uid) {
        User user = userRepository.findOne(uid);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserListResponse.wrap(user));
    }

    @AdminValid
    @PutMapping("/users/{uid}")
    public ResponseEntity updateUser(@PathVariable int uid,
                                     @RequestBody UserListResponse request) {
        User user = userRepository.findOne(uid);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setEmail(request.email);
        user.setNickName(request.nickName);
        userRepository.save(user);
        return ResponseEntity.ok(UserListResponse.wrap(user));
    }

    @AdminValid
    @DeleteMapping("/users/{uid}")
    public ResponseEntity deleteUser(@PathVariable int uid) {
        User user = userRepository.findOne(uid);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @TokenValid
    @GetMapping("/user/profile")
    public ResponseEntity getUserProfile(User user) {
        return ResponseEntity.ok(UserListResponse.wrap(user));
    }
}
