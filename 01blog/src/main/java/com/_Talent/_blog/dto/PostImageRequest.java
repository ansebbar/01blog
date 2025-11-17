package com._Talent._blog.dto;

import lombok.Data;

@Data
public class PostImageRequest {
    private String imageUrl;
    private int order;
    private String alt;

    //services and controllers
}
