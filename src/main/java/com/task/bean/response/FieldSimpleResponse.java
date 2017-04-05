package com.task.bean.response;

import com.task.bean.Field;

/**
 * Created by blanke on 2017/4/5.
 */
public class FieldSimpleResponse {
    public int id;
    public String name;

    public static FieldSimpleResponse wrap(Field field) {
        FieldSimpleResponse response = new FieldSimpleResponse();
        response.name = field.getName();
        response.id = field.getId();
        return response;
    }
}
