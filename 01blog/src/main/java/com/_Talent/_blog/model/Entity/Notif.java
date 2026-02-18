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
public class Notif {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @Column(nullable = false)
  private String fromUser; 
@Column(nullable = false) private String reason;
@Column(nullable = false) private String type;
@Column(nullable = false) private Long contentId;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user; 
@Column(name = "created_at") private LocalDateTime createdAt;

@PrePersist protected void onCreate()
  { createdAt = LocalDateTime.now(); }
}
