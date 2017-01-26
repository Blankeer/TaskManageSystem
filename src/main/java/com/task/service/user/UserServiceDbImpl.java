package com.task.service.user;

import com.task.bean.User;
import com.task.repository.UserRepository;
import com.task.utils.Md5Utils;
import com.task.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by blanke on 17-1-25.
 */
@Service
public class UserServiceDbImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public Object login(String account, String pwd) {
        if (TextUtils.isEmpty(account)
                || TextUtils.isEmpty(pwd)) {
            return -1;//验证不通过
        }
        account = account.trim();
        pwd = pwd.trim();
        User user = userRepository.findByEmail(account);
        if (user != null) {
            if (user.getPwd().equals(pwd)) {
                user.setToken(getToken(user));
                userRepository.save(user);
                user.setPwd("");
                return user;
            }
            return -2;//密码不正确
        }
        return -3;//不存在该用户
    }

    @Override
    public boolean register(String account, String pwd) {
        if (TextUtils.isEmpty(account)
                || TextUtils.isEmpty(pwd)) {
            return false;
        }
        account = account.trim();
        pwd = pwd.trim();
        User user = userRepository.findByEmail(account);
        if (user == null) {
            user = new User();
            user.setEmail(account);
            user.setPwd(Md5Utils.md5(pwd));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean logout(User user) {
        clearToken(user);
        return true;
    }

    @Override
    public boolean changePwd(User user, String oldPwd, String newPwd) {
        if (user.getPwd().equals(Md5Utils.md5(oldPwd))) {
            user.setPwd(Md5Utils.md5(newPwd));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public String getToken(User user) {
        return Md5Utils.md5(new Random().nextInt(1024) + user.getEmail()
                + System.currentTimeMillis());
    }

    @Override
    public void clearToken(User user) {
        user.setToken("");
        userRepository.save(user);
    }
}
