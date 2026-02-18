package com._Talent._blog.dto;

import lombok.Data;

@Data
public class ReportRequest {
    private String username; // Reporter username private long postId; // ID of the post being reported private String reason; // Reason for reporting
    private String reportedusername;
    private long reportedpostId;
    private long reportedcommentid;
    private String raison;
}
