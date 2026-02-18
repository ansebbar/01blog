package com._Talent._blog.services;

import com._Talent._blog.dto.CommentRequest;
import com._Talent._blog.dto.GetPostsRequest;
import com._Talent._blog.dto.UpdatePostReq;
import com._Talent._blog.model.Entity.Comment;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.model.Entity.PostLike;
import com._Talent._blog.model.Entity.User;
import com._Talent._blog.repositery.CommentRepository;
import com._Talent._blog.repositery.PostRepository;
import com._Talent._blog.repositery.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com._Talent._blog.model.Entity.Report;
import com._Talent._blog.repositery.ReportsRepository;
import com._Talent._blog.repositery.PostLikeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportsRepository reportsRepo;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    // CREATE a new post - KEEP AS IS
    public Post createPost(String title, String content, String username, List<String> categories, String visibility) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreator(user);
        post.setCategories(categories);
        post.setVisibility(visibility);
        List<Post> userPosts = user.getPosts();
        userPosts.add(post);
        user.setPosts(userPosts);
        return postRepository.save(post);
    }

    // GET post by ID - SIMPLE VERSION
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.getComments().size(); // This triggers loading of comments
        return post;
    }

    public GetPostsRequest getPostData(Post post) {

        GetPostsRequest dto = new GetPostsRequest();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());

        // Check if creator is not null
        if (post.getCreator() != null) {
            dto.setCreator(post.getCreator().getUsername());
            dto.setAvatarurl(post.getCreator().getProfilePicture());
        }

        // Dates
        dto.setDateFrom(post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
        dto.setUpdatedate(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "");

        // Other fields
        dto.setCategories(post.getCategories());
        dto.setStatus(post.getVisibility());
        dto.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);
        dto.setLikes(post.getLikeCount());
        dto.setDislikes(post.getDislikeCount());
        dto.setLikedByCurrentUser(post.isLikedBy(post.getCreator()));
        dto.setDislikedByCurrentUser(post.isDislikedBy(post.getCreator()));
        // dto.setPostLikesusers(post.getLikedByUsers().stream().map(user ->
        // user.getUsername()).collect(Collectors.toList()));

        if (post.getComments() != null && !post.getComments().isEmpty()) {
            List<CommentRequest> commentDTOs = post.getComments().stream().map(comment -> {
                CommentRequest commentDTO = new CommentRequest();
                commentDTO.setId(comment.getId());
                commentDTO.setContent(comment.getContent());
                commentDTO.setDate(comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : "");

                if (comment.getUser() != null) {
                    commentDTO.setCreator(comment.getUser().getUsername());
                    commentDTO.setAvatarurl(comment.getUser().getProfilePicture());
                }

                commentDTO.setLikes(comment.getLikeCount());
                commentDTO.setDislikes(comment.getDislikeCount());
                commentDTO.setLikedByCurrentUser(comment.isLikedBy(comment.getUser()));
                commentDTO.setDislikedByCurrentUser(comment.isDislikedBy(comment.getUser()));
                return commentDTO;
            }).collect(Collectors.toList());
            dto.setComments(commentDTOs);
        }
        return dto;
    }

    // get my posts

    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .filter(post -> "public".equals(post.getVisibility()))
                .peek(post -> post.getComments().size())
                .collect(Collectors.toList());
    }

    // GET posts by user - KEEP AS IS
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByCreatorId(userId);
    }

    public List<GetPostsRequest> getpostsofany(List<Post> posts) {
        return posts.stream().map(post -> {
            GetPostsRequest dto = new GetPostsRequest();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());

            if (post.getCreator() != null) {
                dto.setCreator(post.getCreator().getUsername());
                dto.setAvatarurl(post.getCreator().getProfilePicture());
            } else {
                dto.setCreator("Unknown");
                dto.setAvatarurl(null);
            }

            dto.setDateFrom(post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
            dto.setUpdatedate(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "");

            dto.setCategories(post.getCategories());
            dto.setStatus(post.getVisibility());
            dto.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);

            dto.setLikes(post.getLikeCount());
            dto.setDislikes(post.getDislikeCount());

            if (post.getComments() != null && !post.getComments().isEmpty()) {
                List<CommentRequest> commentDTOs = post.getComments().stream().map(comment -> {
                    CommentRequest commentDTO = new CommentRequest();
                    commentDTO.setId(comment.getId());
                    commentDTO.setContent(comment.getContent());

                    if (comment.getUser() != null) {
                        commentDTO.setCreator(comment.getUser().getUsername());
                    }

                    commentDTO.setLikes(comment.getLikeCount());

                    return commentDTO;
                }).collect(Collectors.toList());
                dto.setComments(commentDTOs);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public Comment addCommentToPost(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        // comment.setPost(post);
        // comment.setUser(user);

        user.addComment(comment, post);
        post.addComment(comment);

        return commentRepository.save(comment);
    }

    // DELETE a post - KEEP AS IS
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public GetPostsRequest updatePost(Long postId, UpdatePostReq updatePostReq) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = this.userRepository.findByUsername(updatePostReq.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updatePostReq.getType() == null) {
            if (updatePostReq.getTitle() != null) {
                post.setTitle(updatePostReq.getTitle());
            }
            if (updatePostReq.getContent() != null) {
                post.setContent(updatePostReq.getContent());
            }
            if (updatePostReq.getCategories() != null) {
                post.setCategories(updatePostReq.getCategories());
            }
            if (updatePostReq.getVisibility() != null) {
                post.setVisibility(updatePostReq.getVisibility());
            }
        } else if (updatePostReq.getType().equals("like")) {
            PostLike exReaction = postLikeRepository
                    .findByPostAndUser(post, user)
                    .orElse(null);

            if (updatePostReq.isLike()) {
                // User wants to LIKE
                if (exReaction == null) {
                    // No reaction exists, create new like
                    PostLike newLike = new PostLike();
                    newLike.setPost(post);
                    newLike.setUser(user);
                    newLike.setType("LIKE");
                    postLikeRepository.save(newLike);
                    post.getPostLikes().add(newLike);
                } else if (!"LIKE".equals(exReaction.getType())) {
                    // Existing dislike, change to like
                    post.getPostLikes().remove(exReaction);

                    System.out.println(
                            "Existing dislike, changing to likegggggggggggggggggggggggggggg" + exReaction.getType());
                    exReaction.setType("LIKE");
                    postLikeRepository.save(exReaction);
                    System.out.println(
                            "Existing dislike, changing to likegggggggggggggggggggggggggggg" + exReaction.getType());
                    post.getPostLikes().add(exReaction);
                }
            } else {
                // User wants to UNLIKE
                if (exReaction != null && "LIKE".equals(exReaction.getType())) {
                    postLikeRepository.delete(exReaction);
                    post.getPostLikes().remove(exReaction);
                }
            }
        } else if (updatePostReq.getType().equals("dislike")) {
            // Check if there's ANY existing reaction (like or dislike)
            PostLike exReaction = postLikeRepository
                    .findByPostAndUser(post, user) // You need this method
                    .orElse(null);

            if (updatePostReq.isLike()) {
                // User wants to DISLIKE
                if (exReaction == null) {
                    // No reaction exists, create new dislike
                    PostLike newDislike = new PostLike();
                    newDislike.setPost(post);
                    newDislike.setUser(user);
                    newDislike.setType("DISLIKE");
                    postLikeRepository.save(newDislike);
                    post.getPostLikes().add(newDislike);
                } else if (!"DISLIKE".equals(exReaction.getType())) {
                    // Existing like, change to dislike
                    System.out.println(
                            "Existing like, changing to likegggggggggggggggggggggggggggg" + exReaction.getType());
                    exReaction.setType("DISLIKE");
                    postLikeRepository.save(exReaction);
                    // Update the in-memory collection
                    System.out.println(
                            "Existing like, changing to likegggggggggggggggggggggggggggg" + exReaction.getType());
                    post.getPostLikes().remove(exReaction);
                    post.getPostLikes().add(exReaction);
                }
                // If already disliked, do nothing
            } else {
                // User wants to REMOVE DISLIKE
                if (exReaction != null && "DISLIKE".equals(exReaction.getType())) {
                    postLikeRepository.delete(exReaction);
                    post.getPostLikes().remove(exReaction);
                }
            }
        } else if (updatePostReq.getType().equals("comment")) {
            Comment comment = new Comment();
            comment.setContent(updatePostReq.getComment());
            comment.setPost(post);
            comment.setUser(user);
            post.addComment(comment);
        }

        System.out.println("Post updated successfullyjggggggggggggggggggggggggggggggggggggggggggggggggg"
                + post.getPostLikes().size());
        postRepository.save(post);
        return getPostData(post);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public ResponseEntity<String> reportPost(Long postId, String reporterUsername, String reason) {
        // User user = userRepository.findByUsername(reporterUsername)
        //         .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (post.getReports().stream().anyMatch(r -> r.getFromUser().equals(reporterUsername))) {
            // System.out.println("You have already reported this post");
            return ResponseEntity.ok("You have already reported this post");
        }
        Report report = new Report();
        report.setFromUser(reporterUsername);
        report.setPost(post);
        report.setReason(reason);
        report.setUser(null);
        report.setComment(null);
        report.setType("POST");
        post.addReport(report);
        postRepository.save(post);
        reportsRepo.save(report);
        return ResponseEntity.ok("Post reported successfully");
    }

    public ResponseEntity<String> reportComment(Long commentId, String reporterUsername) {
        // User user = userRepository.findByUsername(currentusername)
        //         .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if(comment.getReports().stream().anyMatch(r -> r.getFromUser().equals(reporterUsername))) {
            return ResponseEntity.ok("You have already reported this comment");
        }
        Report report = new Report();
        report.setFromUser(reporterUsername);
        report.setComment(comment);
        report.setReason("comment reported");
        report.setPost(null);
        report.setUser(null);
        report.setType("COMMENT");
        comment.addReport(report);
        commentRepository.save(comment);
        reportsRepo.save(report);
        return ResponseEntity.ok("Comment reported successfully");
    }

    public GetPostsRequest likeComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PostLike existingLike = postLikeRepository.findByCommentAndUser(comment, user).orElse(null);
        if (existingLike != null && existingLike.getType().equals("LIKE")) {
            postLikeRepository.delete(existingLike);
            comment.getCommentLikes().remove(existingLike);
            commentRepository.save(comment);
            return getPostData(postRepository.save(comment.getPost()));

        } else if (existingLike != null && existingLike.getType().equals("DISLIKE")) {
            postLikeRepository.delete(existingLike);
            comment.getCommentLikes().remove(existingLike);
            commentRepository.save(comment);
            
        }
            PostLike like = new PostLike();
            like.setComment(comment);
            like.setUser(user);
            like.setType("LIKE");
            comment.getCommentLikes().add(like);
            postLikeRepository.save(like);

        commentRepository.save(comment);
        return getPostData(postRepository.save(comment.getPost()));

    }

    public GetPostsRequest dislikeComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PostLike existingLike = postLikeRepository.findByCommentAndUser(comment, user).orElse(null);
        if (existingLike != null && existingLike.getType().equals("DISLIKE")) {
            postLikeRepository.delete(existingLike);
            comment.getCommentLikes().remove(existingLike);
            commentRepository.save(comment);
            return getPostData(postRepository.save(comment.getPost()));

        } else if (existingLike != null && existingLike.getType().equals("LIKE")) {
            postLikeRepository.delete(existingLike);
            comment.getCommentLikes().remove(existingLike);
            commentRepository.save(comment);
            
        }
            PostLike like = new PostLike();
            like.setComment(comment);
            like.setUser(user);
            like.setType("DISLIKE");
            comment.getCommentLikes().add(like);
            postLikeRepository.save(like);

        commentRepository.save(comment);
        return getPostData(postRepository.save(comment.getPost()));
    }
}