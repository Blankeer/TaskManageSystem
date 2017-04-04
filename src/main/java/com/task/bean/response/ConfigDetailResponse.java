package com.task.bean.response;

import com.task.bean.Config;

/**
 * Created by blanke on 2017/4/4.
 */
public class ConfigDetailResponse {
    public int id;
    public String name;
    public String description;
    public String expression;

    public static ConfigDetailResponse wrap(Config config) {
        ConfigDetailResponse response = new ConfigDetailResponse();
        response.id = config.getId();
        response.name = config.getName();
        response.description = config.getDescription();
        response.expression = config.getExpression();
        return response;
    }
}
