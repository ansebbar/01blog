package com._Talent._blog.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private Long userId;
}
