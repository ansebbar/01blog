package com._Talent._blog.dto;

import java.util.List;

import lombok.Data;
@Data
public class GetPostsRequest {
    private Long id;
    private String title;
    private String content;
    private String creator;
    private String avatarurl;
    private String dateFrom;
    private String updatedate;
    private List<String> categories;
    private String status;
    private int commentsCount;
    private int likes;
    private int dislikes;
    private boolean likedByCurrentUser;
    private boolean dislikedByCurrentUser;
    private List<String> postLikesusers;
    private List<CommentRequest> comments;
}