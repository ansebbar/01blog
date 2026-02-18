package com._Talent._blog.dto;

import java.util.List;

import lombok.Data;

@Data
public class CommentRequest {
    private Long id;
    private String content;
    private String creator;
    private String avatarurl;
    private int likes;
    private String date;
    // private List<String> commentLikesusers;
    private int dislikes;
    private boolean likedByCurrentUser;
    private boolean dislikedByCurrentUser;
    
    // public List<String> getCommentLikesusers() { return commentLikesusers; }
    // public void setCommentLikesusers(List<String> commentLikesusers) { this.commentLikesusers = commentLikesusers; }
}