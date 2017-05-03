package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.User;
import com.task.bean.request.ChangePwdRequest;
import com.task.bean.request.LoginRequest;
import com.task.bean.response.BaseMessageResponse;
import com.task.bean.response.UserListResponse;
import com.task.bean.response.UserLoginResponse;
import com.task.config.ProjectConfig;
import com.task.mail.MailUtils;
import com.task.repository.ContentRepository;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import com.task.utils.Md5Utils;
import com.task.utils.RegexUtils;
import com.task.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    @Autowired
    MailUtils mailUtils;

    public String getToken(User user) {
        return Md5Utils.md5(new Random().nextInt(1024) + user.getEmail()
                + System.currentTimeMillis());
    }

    //随机生成验证码
    private String getRandomCaptcha() {
        Random random = new Random();
        return ((char) (random.nextInt(25) + 'A')) +
                "" + (random.nextInt(89999) + 10000);
    }

    //检查验证码频率限制
    private boolean checkCaptchaFrequency(Date oldDate) {
        if (oldDate == null) {
            return true;
        }
        Date now = new Date();
        long dt = now.getTime() - oldDate.getTime();
        return dt > ProjectConfig.FREQUENCY_CAPTCHA * 60 * 1000;
    }

    //检查验证码是否过期
    private boolean checkCaptchaExpire(Date oldDate) {
        if (oldDate == null) {
            return true;
        }
        Date now = new Date();
        long dt = now.getTime() - oldDate.getTime();
        return dt < ProjectConfig.EXPIRE_CAPTCHA * 60 * 1000;
    }


    public void clearToken(User user) {
        user.setToken("");
        userRepository.save(user);
    }

    @PostMapping("/login/")
    public ResponseEntity login(@RequestBody LoginRequest request) {
        //首先判断账号和密码是否为空
        if (TextUtils.isEmpty(request.getEmail())
                || TextUtils.isEmpty(request.getPwd())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户名或密码不能为空"), HttpStatus.BAD_REQUEST);
        }
        String account = request.getEmail().trim();
        String pwd = request.getPwd().trim();
        User user = userRepository.findByEmail(account);
        if (user != null && user.isActivate()) {
            if (user.getPwd().equals(Md5Utils.md5(pwd))) {
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

    /**
     * 根据 token 校验用户
     *
     * @return
     */
    @TokenValid
    @GetMapping("/token/check")
    public ResponseEntity checkToken(User user) {
        return ResponseEntity.ok(UserLoginResponse.wrap(user));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null && user.isActivate()) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户已存在"), HttpStatus.BAD_REQUEST);
        }
        if (TextUtils.isEmpty(request.getCaptcha())
                || user == null || TextUtils.isEmpty(user.getCaptcha())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("请先获取验证码"), HttpStatus.BAD_REQUEST);
        }
        if (!checkCaptchaExpire(user.getCaptchaCreatedAt())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("验证码过期,请重新获取"), HttpStatus.BAD_REQUEST);
        }
        if (!request.getCaptcha().equalsIgnoreCase(user.getCaptcha())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("验证码输入不正确"), HttpStatus.BAD_REQUEST);
        }
        user.setEmail(request.getEmail());
        user.setPwd(Md5Utils.md5(request.getPwd()));
        user.setActivate(true);
        user.setCaptcha("");
        userRepository.save(user);
        return ResponseEntity.ok(new BaseMessageResponse(""));
    }

    @PostMapping("/find-pwd")
    public ResponseEntity findPwd(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !user.isActivate()) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("用户不存在"), HttpStatus.BAD_REQUEST);
        }
        if (TextUtils.isEmpty(request.getCaptcha())
                || TextUtils.isEmpty(user.getCaptcha())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("请先获取验证码"), HttpStatus.BAD_REQUEST);
        }
        if (!checkCaptchaExpire(user.getCaptchaCreatedAt())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("验证码过期,请重新获取"), HttpStatus.BAD_REQUEST);
        }
        if (!request.getCaptcha().equalsIgnoreCase(user.getCaptcha())) {
            return new ResponseEntity<>(
                    new BaseMessageResponse("验证码输入不正确"), HttpStatus.BAD_REQUEST);
        }
        user.setPwd(Md5Utils.md5(request.getPwd()));
        user.setActivate(true);
        user.setCaptcha("");
        userRepository.save(user);
        return ResponseEntity.ok(new BaseMessageResponse(""));
    }

    @GetMapping("/captcha")
    public ResponseEntity getCaptcha(@RequestParam String account) {
        User user = userRepository.findByEmail(account);
        if (!RegexUtils.isEmail(account)) {
            return new ResponseEntity(new BaseMessageResponse("邮箱格式不正确")
                    , HttpStatus.BAD_REQUEST);
        }
        if (user != null && !checkCaptchaFrequency(user.getCaptchaCreatedAt())) {
            return new ResponseEntity(new BaseMessageResponse("请求过于频繁")
                    , HttpStatus.BAD_REQUEST);
        }
        String captcha = getRandomCaptcha();//获得随机验证码
        if (user != null && checkCaptchaExpire(user.getCaptchaCreatedAt())) {//如果验证码没有过期
            captcha = user.getCaptcha();//重复使用未过期的验证码
        }
        boolean succ = mailUtils.sendCaptchaEmail(account, captcha);
        if (succ) {
            if (user == null) {
                user = new User();
            }
            user.setEmail(account);
            user.setCaptcha(captcha);
            user.setCaptchaCreatedAt(new Date());
            user.setActivate(false);
            userRepository.save(user);
            return ResponseEntity.ok(new BaseMessageResponse(""));
        } else {
            return new ResponseEntity(new BaseMessageResponse("发送验证码失败,请联系管理员")
                    , HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        return ResponseEntity.ok(new BaseMessageResponse("修改密码成功"));
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
        return ResponseEntity.ok(new BaseMessageResponse("修改昵称成功"));
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
