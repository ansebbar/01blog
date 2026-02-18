package com._Talent._blog.controllers;

import com._Talent._blog.dto.FollowRequest;
import com._Talent._blog.services.FollowService;
import com._Talent._blog.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService; // To get current user ID

    /**
     * Toggle follow/unfollow
     */
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFollow(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FollowRequest followRequest) {
        System.out.println("Toggle follow called for user to follow IDffffffffffffffffffffffffffffffffffffffffffffffffffffff: " + followRequest.getUserIdToFollow());
        try {
            // Get current user ID from authentication
            Long currentUserId = userService.getUserIdByUsername(userDetails.getUsername());
            Long targetUserId = followRequest.getUserIdToFollow();
            System.out.println("Current User ID: " + currentUserId + ", Target User ID: " + targetUserId);
            
            // Toggle follow status
            boolean isNowFollowing = followService.toggleFollow(currentUserId, targetUserId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("following", isNowFollowing);
            response.put("message", isNowFollowing ? "Successfully followed user" : "Successfully unfollowed user");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error toggling follow status: " + e.getMessage()
            ));
        }
    }

    /**
     * Check follow status
     */
    @GetMapping("/status/{targetUserId}")
    public ResponseEntity<?> getFollowStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("targetUserId") Long targetUserId) {
        
        try {
            Long currentUserId = userService.getUserIdByUsername(userDetails.getUsername());
            
            FollowService.FollowStatus status = followService.getFollowStatus(currentUserId, targetUserId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "following", status.isFollowing(),
                "followedBy", status.isFollowedBy()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error getting follow status"
            ));
        }
    }

    @GetMapping("/counts/{userId}")
    public ResponseEntity<?> getFollowCounts(@PathVariable("userId") Long userId) {
        try {
            // You would need to add these methods to your FollowService
            Long followerCount = followService.getFollowerCount(userId);
            Long followingCount = followService.getFollowingCount(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "followerCount", followerCount,
                "followingCount", followingCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error getting follow counts"
            ));
        }
    }
}