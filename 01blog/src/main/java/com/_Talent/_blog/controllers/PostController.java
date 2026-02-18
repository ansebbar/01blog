package com._Talent._blog.controllers;

import com._Talent._blog.dto.AddCommentRequest;
import com._Talent._blog.dto.CreatePostRequest;
import com._Talent._blog.dto.GetPostsRequest;
import com._Talent._blog.dto.CommentRequest;
import com._Talent._blog.model.Entity.Comment;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com._Talent._blog.dto.UpdatePostReq;
import com._Talent._blog.dto.ReportRequest;
import com._Talent._blog.model.Entity.Report;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // CREATE a new post - WORKS AS IS
    @PostMapping("/newpost")
    public ResponseEntity<Boolean> createPost(@RequestBody CreatePostRequest request) {
        System.out.println("Received CreatePostRequest:ffffffffffffffffffffffffffffffffffffffffffffffffff " + request);

        try {
            Post post = postService.createPost(
                    request.getTitle(),
                    request.getContent(),
                    request.getCreator(),
                    request.getCategories(),
                    request.getVisibility());
            System.out.println("Post created successfully with ID: " + post.getId());
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            System.err.println("Error creating post: " + e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping
    public ResponseEntity<List<GetPostsRequest>> getAllPosts() {
        try {
            List<Post> posts = postService.getAllPosts();

            List<GetPostsRequest> getPostsRequests = postService.getpostsofany(posts);

            System.out.println("Returning " + getPostsRequests.size() + " posts");
            return ResponseEntity.ok(getPostsRequests);

        } catch (Exception e) {
            System.err.println("Error getting posts: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // GET post by ID - FIXED VERSION
    @GetMapping("/{postId}")
    public ResponseEntity<GetPostsRequest> getPostById(@PathVariable("postId") Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return ResponseEntity.ok(postService.getPostData(post));

        } catch (Exception e) {
            System.err.println("Error getting post by ID: " + e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }

    // GET posts by user - FIXED VERSION
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GetPostsRequest>> getPostsByUser(@PathVariable("userId") Long userId) {
        try {
            List<Post> posts = postService.getPostsByUser(userId);

            List<GetPostsRequest> postDTOs = posts.stream().map(post -> {
                GetPostsRequest dto = new GetPostsRequest();
                dto.setId(post.getId());
                dto.setTitle(post.getTitle());
                dto.setContent(post.getContent());

                if (post.getCreator() != null) {
                    dto.setCreator(post.getCreator().getUsername());
                    dto.setAvatarurl(post.getCreator().getProfilePicture());
                }

                dto.setDateFrom(post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
                dto.setUpdatedate(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "");
                dto.setCategories(post.getCategories());
                dto.setStatus(post.getVisibility());
                dto.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);
                dto.setLikes(post.getLikeCount());

                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(postDTOs);

        } catch (Exception e) {
            System.err.println("Error getting posts by user: " + e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }

    // ADD comment - WORKS AS IS
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> addComment(
            @PathVariable("postId") Long postId,
            @RequestBody AddCommentRequest request) {
        try {
            Comment comment = postService.addCommentToPost(
                    postId,
                    request.getUserId(),
                    request.getContent());
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            System.err.println("Error adding comment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{postId}")
    public ResponseEntity<GetPostsRequest> updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody UpdatePostReq request) {
        try {
            // System.out.println("Received
            // UpdatePostReq:fffffffffffffffffffffffffffffffffffffff " +
            // request.getComment());
            GetPostsRequest isUpdated = postService.updatePost(postId, request);
            return ResponseEntity.ok(isUpdated);
        } catch (Exception e) {
            System.err.println("Error updating post: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/post/report")
    public ResponseEntity<String> reportPost(@RequestBody ReportRequest request) {
        try {
            return postService.reportPost(request.getReportedpostId(), request.getUsername(), request.getRaison());
        } catch (Exception e) {
            System.err.println("Error reporting post: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/comment/report")
    public ResponseEntity<String> reportComment(@RequestParam("id") Long id, @RequestParam("username") String username) {
        try {
            System.out.println("Reporting comment with ID: " + id + " and usernameMMMMMMMMM " + username);
            return postService.reportComment(id, username);

        } catch (Exception e) {
            System.err.println("Error reporting comment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/comment/like")
    public GetPostsRequest likeComment(@RequestParam("id") Long id, @RequestParam("username") String username) {
        try {
            return postService.likeComment(id, username);
        } catch (Exception e) {
            System.err.println("Error liking comment: " + e.getMessage());
            return null;
        }
    }

    @PostMapping("/comment/dislike")
    public GetPostsRequest dislikeComment(@RequestParam("id") Long id, @RequestParam("username") String username) {
        try {
            return postService.dislikeComment(id, username);
        } catch (Exception e) {
            System.err.println("Error disliking comment: " + e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/comment/delete")
    public ResponseEntity<String> deleteComment(@RequestParam("id") Long commentId) {
        try {
            postService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting comment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId) {
        try {
            postService.deletePost(postId);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            System.err.println("Error deleting post: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}