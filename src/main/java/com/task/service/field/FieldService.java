package com.task.service.field;

import com.task.bean.Field;

/**
 * Created by blanke on 17-1-27.
 */
public interface FieldService {
    Field addField(Field field, int tid,int cid);

    Field updateField(Field field,int id);

    boolean deleteField(int id);
}
