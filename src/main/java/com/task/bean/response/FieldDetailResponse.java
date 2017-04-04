package com.task.bean.response;

import com.task.bean.Field;

/**
 * Created by blanke on 2017/4/4.
 */
public class FieldDetailResponse {
    public int id;
    public String name;
    public String description;
    public ConfigDetailResponse config;

    public static FieldDetailResponse wrap(Field field) {
        FieldDetailResponse response = new FieldDetailResponse();
        response.description = field.getDescription();
        response.name = field.getName();
        response.id = field.getId();
        response.config = ConfigDetailResponse.wrap(field.getConfig());
        return response;
    }
}
