package com.task.bean.response;

import com.task.bean.Content;
import com.task.bean.ContentItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blanke on 2017/4/5.
 */
public class ContentDetailResponse {
    public int id;
    public boolean isSubmit;
    public boolean isVerify;
    public List<ContentItemDetailResponse> items;
    public UserListResponse user;

    public static ContentDetailResponse wrap(Content content) {
        ContentDetailResponse response = new ContentDetailResponse();
        response.id = content.getId();
        response.isSubmit = content.isSubmit();
        response.isVerify = content.isVerify();
        response.user = UserListResponse.wrap(content.getUser());
        response.items = new ArrayList<>();
        if (content.getItems() != null) {
            for (ContentItem contentItem : content.getItems()) {
                response.items.add(ContentItemDetailResponse.wrap(contentItem));
            }
        }
        return response;
    }
}

