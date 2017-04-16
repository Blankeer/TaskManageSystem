package com.task.controller;

import com.task.annotation.AdminValid;
import com.task.bean.Config;
import com.task.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class ConfigController {
    @Autowired
    ConfigRepository configRepository;

    //    @AdminValid
    @PutMapping("/configs")
    public ResponseEntity addConfig(@RequestBody Config config) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok().build();
    }

    //    @AdminValid
    @DeleteMapping("/configs/{id}")
    public ResponseEntity deleteConfig(@PathVariable("id") int id) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok().build();
    }

    @AdminValid
    @GetMapping("/configs")
    public ResponseEntity getAllConfig(@RequestParam(value = "page", defaultValue = "0")
                                               Integer page,
                                       @RequestParam(value = "size", defaultValue = "10")
                                               Integer size,
                                       @RequestParam(value = "key", defaultValue = "")
                                               String key) {
        Pageable pageable = new PageRequest(page, size);
        return ResponseEntity.ok(configRepository.findNameContaining(pageable, key));
    }
}
