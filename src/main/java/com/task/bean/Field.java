package com.task.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.regex.Pattern;

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

    public boolean verify(String content) {
        return Pattern.matches(config.getExpression(), content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        return id == field.id;
    }

    @Override
    public int hashCode() {
        return id;
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
