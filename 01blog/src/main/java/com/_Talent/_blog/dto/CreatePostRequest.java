package com._Talent._blog.dto;

import java.util.List;

// import com._Talent._blog.model.Entity.PostImage;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String title;
    private String content;
    private Long creator;
    private List<PostImageRequest> images;
}
