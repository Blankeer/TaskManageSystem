package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.bean.Config;
import com.task.bean.request.UpdateConfigRequest;
import com.task.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 规则配置相关 API
 * Created by blanke on 17-1-27.
 */
@RestController
public class ConfigController {
    @Autowired
    ConfigRepository configRepository;

    /**
     * 新增规则
     *
     * @param config
     * @return
     */
    @AdminValid
    @PostMapping("/configs")
    public ResponseEntity addConfig(@RequestBody Config config) {
        configRepository.save(config);
        return ResponseEntity.ok(config);
    }

    /**
     * 删除规则
     *
     * @param id
     * @return
     */
    @AdminValid
    @DeleteMapping("/configs/{id}")
    public ResponseEntity deleteConfig(@PathVariable("id") int id) {
        Config config = configRepository.findOne(id);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        configRepository.delete(config);
        return ResponseEntity.noContent().build();//204成功删除
    }

    /**
     * 获得所有规则
     *
     * @param page
     * @param size
     * @param key
     * @return
     */
    @AdminValid
    @GetMapping("/configs")
    public ResponseEntity getAllConfig(@RequestParam(value = "page", defaultValue = "0")
                                               Integer page,
                                       @RequestParam(value = "size", defaultValue = "10")
                                               Integer size,
                                       @RequestParam(value = "key", defaultValue = "")
                                               String key) {
        Pageable pageable = new PageRequest(page, size);
        return ResponseEntity.ok(configRepository.findByNameContaining(pageable, key));
    }

    /**
     * 获得某个规则的详情
     *
     * @param cid
     * @return
     */
    @AdminValid
    @GetMapping("/configs/{cid}")
    public ResponseEntity getConfigDetail(@PathVariable int cid) {
        Config config = configRepository.findOne(cid);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }

    /**
     * 更新规则
     *
     * @param cid
     * @param request
     * @return
     */
    @AdminValid
    @PutMapping("/configs/{cid}")
    public ResponseEntity updateConfig(@PathVariable int cid,
                                       @RequestBody UpdateConfigRequest request) {
        Config config = configRepository.findOne(cid);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        config.setName(request.name);
        config.setDescription(request.description);
        config.setExpression(request.expression);
        configRepository.save(config);
        return ResponseEntity.ok("修改成功");
    }
}
