package com.task.bean.request;

import java.util.Date;

/**
 * Created by blanke on 2017/4/17.
 */
public class AddTaskRequest {
    private String title;
    private String description;
    private Date deadlineTime;
    private int[] users;
    private AddFieldRequest[] fields;

    public AddFieldRequest[] getFields() {
        return fields;
    }

    public void setFields(AddFieldRequest[] fields) {
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadlineTime() {
        return deadlineTime;
    }

    public void setDeadlineTime(Date deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    public int[] getUsers() {
        return users;
    }

    public void setUsers(int[] users) {
        this.users = users;
    }

}

