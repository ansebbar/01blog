package com._Talent._blog.dto;

import lombok.Data;

@Data
public class AddCommentRequest {
    private Long postId;
    private Long userId;
    private String content;

}
