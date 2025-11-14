package com._Talent._blog.model.Entity;

// import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePicture;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // One-to-Many: One User can create many Posts
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();
    
    // One-to-Many: One User can write many Comments
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    
    // One-to-Many: One User can have many Post Likes
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();
    
    // One-to-Many: One User can have many Comment Likes
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentLike> commentLikes = new ArrayList<>();
    
    // Following relationships (User follows other users)
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> following = new ArrayList<>();
    
    // Follower relationships (Other users follow this user)
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

       @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    public void addPost(Post post) {
        posts.add(post);
        post.setCreator(this);
    }
    public void removePost(Post post) {
        posts.remove(post);
        post.setCreator(null);
    }
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setUser(this);
    }
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setUser(null);
    }
    public void followUser(User userToFollow) {
        Follow follow = new Follow(this, userToFollow);
        following.add(follow);
        userToFollow.getFollowers().add(follow);
    }
    public void unfollowUser(User userToUnfollow) {
        following.removeIf(follow -> follow.getFollowing().equals(userToUnfollow));
        userToUnfollow.getFollowers().removeIf(follow -> follow.getFollower().equals(this));
    }
    public boolean isFollowing(User user) {
        return following.stream().anyMatch(follow -> follow.getFollowing().equals(user));
    }
    public boolean isFollowedBy(User user) {
        return followers.stream().anyMatch(follow -> follow.getFollower().equals(user));
    }
    
    
}
    