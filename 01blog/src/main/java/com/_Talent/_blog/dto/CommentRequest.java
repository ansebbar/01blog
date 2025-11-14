package com._Talent._blog.dto;
import java.util.List;

public class CommentRequest {
    private String content;
    private String creator;
    private String createdat;
    private int likes;
    private List<String> commentLikesusers;
}
