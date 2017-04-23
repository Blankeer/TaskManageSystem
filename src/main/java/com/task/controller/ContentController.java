package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.annotation.TokenValid;
import com.task.bean.Content;
import com.task.bean.Task;
import com.task.bean.User;
import com.task.bean.response.BaseMessageResponse;
import com.task.bean.response.ContentDetailResponse;
import com.task.repository.ContentRepository;
import com.task.repository.TaskRepository;
import com.task.utils.TaskExportUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blanke on 2017/3/19.
 */
@RestController
public class ContentController {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ContentRepository contentRepository;

    /**
     * 获得当前用户 某个 task 提交的内容,可能有多条
     * 普通用户调用返回自己的,管理员返回所有人的
     *
     * @param id
     * @param user
     * @return
     */
    @TokenValid
    @GetMapping("/tasks/{id}/contents")
    public ResponseEntity getTaskContents(@PathVariable int id,
                                          @RequestParam(value = "page", defaultValue = "0")
                                                  Integer page,
                                          @RequestParam(value = "size", defaultValue = "10")
                                                  Integer size,
                                          User user) {
        Task task = taskRepository.findOne(id);
        if (task != null) {
            if (!user.isAdmin()) {
                List<ContentDetailResponse> contents = new ArrayList<>();
                List<Content> sets = contentRepository.findByTaskAndUser(task, user);
                for (Content item : sets) {
                    contents.add(ContentDetailResponse.wrap(item));
                }
                return ResponseEntity.ok(contents);
            } else {//管理员显示所有人提交的内容
                Pageable pageable = new PageRequest(page, size);
                Page<Content> contents = contentRepository.findByTaskAndIsSubmit(pageable, task, true);
                return ResponseEntity.ok(contents.map(new Converter<Content, ContentDetailResponse>() {
                    @Override
                    public ContentDetailResponse convert(Content content) {
                        return ContentDetailResponse.wrap(content);
                    }
                }));
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 管理员审核内容
     *
     * @param cid
     * @param pass
     * @return
     */
    @AdminValid
    @GetMapping("/contents/{cid}")
    public ResponseEntity verifyContent(@PathVariable int cid,
                                        @RequestParam boolean pass) {
        Content content = contentRepository.findOne(cid);
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        if (content.getState() != 0) {//不能重复操作审核
            return ResponseEntity.badRequest()
                    .body(new BaseMessageResponse("已经审核过"));
        }
        content.setState(pass ? 1 : -1);
        contentRepository.save(content);
        return ResponseEntity.ok().build();
    }

    @AdminValid
    @GetMapping("/tasks/{tid}/contents/export")
    public ResponseEntity<Resource> exportContents(@PathVariable int tid) {
        Task task = taskRepository.findOne(tid);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        String filename = task.getTitle() + ".xls";
        HSSFWorkbook workbook = TaskExportUtils.exportTaskExcel(task);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            workbook.close();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("charset", "utf-8");
            //设置下载文件名
            filename = URLEncoder.encode(filename, "UTF-8");
            headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");

            Resource resource = new InputStreamResource(new ByteArrayInputStream(bos.toByteArray()));
            return ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.parseMediaType("application/x-msdownload")).body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
}
