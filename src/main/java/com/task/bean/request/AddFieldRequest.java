package com.task.bean.request;

/**
 * Created by blanke on 2017/4/17.
 */
public class AddFieldRequest {
    private String name;
    private String description;
    private int config_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getConfig_id() {
        return config_id;
    }

    public void setConfig_id(int config_id) {
        this.config_id = config_id;
    }
}
