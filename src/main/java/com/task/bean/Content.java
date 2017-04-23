package com.task.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class Content {
    @Id
    @GeneratedValue
    private int id;
    @JsonIgnore
    @ManyToOne
    private Task task;
    @JsonIgnore
    @ManyToOne
    private User user;
    private boolean isSubmit;
    private int state;
    private Date updatedAt;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<ContentItem> items;

    public Content() {
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ContentItem> getItems() {
        Collections.sort(items);
        return items;
    }

    public void setItems(List<ContentItem> items) {
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
