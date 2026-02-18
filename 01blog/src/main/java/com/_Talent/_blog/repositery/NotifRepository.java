package com._Talent._blog.repositery;

import org.springframework.data.jpa.repository.JpaRepository;

import com._Talent._blog.model.Entity.Report;

public interface NotifRepository extends JpaRepository<Report, Long> {
    
}
