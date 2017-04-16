package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.*;
import com.task.bean.request.ContentItemRequest;
import com.task.bean.request.ContentRequest;
import com.task.bean.request.LikeTaskRequest;
import com.task.bean.response.BaseMessageResponse;
import com.task.bean.response.ContentDetailResponse;
import com.task.bean.response.FieldDetailResponse;
import com.task.bean.response.TaskListResponse;
import com.task.repository.ContentRepository;
import com.task.repository.FieldRepository;
import com.task.repository.TaskRepository;
import com.task.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class TaskController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ContentRepository contentRepository;
    @Autowired
    FieldRepository fieldRepository;

    @TokenValid
    @GetMapping("/tasks")
    public ResponseEntity getAllTasks(@RequestParam(value = "page", defaultValue = "0")
                                              Integer page,
                                      @RequestParam(value = "size", defaultValue = "10")
                                              Integer size,
                                      @RequestParam(value = "key", defaultValue = "")
                                              String key,
                                      User user) {
        Pageable pageable = new PageRequest(page, size);
        Page<Task> tasks = taskRepository.findByUsersAndTitleContaining(pageable, user, key);
        return ResponseEntity.ok(tasks.map(new Converter<Task, TaskListResponse>() {
            @Override
            public TaskListResponse convert(Task task) {
                return TaskListResponse.wrap(task);
            }
        }));
    }

    @TokenValid
    @GetMapping("/task/{task_id}")
    public ResponseEntity getTask(@PathVariable(value = "task_id") int taskId) {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @AdminValid
    @PostMapping("/tasks")
    public ResponseEntity addTask(@RequestBody Task task) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @AdminValid
    @PostMapping("/tasks/{id}")
    public ResponseEntity updateTask(@PathVariable int id, @RequestBody Task task) {
        // TODO: 17-1-27 验证
        Task newTask = null;
        Task oldTask = taskRepository.findOne(id);
        if (oldTask != null) {
            oldTask.setTitle(task.getTitle());
            oldTask.setDescription(task.getDescription());
            newTask = taskRepository.save(oldTask);
        }
        if (newTask != null) {
            return ResponseEntity.ok(newTask);
        }
        return ResponseEntity.notFound().build();
    }

    @AdminValid
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable int id) {
        // TODO: 17-1-27 验证
        Task task = taskRepository.findOne(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.delete(id);
        return ResponseEntity.ok().build();
    }

    @AdminValid
    @GetMapping("/tasks/{id}/fields")
    public ResponseEntity getTaskFields(@PathVariable int id) {
        // TODO: 17-1-27 验证
        Task task = taskRepository.findOne(id);
        List<FieldDetailResponse> fields = new ArrayList<>();
        if (task != null) {
            Set<Field> fields1 = task.getFields();
            for (Field field : fields1) {
                fields.add(FieldDetailResponse.wrap(field));
            }
            return ResponseEntity.ok(fields);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获得当前用户 某个 task 提交的内容,可能有多条
     *
     * @param id
     * @param user
     * @return
     */
    @TokenValid
    @GetMapping("/tasks/{id}/contents")
    public ResponseEntity getTaskContents(@PathVariable int id, User user) {
        Task task = taskRepository.findOne(id);
        List<ContentDetailResponse> contents = new ArrayList<>();
        if (task != null) {
            List<Content> sets = contentRepository.findByTaskAndUser(task, user);
            for (Content item : sets) {
                contents.add(ContentDetailResponse.wrap(item));
            }
            return ResponseEntity.ok(contents);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 提交 task 的内容
     *
     * @param taskId
     * @param user
     * @param request
     * @return
     */
    @TokenValid
    @PostMapping("/tasks/{taskId}/contents")
    public ResponseEntity addTaskContents(@PathVariable int taskId,
                                          User user,
                                          @RequestBody ContentRequest request) {
        Task task = taskRepository.findOne(taskId);
        if (task.getDeadlineTime().compareTo(new Date()) >= 0) {//超过截止时间
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("超过截止时间,不能提交"));
        }
        String failMsg = null;
        HttpStatus status = HttpStatus.CREATED;
        Content content = new Content();
        Set<ContentItem> contentItems = new HashSet<>();
        if (task != null) {
            content.setTask(task);
            content.setUser(user);
            content.setSubmit(request.isSubmit());
            for (ContentItemRequest contentItemRequest : request.getData()) {
                int fieldId = contentItemRequest.getFieldId();
                String value = contentItemRequest.getValue();
                Field field = null;
                if (fieldId != -1
                        && (field = fieldRepository.findOne(fieldId)) != null
                        && task.getFields().contains(field)) {
                    if (value == null) {
                        failMsg = field.getName() + " 不能为空";
                        status = HttpStatus.NOT_FOUND;
                        break;
                    } else {
                        value = value.trim();
                        if (!field.verify(value)) {
                            failMsg = field.getName() + "必须填写"
                                    + field.getConfig().getName()
                                    + ",详细：" + field.getConfig().getDescription();
                            status = HttpStatus.BAD_REQUEST;
                            break;
                        } else {
                            ContentItem contentItem = new ContentItem();
                            contentItem.setValue(value);
                            contentItem.setContent(content);
                            contentItem.setField(field);
                            contentItem.setVerify(false);//未审核
                            contentItems.add(contentItem);
                        }
                    }
                } else {
                    failMsg = "字段不存在";
                    status = HttpStatus.NOT_FOUND;
                    break;
                }
            }
        } else {
            failMsg = "任务不存在";
            status = HttpStatus.NOT_FOUND;
        }
        if (failMsg == null && contentItems.size() != task.getFields().size()) {
            failMsg = "请提交完整";
            status = HttpStatus.BAD_REQUEST;
        }
        if (failMsg != null) {
            return ResponseEntity.status(status)
                    .body(new BaseMessageResponse(failMsg));
        }
        content.setUpdatedAt(new Date());
        content.setItems(new ArrayList<>(contentItems));
        contentRepository.save(content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContentDetailResponse.wrap(content));
    }

    /**
     * 更新 task 某个 content 的内容
     *
     * @param taskId
     * @param contentId
     * @param user
     * @param request
     * @return
     */
    @TokenValid
    @PutMapping("/tasks/{taskId}/contents/{contentId}")
    public ResponseEntity updateTaskContents(@PathVariable int taskId,
                                             @PathVariable int contentId,
                                             User user,
                                             @RequestBody ContentRequest request) {
        Task task = taskRepository.findOne(taskId);
        if (task.getDeadlineTime().compareTo(new Date()) >= 0) {//超过截止时间
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("超过截止时间,不能提交"));
        }
        String failMsg = null;
        HttpStatus status = HttpStatus.CREATED;
        Content content = contentRepository.findOne(contentId);
        if (task != null && content != null) {
            if (!user.equals(content.getUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseMessageResponse("禁止访问"));
            }
            content.setTask(task);
            content.setUser(user);
            content.setVerify(false);
            content.setSubmit(request.isSubmit());
            for (ContentItemRequest contentItemRequest : request.getData()) {
                int fieldId = contentItemRequest.getFieldId();
                String value = contentItemRequest.getValue();
                Field field = null;
                if (fieldId != -1
                        && (field = fieldRepository.findOne(fieldId)) != null
                        && task.getFields().contains(field)) {
                    if (value == null) {
                        failMsg = field.getName() + " 不能为空";
                        status = HttpStatus.NOT_FOUND;
                        break;
                    } else {
                        value = value.trim();
                        if (!field.verify(value)) {
                            failMsg = field.getName() + "必须填写"
                                    + field.getConfig().getName()
                                    + ",详细：" + field.getConfig().getDescription();
                            status = HttpStatus.BAD_REQUEST;
                            break;
                        } else {
                            for (ContentItem item : content.getItems()) {
                                if (item.getField().equals(field)) {
                                    item.setValue(value);
                                    item.setVerify(false);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    failMsg = "字段不存在";
                    status = HttpStatus.NOT_FOUND;
                    break;
                }
            }
        } else {
            failMsg = "任务或内容不存在";
            status = HttpStatus.NOT_FOUND;
        }
        if (failMsg != null) {
            return ResponseEntity.status(status)
                    .body(new BaseMessageResponse(failMsg));
        }
        content.setUpdatedAt(new Date());
        contentRepository.save(content);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ContentDetailResponse.wrap(content));
    }

    @TokenValid
    @DeleteMapping("/tasks/{taskId}/contents/{contentId}")
    public ResponseEntity deleteTaskContents(@PathVariable int taskId,
                                             @PathVariable int contentId,
                                             User user) {
        Task task = taskRepository.findOne(taskId);
        Content content = contentRepository.findOne(contentId);
        if (task != null && content != null) {
            if (!user.equals(content.getUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseMessageResponse("禁止访问"));
            }
            contentRepository.delete(content);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseMessageResponse("任务或内容不存在"));
        }
    }

    /**
     * 获得用户收藏的任务列表
     *
     * @param user
     * @return
     */
    @TokenValid
    @GetMapping("/tasks/likes")
    public ResponseEntity getAllLikeTasks(@RequestParam(value = "page", defaultValue = "0")
                                                  Integer page,
                                          @RequestParam(value = "size", defaultValue = "10")
                                                  Integer size,
                                          User user) {
        Pageable pageable = new PageRequest(page, size);
        Page<Task> tasks = taskRepository.findByLikeUsers(pageable, user);
        return ResponseEntity.ok(tasks.map(new Converter<Task, TaskListResponse>() {
            @Override
            public TaskListResponse convert(Task task) {
                return TaskListResponse.wrap(task);
            }
        }));
    }

    /**
     * 添加收藏
     *
     * @param request
     * @param user
     * @return
     */
    @TokenValid
    @PostMapping("/tasks/likes")
    public ResponseEntity addLikeTask(@RequestBody LikeTaskRequest request, User user) {
        Task task = taskRepository.findOne(request.getTaskId());
        if (task != null) {
            user.getLikeTasks().add(task);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @TokenValid
    @DeleteMapping("/tasks/likes/")
    public ResponseEntity deleteLikeTask(@RequestBody LikeTaskRequest request, User user) {
        Task task = taskRepository.findOne(request.getTaskId());
        if (task != null) {
            user.getLikeTasks().remove(task);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
