package com.task.bean.request;

/**
 * Created by blanke on 2017/4/8.
 */
public class ContentItemRequest {
    private int id = -1;
    private int fieldId = -1;
    private String value;

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
