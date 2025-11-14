package com._Talent._blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private String creator;
    private List<PostImageRequest> images;
}
