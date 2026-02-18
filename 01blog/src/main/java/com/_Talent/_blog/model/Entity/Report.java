package com._Talent._blog.model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report {



    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY) 
@Column(name = "report_id")
private Long id;
 @Column(nullable = false)
  private String fromUser; 
@Column(nullable = false) private String reason;
@Column(nullable = false) private String type;
@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id") private Post post;
 @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "comment_id") private Comment comment;
 @Column(name = "created_at") private LocalDateTime createdAt;
 @PrePersist 
 protected void onCreate()
  { createdAt = LocalDateTime.now(); }

}
