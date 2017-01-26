package com.task.bean;

import javax.persistence.*;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class Field {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    @ManyToOne
    private Config config;
    @ManyToOne
    private Task task;

    public Field() {
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
