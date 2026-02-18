package com._Talent._blog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com._Talent._blog.dto.CreatePostRequest;
// import com._Talent._blog.model.Entity.Post;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend.url}")
public class HomeController {


    @Autowired
    private PostController postController;
    
    @GetMapping("/home")
    public String home() {
        return "Welcome to the Blog API!";
        // return postController.getAllPosts();
        // return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/home/newpost")
    public int newPost(@RequestBody CreatePostRequest request) {
        postController.createPost(request);
        return 1;
    }
    
    @GetMapping("/secure/hello")
    public String secureHello() {
        return "This is a secure endpoint!";
    }
}