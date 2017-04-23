package com.task.bean.response;

import com.task.bean.Task;

import java.util.Date;

/**
 * Created by blanke on 2017/4/23.
 */
public class AdminTaskDetailResponse {
    public int id;
    public String title;
    public String description;
    public Date publishTime;
    public Date deadlineTime;
    public int contentCount;//总的内容总数
    public int passContentCount;//通过的内容数
    public int dismissContentCount;//未通过的总数
    public int waitContentCount;//待审核的数目
    public int userCount;//用户总数

    public static AdminTaskDetailResponse wrap(Task task) {
        AdminTaskDetailResponse response = new AdminTaskDetailResponse();
        response.id = task.getId();
        response.title = task.getTitle();
        response.description=task.getDescription();
        response.publishTime = task.getPublishTime();
        response.deadlineTime = task.getDeadlineTime();
        response.userCount=task.getUsers().size();
        return response;
    }
}
