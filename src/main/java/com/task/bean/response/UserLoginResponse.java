package com.task.bean.response;

import com.task.bean.User;

/**
 * Created by blanke on 2017/4/26.
 */
public class UserLoginResponse {
    public int id;
    public String email;
    public String nickName;
    public String token;

    public static UserLoginResponse wrap(User user) {
        UserLoginResponse userListResponse = new UserLoginResponse();
        userListResponse.id = user.getId();
        userListResponse.email = user.getEmail();
        userListResponse.nickName = user.getNickName();
        userListResponse.token = user.getToken();
        return userListResponse;
    }


}
