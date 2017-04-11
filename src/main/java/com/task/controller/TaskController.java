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
import com.task.service.task.TaskService;
import com.task.utils.DataTableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class TaskController {
    @Autowired
    TaskService taskService;
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
    public ResponseEntity getAllTasks(DataTablesInput input, User user) {
        List<Column> columns = input.getColumns();
        List<Column> deleteColumns = new ArrayList<>();
        //需要删除data为空的字段，防止报错
        for (Column column : columns) {
            if (column.getData() == null || column.getData().trim().length() == 0) {
                deleteColumns.add(column);
            }
        }
        columns.removeAll(deleteColumns);
        DataTablesOutput<Task> tasks_row = taskRepository.findAll(input, new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Set<User>> users = root.get("users");
                return criteriaBuilder.isMember(user, users);
            }
        });
        //把task转成taskResponse
        List<TaskListResponse> tasks = new ArrayList<>();
        DataTablesOutput result = DataTableUtils.convert(tasks_row);
        if (tasks_row.getData() != null) {
            for (Task task : tasks_row.getData()) {
                tasks.add(TaskListResponse.wrap(task));
            }
        }
        result.setData(tasks);
        return ResponseEntity.ok(result);
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
        return ResponseEntity.ok(taskService.addTask(task));
    }

    @AdminValid
    @PostMapping("/tasks/{id}")
    public ResponseEntity updateTask(@PathVariable int id, @RequestBody Task task) {
        // TODO: 17-1-27 验证
        Task newTask = taskService.updateTask(id, task);
        if (newTask != null) {
            return ResponseEntity.ok(newTask);
        }
        return ResponseEntity.notFound().build();
    }

    @AdminValid
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTask(@PathVariable int id) {
        // TODO: 17-1-27 验证
        taskService.deleteTask(id);
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

    private List<Field> getFiledsByContent(List<ContentItem> contentItems) {
        List<Field> fields = new ArrayList<>();
        for (ContentItem item : contentItems) {
            fields.add(item.getField());
        }
        return fields;
    }

    @TokenValid
    @GetMapping("/tasks/{id}/contents")
    public ResponseEntity getTaskContents(@PathVariable int id, User user) {
        Task task = taskRepository.findOne(id);
        List<ContentDetailResponse> contents = new ArrayList<>();
        if (task != null) {
            List<Content> sets = contentRepository.findByTaskAndUser(task, user);
//            for (Content content : sets) {
//                if (content.getItems().size() != task.getFields().size()) {//有些字段没有填写
//                    List<Field> existFields = getFiledsByContent(content.getItems());
//                    List<Field> missFields = new ArrayList<>(task.getFields());
//                    missFields.removeAll(existFields);
//                    for (Field missField : missFields) {
//                        ContentItem item = new ContentItem();
//                        item.setField(missField);
//                        item.setContent(content);
//                        item.setValue("");
//                        item.setVerify(false);
//                        content.getItems().add(item);
//                    }
//                }
//            }
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
//        Set<ContentItem> contentItems = new HashSet<>();
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
//                            ContentItem contentItem = new ContentItem();
//                            contentItem.setValue(value);
//                            contentItem.setContent(content);
//                            contentItem.setField(field);
//                            contentItem.setVerify(false);//未审核
//                            contentItems.add(contentItem);
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
//        if (failMsg == null && contentItems.size() != task.getFields().size()) {
//            failMsg = "请提交完整";
//            status = HttpStatus.BAD_REQUEST;
//        }
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
    @GetMapping("/tasks/likes/")
    public ResponseEntity getAllLikeTasks(User user) {
        return ResponseEntity.ok(user.getLikeTasks());
    }

    /**
     * 添加收藏
     *
     * @param request
     * @param user
     * @return
     */
    @TokenValid
    @PostMapping("/tasks/likes/")
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
