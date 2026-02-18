package com._Talent._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String id;
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String profileImageUrl;

    public AuthResponse(String token, String username, String email,String id, String profileImageUrl) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.id = id;
        this.profileImageUrl = profileImageUrl;
    }
}