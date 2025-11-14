package com._Talent._blog.repositery;

import org.springframework.data.jpa.repository.JpaRepository;
import com._Talent._blog.model.Entity.*;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    // Optional<User> update(User user);
}