package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 匹配请求实体
 */
@Entity
@Table(name = "match_requests")
public class MatchRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "match_type", nullable = false)
    private String matchType; // VIDEO, VOICE
    
    @Column(name = "preference_level", nullable = false)
    private Integer preferenceLevel; // 1=活跃女生, 2=人气女生, 3=高颜女生
    
    @Column(name = "min_price")
    private Double minPrice;
    
    @Column(name = "max_price")
    private Double maxPrice;
    
    @Column(name = "status", nullable = false)
    private String status; // PENDING, MATCHED, EXPIRED, CANCELLED
    
    @Column(name = "matched_user_id")
    private Long matchedUserId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "matched_at")
    private LocalDateTime matchedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    public MatchRequest() {}
    
    public MatchRequest(Long userId, String matchType, Integer preferenceLevel, 
                       Double minPrice, Double maxPrice) {
        this.userId = userId;
        this.matchType = matchType;
        this.preferenceLevel = preferenceLevel;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5); // 5分钟过期
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    
    public Integer getPreferenceLevel() { return preferenceLevel; }
    public void setPreferenceLevel(Integer preferenceLevel) { this.preferenceLevel = preferenceLevel; }
    
    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }
    
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getMatchedUserId() { return matchedUserId; }
    public void setMatchedUserId(Long matchedUserId) { this.matchedUserId = matchedUserId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
