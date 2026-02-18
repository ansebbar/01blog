package com._Talent._blog.services;

import com._Talent._blog.model.Entity.User;
import com._Talent._blog.model.Entity.UserFollowing;
import com._Talent._blog.repositery.UserFollowingRepository;
import com._Talent._blog.repositery.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FollowService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowingRepository userFollowingRepository;

    @Autowired
    private UserService userService; 

    /**
     * Toggle follow/unfollow status
     * @param currentUserId The ID of the user who is performing the action
     * @param targetUserId The ID of the user to follow/unfollow
     * @return true if now following, false if now unfollowed
     */
    @Transactional
    public boolean toggleFollow(Long currentUserId, Long targetUserId) {
        // Prevent self-follow
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        // Check if already following
        Optional<UserFollowing> existingFollow = userFollowingRepository
                .findByFollowerIdAndFollowingId(currentUserId, targetUserId);

        if (existingFollow.isPresent()) {
            // Unfollow
            userFollowingRepository.delete(existingFollow.get());
            currentUser.getFollowingRelations().remove(existingFollow.get());
            targetUser.getFollowerRelations().remove(existingFollow.get());
            userRepository.save(currentUser);
            userRepository.save(targetUser);
            return false; // Now unfollowed
        } else {
            // currentUser.follow(targetUser);
            UserFollowing follow = new UserFollowing();
            follow.setFollower(currentUser);
            follow.setFollowing(targetUser);
            userFollowingRepository.save(follow);
            currentUser.getFollowingRelations().add(follow);
            targetUser.getFollowerRelations().add(follow);
            userRepository.save(targetUser);
            userRepository.save(currentUser);
            // System.out.println(currentUser.get);
            return true; // Now following
        }
    }

    /**
     * Check if a user is following another user
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            return false; // User is not considered as following themselves
        }
        return userFollowingRepository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .isPresent();
    }

    /**
     * Get follow status between two users
     */
    public FollowStatus getFollowStatus(Long currentUserId, Long targetUserId) {
        boolean isFollowing = isFollowing(currentUserId, targetUserId);
        boolean isFollowedBy = isFollowing(targetUserId, currentUserId);
        
        return new FollowStatus(isFollowing, isFollowedBy);
    }

    public Long getFollowerCount(Long userId) {
        return userFollowingRepository.countByFollowingId(userId);
    }

    public Long getFollowingCount(Long userId) {
        return userFollowingRepository.countByFollowerId(userId);
    }

    // Inner class to represent follow status
    public static class FollowStatus {
        private boolean following;
        private boolean followedBy;
        
        public FollowStatus(boolean following, boolean followedBy) {
            this.following = following;
            this.followedBy = followedBy;
        }
        
        // Getters
        public boolean isFollowing() { return following; }
        public boolean isFollowedBy() { return followedBy; }
    }
}