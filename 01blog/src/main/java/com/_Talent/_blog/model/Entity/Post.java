package com._Talent._blog.model.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "featured_image_url")
    private String featuredImageUrl; 
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("order ASC") 
    private List<PostImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    
    // Helper methods for images
    public void addImage(PostImage image) {
        images.add(image);
        image.setPost(this);
    }

    public void addImages(List<PostImage> images) {
        for (PostImage image : images) {
            addImage(image);
        }
    }
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    public void addLike(PostLike like) {
        likes.add(like);
        like.setPost(this);
    }
    
    
    public void removeImage(PostImage image) {
        images.remove(image);
        image.setPost(null);
    }
    
    public List<String> getImageUrls() {
        return images.stream()
                .map(PostImage::getImageUrl)
                .toList();
    }
    
    // Helper methods for counting
    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }
    
    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }
    
    public int getImageCount() {
        return images != null ? images.size() : 0;
    }
}