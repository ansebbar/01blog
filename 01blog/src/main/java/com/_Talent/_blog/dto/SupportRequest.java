package com._Talent._blog.dto;

import lombok.Data;

@Data
public class SupportRequest {
    private String name;
    private String email;
    private String subject;
    private String message;
    private String category;
}
