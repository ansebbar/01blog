package com._Talent._blog.dto;

import lombok.Data;

@Data
public class ARLikeRequest {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String type;
    private boolean addOrRemove;
}
