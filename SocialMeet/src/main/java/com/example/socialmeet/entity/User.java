package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(length = 100)
    private String password;
    
    @Column(length = 100)
    private String nickname;
    
    @Column(unique = true, length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    
    @Column(length = 500)
    private String bio;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "height")
    private Integer height; // 身高(cm)
    
    @Column(name = "weight")
    private Integer weight; // 体重(kg)
    
    @Column(name = "education", length = 50)
    private String education; // 学历
    
    @Column(name = "income", length = 50)
    private String income; // 收入
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Gender {
        MALE, FEMALE
    }
}
