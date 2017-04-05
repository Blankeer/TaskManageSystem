package com.task.bean.response;

import com.task.bean.Content;
import com.task.bean.ContentItem;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by blanke on 2017/4/5.
 */
public class ContentDetailResponse {
    public int id;
    public boolean isSubmit;
    public boolean isVerify;
    public Set<ContentItemDetailResponse> items;

    public static ContentDetailResponse wrap(Content content) {
        ContentDetailResponse response = new ContentDetailResponse();
        response.id = content.getId();
        response.isSubmit = content.isSubmit();
        response.isVerify = content.isVerify();
        response.items = new HashSet<>();
        for (ContentItem contentItem : content.getItems()) {
            response.items.add(ContentItemDetailResponse.wrap(contentItem));
        }
        return response;
    }
}

