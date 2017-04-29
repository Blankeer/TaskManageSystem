package com.task.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private int id;
    private String email;
    private String pwd;
    private String nickName;
    private boolean isAdmin;
    private String token;
    private String captcha;//验证码
    private Date captchaCreatedAt;//验证码生成时间,用于判断过期
    private boolean isActivate = false;//激活状态,主要用于注册时的验证状态

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Content> contents;
    @ManyToMany(mappedBy = "users")
    private Set<Task> tasks;
    @ManyToMany
    private Set<Task> likeTasks;

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public Date getCaptchaCreatedAt() {
        return captchaCreatedAt;
    }

    public void setCaptchaCreatedAt(Date captchaCreatedAt) {
        this.captchaCreatedAt = captchaCreatedAt;
    }

    public boolean isActivate() {
        return isActivate;
    }

    public void setActivate(boolean activate) {
        isActivate = activate;
    }

    public Set<Task> getLikeTasks() {
        return likeTasks;
    }

    public void setLikeTasks(Set<Task> likeTasks) {
        this.likeTasks = likeTasks;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Content> getContents() {
        return contents;
    }

    public void setContents(Set<Content> contents) {
        this.contents = contents;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
