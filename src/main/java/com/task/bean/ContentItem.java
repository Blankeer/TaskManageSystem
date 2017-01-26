package com.task.bean;

import javax.persistence.*;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class ContentItem {
    @Id
    @GeneratedValue
    private int id;
    @ManyToOne
    private Content content;
    @OneToOne
    private Field field;
    private String value;
    private boolean isVerify;

    public ContentItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }
}
