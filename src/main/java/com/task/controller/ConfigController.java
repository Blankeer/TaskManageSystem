package com.task.controller;

import com.task.bean.Config;
import com.task.service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class ConfigController {
    @Autowired
    ConfigService configService;

    //    @AdminValid
    @PutMapping("/configs")
    public ResponseEntity addConfig(@RequestBody Config config) {
        // TODO: 17-1-27 验证
        configService.addConfig(config);
        return ResponseEntity.ok().build();
    }

    //    @AdminValid
    @DeleteMapping("/configs/{id}")
    public ResponseEntity deleteConfig(@PathVariable("id") int id) {
        // TODO: 17-1-27 验证
        configService.deleteConfig(id);
        return ResponseEntity.ok().build();
    }

    //    @AdminValid
    @GetMapping("/configs")
    public ResponseEntity getAllConfig() {
        return ResponseEntity.ok(configService.getAllConfig());
    }
}
