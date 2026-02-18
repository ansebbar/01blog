package com._Talent._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com._Talent._blog.model.Entity.Post;
// import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserinfoRequest {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String avatarUrl;
    private String bio;
    private String status;
    private String rateadmin;
    private String createdAt;
    private UserinfoRequest[] followers; 
    private UserinfoRequest[] following;
    private int followersCount;
    private int followingCount; 
    private int postsCount;
    private int reportsCount;
    private String[] reports;
    private List<GetPostsRequest> posts;
}
