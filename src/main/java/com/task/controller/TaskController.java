package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.*;
import com.task.bean.request.*;
import com.task.bean.response.*;
import com.task.repository.*;
import com.task.utils.TextUtils;
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
    @Autowired
    ConfigRepository configRepository;

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
        Page<Task> tasks = null;
        if (user.isAdmin()) {//管理员显示所有
            tasks = taskRepository.findByTitleContaining(pageable, key);
        } else {
            tasks = taskRepository.findByUsersAndTitleContaining(pageable, user, key);
        }
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
    @GetMapping("/task/{task_id}/users")
    public ResponseEntity getTaskUsers(@PathVariable(value = "task_id") int taskId) {
        Task task = taskRepository.findOne(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserListResponse.wrap(task.getUsers()));
    }

    @AdminValid
    @PostMapping("/tasks")
    public ResponseEntity addTask(@RequestBody AddTaskRequest taskRequest) {
        String title = taskRequest.getTitle();
        String desc = taskRequest.getDescription();
        Date deadlineTime = taskRequest.getDeadlineTime();
        Set<User> users = new HashSet<>();
        List<Field> fields = new ArrayList<>();
        String failMsg = null;
        if (TextUtils.isEmpty(title)) {
            failMsg = "任务标题不能为空";
        } else if (deadlineTime == null) {
            failMsg = "任务截止日期不能为空";
        } else if (deadlineTime.compareTo(new Date()) <= 0) {
            failMsg = "任务截止日期必须在今天之后";
        }
        if (failMsg != null) {
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse(failMsg));
        }
        //解析 user
        if (taskRequest.getUsers() != null) {
            for (int uid : taskRequest.getUsers()) {
                User user = userRepository.findOne(uid);
                if (user == null) {
                    return new ResponseEntity(
                            new BaseMessageResponse("用户不存在"), HttpStatus.NOT_FOUND);
                }
                users.add(user);
            }
        }
        //解析 config
        if (taskRequest.getFields() != null) {
            for (AddFieldRequest fieldRequest : taskRequest.getFields()) {
                if (fieldRequest.getConfig_id() > 0) {
                    Config config = configRepository.findOne(fieldRequest.getConfig_id());
                    if (config == null) {
                        return new ResponseEntity(
                                new BaseMessageResponse("规则不存在"), HttpStatus.NOT_FOUND);
                    }
                    Field field = new Field();
                    field.setDescription(fieldRequest.getDescription());
                    field.setConfig(config);
                    field.setName(fieldRequest.getName());
                    fields.add(field);
                }
            }
        }
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(desc);
        task.setPublishTime(new Date());
        task.setDeadlineTime(deadlineTime);
        task.setUsers(users);
        taskRepository.save(task);
        for (Field field : fields) {
            field.setTask(task);
            fieldRepository.save(field);
        }
        return ResponseEntity.ok().build();
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
        String failMsg = null;
        HttpStatus status = HttpStatus.CREATED;
        Content content = new Content();
        Set<ContentItem> contentItems = new HashSet<>();
        if (task != null) {
            if (task.getDeadlineTime().compareTo(new Date()) >= 0) {//超过截止时间
                return ResponseEntity.badRequest()
                        .body(new BaseMessageResponse("超过截止时间,不能提交"));
            }
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
        String failMsg = null;
        HttpStatus status = HttpStatus.CREATED;
        Content content = contentRepository.findOne(contentId);
        if (task != null && content != null) {
            if (!user.equals(content.getUser())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new BaseMessageResponse("禁止访问"));
            }
            if (task.getDeadlineTime().compareTo(new Date()) >= 0) {//超过截止时间
                return ResponseEntity.badRequest()
                        .body(new BaseMessageResponse("超过截止时间,不能提交"));
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
