package com._Talent._blog.dto;

import lombok.Data;

@Data
public class ARFollowRequest {
    private Long followerId;
    private Long followeeId;
    private boolean addOrRemove;
}
