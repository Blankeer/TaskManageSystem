package com.task.bean;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class Content {
    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private Task task;
    @OneToOne
    private User user;
    private boolean isSubmit;
    private boolean isVerify;
    @OneToMany
    private Set<ContentItem> items;

    public Content() {
    }

    public Set<ContentItem> getItems() {
        return items;
    }

    public void setItems(Set<ContentItem> items) {
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
