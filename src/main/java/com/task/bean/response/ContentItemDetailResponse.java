package com.task.bean.response;

import com.task.bean.ContentItem;

/**
 * Created by blanke on 2017/4/5.
 */
public class ContentItemDetailResponse {
    public int id;
    public FieldDetailResponse field;
    public String value;
    public boolean isVerify;

    public static ContentItemDetailResponse wrap(ContentItem contentItem) {
        ContentItemDetailResponse response = new ContentItemDetailResponse();
        response.id = contentItem.getId();
        response.value = contentItem.getValue();
        response.isVerify = contentItem.isVerify();
        response.field = FieldDetailResponse.wrap(contentItem.getField());
        return response;
    }
}
