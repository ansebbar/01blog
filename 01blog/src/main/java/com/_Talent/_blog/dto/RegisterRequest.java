package com._Talent._blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String dateofbirth;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String bio;
}