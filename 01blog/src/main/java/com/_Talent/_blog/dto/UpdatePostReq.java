package com._Talent._blog.dto;

import lombok.Data;

@Data
public class UpdatePostReq {
    private String type;
    private String username;
    private String comment;
    private boolean like;
    private String title;
    private String content;
    private java.util.List<String> categories;
    private String visibility;

    public boolean isLike() {
        return like;
    }
}
