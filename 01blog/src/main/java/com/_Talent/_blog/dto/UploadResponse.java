package com._Talent._blog.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private String url;
    private String publicId;
    private String type;
    private String filename;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private Integer duration;
    private String format;
    private String resourceType;
    private String secureUrl;
    private String originalFilename;
}