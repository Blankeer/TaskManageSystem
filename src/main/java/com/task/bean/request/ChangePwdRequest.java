package com.task.bean.request;

/**
 * Created by blanke on 17-1-25.
 */
public class ChangePwdRequest {
    private String oldPwd;
    private String newPwd;

    public ChangePwdRequest() {
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }
}
