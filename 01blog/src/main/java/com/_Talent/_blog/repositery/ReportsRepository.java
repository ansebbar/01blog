package com._Talent._blog.repositery;

import org.springframework.data.jpa.repository.JpaRepository;
import com._Talent._blog.model.Entity.*;
import java.util.*;

public interface ReportsRepository extends JpaRepository<Report, Long> {
    List<Report> findByPostId(Long postId);
    List<Report> findByUserId(Long userId);
    Report findByFromUserAndComment(String fromUser, Comment comment);
}