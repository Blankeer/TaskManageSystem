package com.task.bean.response;

/**
 * Created by blanke on 17-1-26.
 */
public class BaseMessageResponse {
    private String message;

    public BaseMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
