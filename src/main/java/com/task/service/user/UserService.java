package com.task.service.user;

import com.task.bean.User;

/**
 * Created by blanke on 17-1-25.
 */
public interface UserService {
    Object login(String account, String pwd);

    boolean register(String account, String pwd);

    boolean logout(String token);

    String getToken(User user);

    void clearToken(User user);
}
