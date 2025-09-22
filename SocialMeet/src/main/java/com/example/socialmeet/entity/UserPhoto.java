package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_photos")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserPhoto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "photo_url", length = 500, nullable = false)
    private String photoUrl;
    
    @Column(name = "is_avatar", nullable = false)
    private Boolean isAvatar = false;
    
    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
