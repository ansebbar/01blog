package com._Talent._blog.repositery;
import com._Talent._blog.model.Entity.Comment;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByUser(User user);
    Long countByPost(Post post);
}