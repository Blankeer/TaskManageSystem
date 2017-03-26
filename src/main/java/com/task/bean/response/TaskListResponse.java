package com.task.bean.response;

import com.task.bean.Task;

import java.util.Date;

/**
 * Created by blanke on 2017/3/26.
 */
public class TaskListResponse {
    public int id;
    public String title;
    public Date publishTime;
    public Date deadlineTime;

    public static TaskListResponse wrap(Task task) {
        TaskListResponse response = new TaskListResponse();
        response.id = task.getId();
        response.title = task.getTitle();
        response.publishTime = task.getPublishTime();
        response.deadlineTime = task.getDeadlineTime();
        return response;
    }
}
