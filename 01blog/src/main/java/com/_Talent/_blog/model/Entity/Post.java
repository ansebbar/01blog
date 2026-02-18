package com._Talent._blog.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com._Talent._blog.repositery.PostLikeRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    // @Autowired
    // PostLikeRepository pl;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;

    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private List<String> categories = new ArrayList<>();
    
    @Column(nullable = false)
    private String visibility = "public";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private User creator;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> postLikes = new ArrayList<>();


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports = new ArrayList<>();

        @Transient
    public int getLikeCount() {
        return (int) postLikes.stream()
            .filter(like -> "LIKE".equals(like.getType()))
            .count();
    }
    
    @Transient
    public int getDislikeCount() {
        return (int) postLikes.stream()
            .filter(like -> "DISLIKE".equals(like.getType()))
            .count();
    }
    
    public boolean isLikedBy(User user) {
        return postLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()));
    }
    
    public boolean isDislikedBy(User user) {
        return postLikes.stream()
            .anyMatch(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()));
    }
    
public void like(User user, PostLike like) {
    postLikes.removeIf(l -> l.getUser().equals(user) && "DISLIKE".equals(l.getType()));
    
    if (!postLikes.contains(like)) {
        postLikes.add(like);
    }
}

public void dislike(User user, PostLike dislike) {
    postLikes.removeIf(l -> l.getUser().equals(user) && "LIKE".equals(l.getType()));
    
    if (!postLikes.contains(dislike)) {
        postLikes.add(dislike);
    }
}
    public void removeLike(User user) {
        postLikes.removeIf(like -> like.getUser().equals(user) && "LIKE".equals(like.getType()));
    }
    
    public void removeDislike(User user) {
        postLikes.removeIf(like -> like.getUser().equals(user) && "DISLIKE".equals(like.getType()));
    }
    
    @Transient
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removePost() {
        for (Comment comment : comments) {
            comment.setPost(null);
        }
        comments.clear();
    }
    public void addReport(Report report) {
        reports.add(report);
        report.setPost(this);
    }
    
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
    

}