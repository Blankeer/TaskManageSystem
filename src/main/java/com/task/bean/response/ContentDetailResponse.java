package com.task.bean.response;

import com.task.bean.Content;
import com.task.bean.ContentItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by blanke on 2017/4/5.
 */
public class ContentDetailResponse {
    public int id;
    public boolean isSubmit;
    public int state;
    public UserListResponse user;
    public Date updatedAt;
    public List<ContentItemDetailResponse> items;

    public static ContentDetailResponse wrap(Content content) {
        ContentDetailResponse response = new ContentDetailResponse();
        response.id = content.getId();
        response.isSubmit = content.isSubmit();
        response.state = content.getState();
        response.user = UserListResponse.wrap(content.getUser());
        response.updatedAt = content.getUpdatedAt();
        response.items = new ArrayList<>();
        if (content.getItems() != null) {
            for (ContentItem contentItem : content.getItems()) {
                response.items.add(ContentItemDetailResponse.wrap(contentItem));
            }
        }
        return response;
    }
}

