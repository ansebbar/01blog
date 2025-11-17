package com._Talent._blog.dto;
import java.util.List;

import lombok.Data;

@Data
public class GetPostsRequest {
    private String title;
    private String content;
    private String creator;
    private String dateFrom;
    private String updatedate;
    private List<PostImageRequest> images;
    private List<CommentRequest> comments;
    private int likes;
    private List<String> postLikesusers;
}
    //services and controllers
