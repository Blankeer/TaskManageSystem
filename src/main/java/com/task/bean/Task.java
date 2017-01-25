package com.task.bean;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class Task {
    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String description;
    @OneToMany
    private Set<Field> fields;

    public Task() {
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void setFields(Set<Field> fields) {
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
