package com.task.service.content;

import com.task.bean.Content;
import com.task.bean.User;

/**
 * Created by blanke on 2017/3/19.
 */
public interface ContentService {
    Content getContent(int taskId, User user);

}
