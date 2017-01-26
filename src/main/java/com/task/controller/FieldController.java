package com.task.controller;

import com.task.bean.Field;
import com.task.service.field.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by blanke on 17-1-27.
 */
@RestController
public class FieldController {
    @Autowired
    FieldService fieldService;

    //    @AdminValid
    @PostMapping("/fields")
    public ResponseEntity addField(@RequestBody Field field,
                                   @RequestParam("tid") int tid,
                                   @RequestParam("cid") int cid) {
        // TODO: 17-1-27 验证
        Field field1 = fieldService.addField(field, tid, cid);
        if (field1 != null) {
            return ResponseEntity.ok(field1);
        }
        return ResponseEntity.notFound().build();
    }

    //    @AdminValid
    @PostMapping("/fields/{id}")
    public ResponseEntity updateField(@PathVariable int id,
                                      @RequestBody Field field) {
        // TODO: 17-1-27 验证
        Field field1 = fieldService.updateField(field, id);
        if (field1 != null) {
            return ResponseEntity.ok(field1);
        }
        return ResponseEntity.notFound().build();
    }

    //    @AdminValid
    @DeleteMapping("/fields/{id}")
    public ResponseEntity deleteField(@PathVariable int id) {
        // TODO: 17-1-27 验证
        return ResponseEntity.ok(fieldService.deleteField(id));
    }
}
