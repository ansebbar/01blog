package com._Talent._blog.repositery;
import com._Talent._blog.model.Entity.Post;
import com._Talent._blog.model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCreator(User creator);
    List<Post> findByCreatorId(Long userId);
    
    @Query("SELECT p FROM Post p WHERE p.creator.username = :username ORDER BY p.createdAt DESC")
    List<Post> findPostsByUsername(@Param("username") String username);
    

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword%")
    List<Post> searchByTitle(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE CONCAT('%', :keyword, '%')")
    List<Post> searchByTitleWithConcat(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword%")
    List<Post> searchByContent(@Param("keyword") String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> searchByTitleOrContent(@Param("keyword") String keyword);

    
}