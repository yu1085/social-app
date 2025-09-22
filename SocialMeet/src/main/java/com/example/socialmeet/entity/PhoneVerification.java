package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_verifications")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PhoneVerification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;
    
    @Column(name = "verification_code", length = 10, nullable = false)
    private String verificationCode;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PENDING"; // PENDING, VERIFIED, EXPIRED
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
