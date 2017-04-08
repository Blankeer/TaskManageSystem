package com.task.bean.request;

/**
 * Created by blanke on 2017/4/8.
 */
public class ContentRequest {
    private ContentItemRequest[] data;
    private boolean isSubmit = true;

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public ContentItemRequest[] getData() {
        return data;
    }

    public void setData(ContentItemRequest[] data) {
        this.data = data;
    }
}
