package com._Talent._blog.repositery;

import org.springframework.data.jpa.repository.JpaRepository;
import com._Talent._blog.model.Entity.PostLike;
import java.util.*;
import com._Talent._blog.model.Entity.*;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    Optional<PostLike> findByCommentAndUser(Comment comment, User user);
    Optional<PostLike> findByPostAndUserAndType(Post post, User user, String Type);
}
