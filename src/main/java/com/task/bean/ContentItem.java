package com.task.bean;

import javax.persistence.*;

/**
 * Created by blanke on 17-1-25.
 */
@Entity
public class ContentItem implements Comparable<ContentItem> {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentItem that = (ContentItem) o;
        if (getId() > 0 && that.getId() == getId()) {
            return true;
        }
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        return field != null ? field.equals(that.field) : that.field == null;
    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
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

    @Override
    public int compareTo(ContentItem o) {
        if (field == null) {
            return -1;
        }
        if (o.getField() == null) {
            return 1;
        }
        return field.getId() - o.getField().getId();
    }
}
