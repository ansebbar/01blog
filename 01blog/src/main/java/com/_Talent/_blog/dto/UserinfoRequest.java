package com._Talent._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserinfoRequest {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
    private String createdAt;
    private Map<String,String> followers; 
    private Map<String, String> following; 
        //services and controllers

}
