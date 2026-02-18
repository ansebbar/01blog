package com._Talent._blog.repositery;

import com._Talent._blog.model.Entity.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowingRepository extends JpaRepository<UserFollowing, Long> {
    
    Optional<UserFollowing> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    @Query("SELECT COUNT(uf) FROM UserFollowing uf WHERE uf.follower.id = :userId")
    Long countByFollowerId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(uf) FROM UserFollowing uf WHERE uf.following.id = :userId")
    Long countByFollowingId(@Param("userId") Long userId);
    
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}