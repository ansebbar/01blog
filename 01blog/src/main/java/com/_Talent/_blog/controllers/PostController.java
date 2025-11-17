package com._Talent._blog.controllers;
import com._Talent._blog.dto.AddCommentRequest;
import com._Talent._blog.dto.CreatePostRequest;
import com._Talent._blog.model.Entity.Comment;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    // CREATE post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody CreatePostRequest request) {
        Post post = postService.createPost(
            request.getTitle(), 
            request.getContent(), 
            request.getCreator(),
            request.getImages()
        );
        return ResponseEntity.ok(post);
    }
    
    // GET all posts
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    
    // GET posts by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }
    
    // ADD comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long postId, 
            @RequestBody AddCommentRequest request) {
        Comment comment = postService.addCommentToPost(
            postId, 
            request.getUserId(), 
            request.getContent()
        );
        return ResponseEntity.ok(comment);
    }
}