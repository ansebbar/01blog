package com._Talent._blog.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "image_order")
    private Integer order; // To maintain image sequence
    
    private String caption;
    
    @Column(name = "alt_text")
    private String altText;
    
    // Many-to-One: Many images can belong to one Post
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    
    public PostImage(String imageUrl, Integer order, Post post) {
        this.imageUrl = imageUrl;
        this.order = order;
        this.post = post;
    }
    
}