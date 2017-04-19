package com.task.bean.response;

import com.task.bean.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by blanke on 2017/4/17.
 */
public class UserListResponse {
    public int id;
    public String email;
    public String nickName;

    public static UserListResponse wrap(User user) {
        UserListResponse userListResponse = new UserListResponse();
        userListResponse.id = user.getId();
        userListResponse.email = user.getEmail();
        userListResponse.nickName = user.getNickName();
        return userListResponse;
    }

    public static List<UserListResponse> wrap(Collection<User> users) {
        List<UserListResponse> lists = new ArrayList<>();
        for (User user : users) {
            lists.add(wrap(user));
        }
        return lists;
    }
}
