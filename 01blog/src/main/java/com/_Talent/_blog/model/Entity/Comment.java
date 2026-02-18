package com._Talent._blog.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();
    
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addReport(Report report) {
        reports.add(report);
    }
        @Transient
    public int getLikeCount() {
        return (int) commentLikes.stream()
            .filter(like -> "LIKE".equals(like.getType()))
            .count();
    }
    
    @Transient
    public int getDislikeCount() {
        return (int) commentLikes.stream()
            .filter(like -> "DISLIKE".equals(like.getType()))
            .count();
    }
    
    public boolean isLikedBy(User user) {
        return commentLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()));
    }
    
    public boolean isDislikedBy(User user) {
        return commentLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()));
    }
    
    public void like(User user) {
        if(commentLikes.removeIf(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()))){
            this.dislike(user);
        }
        
        boolean alreadyLiked = commentLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()));
        
        if (!alreadyLiked) {
            PostLike like = new PostLike();
            like.setComment(this);
            like.setUser(user);
            like.setType("LIKE");
            commentLikes.add(like);
        }
    }
    
    public void dislike(User user) {
        if(commentLikes.removeIf(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()))){
            this.like(user);
        }
        
        boolean alreadyDisliked = commentLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()));
        
        if (!alreadyDisliked) {
            PostLike dislike = new PostLike();
            dislike.setComment(this);
            dislike.setUser(user);
            dislike.setType("DISLIKE");
            commentLikes.add(dislike);
        }
    }
    
    public void removeLike(User user) {
        commentLikes.removeIf(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()));
    }
    
    public void removeDislike(User user) {
        commentLikes.removeIf(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()));
    }
}