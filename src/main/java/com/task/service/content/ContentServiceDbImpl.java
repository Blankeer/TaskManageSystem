package com.task.service.content;

import com.task.bean.Content;
import com.task.bean.Task;
import com.task.bean.User;
import com.task.repository.ContentRepository;
import com.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by blanke on 2017/3/19.
 */
@Service
public class ContentServiceDbImpl implements ContentService {
    @Autowired
    ContentRepository mContentRepository;
    @Autowired
    TaskRepository mTaskRepository;

    @Override
    public Content getContent(int taskId, User user) {
        if (taskId <= 0) {
            return null;
        }
        Task task=mTaskRepository.findOne(taskId);
        return mContentRepository.findByTaskAndUser(task, user);
    }
}
