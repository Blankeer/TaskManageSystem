package com.task.bean.response;

import com.task.bean.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static List<TaskListResponse> wrap(List<Task> tasks) {
        List<TaskListResponse> res = new ArrayList<>();
        for (Task task1 : tasks) {
            res.add(wrap(task1));
        }
        return res;
    }
}
