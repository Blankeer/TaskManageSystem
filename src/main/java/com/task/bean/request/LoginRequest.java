package com.task.bean.request;

/**
 * Created by blanke on 17-1-25.
 */
public class LoginRequest {
    private String email;
    private String pwd;
    private String captcha;//验证码

    public LoginRequest() {
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
