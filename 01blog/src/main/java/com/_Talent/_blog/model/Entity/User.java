package com._Talent._blog.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Column(name = "pin")
    private String pin;

    @Column(name = "token", length = 1000)
    private String token; 
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "status")
    private String status = "active"; // active, deactivated, banned
    
    @Column(name = "admin")
    private String rateadmin = "user"; // user, admin, superadmin

    @Column(length = 1000)
    private String bio;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // One-to-Many: A user can create many posts
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();
    
    // One-to-Many: A user can write many comments
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notif> notifs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Report> reports = new ArrayList<>();


    
    // One-to-Many: Users this user is following
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFollowing> followingRelations = new ArrayList<>();
    
    // One-to-Many: Users who are following this user
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserFollowing> followerRelations = new ArrayList<>();
    
    // One-to-Many: Posts liked by this user
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> userReports = new ArrayList<>();
    
    // One-to-Many: Comments liked by this user
    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<CommentLike> commentLikes = new ArrayList<>();
    
    
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
    
    
    @Transient
    public List<User> getFollowers() {
        List<User> followers = new ArrayList<>();
        for (UserFollowing relation : followerRelations) {
            followers.add(relation.getFollower());
        }
        return followers;
    }
    
    @Transient
    public List<User> getFollowing() {
        List<User> following = new ArrayList<>();
        for (UserFollowing relation : followingRelations) {
            following.add(relation.getFollowing());
        }
        return following;
    }
    
    public void createPost(Post post) {
        posts.add(post);
        post.setCreator(this);
    }
    
    public void addComment(Comment comment, Post post) {
        comments.add(comment);
        comment.setUser(this);
        comment.setPost(post);
    }

    public void addReport(Report report) {
        reports.add(report);
        report.setUser(this);
    }
    
    public void follow(User userToFollow) {
        if (!isFollowing(userToFollow) && !this.equals(userToFollow)) {
            UserFollowing following = new UserFollowing();
            following.setFollower(this);
            following.setFollowing(userToFollow);
            // userFollowingRepository.save(following);
            followingRelations.add(following);
            userToFollow.getFollowerRelations().add(following);
        }
    }
    
    public void unfollow(User userToUnfollow) {
        followingRelations.removeIf(relation -> 
            relation.getFollowing().equals(userToUnfollow));
        userToUnfollow.getFollowerRelations().removeIf(relation ->
            relation.getFollower().equals(this));
    }
    
    public boolean isFollowing(User user) {
        return followingRelations.stream()
            .anyMatch(relation -> relation.getFollowing().equals(user));
    }
    
    public boolean isFollowedBy(User user) {
        return followerRelations.stream()
            .anyMatch(relation -> relation.getFollower().equals(user));
    }
    
    public int getPostCount() {
        return posts != null ? posts.size() : 0;
    }
    
    public int getFollowerCount() {
        return followerRelations != null ? followerRelations.size() : 0;
    }
    
    public int getFollowingCount() {
        return followingRelations != null ? followingRelations.size() : 0;
    }
}