package com._Talent._blog.services;
import com._Talent._blog.dto.PostImageRequest;
import com._Talent._blog.model.Entity.Comment;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.model.Entity.PostImage;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.CommentRepository;
import com._Talent._blog.repositery.PostRepository;
import com._Talent._blog.repositery.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    // CREATE a new post
    public Post createPost(String title, String content, Long userId, List<PostImageRequest> images) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreator(user);
        for (PostImageRequest imgReq : images) {
            PostImage img = new PostImage();
            img.setImageUrl(imgReq.getImageUrl());
            img.setOrder(imgReq.getOrder());
            img.setPost(post); // Set the back-reference
            post.getImages().add(img);
        }
        
        user.addPost(post);
        
        return postRepository.save(post);
    }
    
    // GET all posts with comments
    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        
        // Eagerly load comments for each post
        posts.forEach(post -> {
            // This triggers LAZY loading of comments
            post.getComments().size(); // Just accessing the list loads it
        });
        
        return posts;
    }
    
    // GET posts by user
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByCreatorId(userId);
    }
    
    // ADD comment to post
    public Comment addCommentToPost(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        
        post.addComment(comment);
        
        return commentRepository.save(comment);
    }
    
    // DELETE a post (and its comments due to CASCADE)
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}