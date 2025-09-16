package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intimacy_relationships")
public class IntimacyRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Column(name = "intimacy_score")
    private Integer intimacyScore = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private IntimacyLevel level = IntimacyLevel.STRANGER;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public IntimacyRelationship() {}
    
    public IntimacyRelationship(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }
    
    // Enums
    public enum IntimacyLevel {
        STRANGER, ACQUAINTANCE, FRIEND, CLOSE_FRIEND
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUser1Id() {
        return user1Id;
    }
    
    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }
    
    public Long getUser2Id() {
        return user2Id;
    }
    
    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }
    
    public Integer getIntimacyScore() {
        return intimacyScore;
    }
    
    public void setIntimacyScore(Integer intimacyScore) {
        this.intimacyScore = intimacyScore;
    }
    
    public IntimacyLevel getLevel() {
        return level;
    }
    
    public void setLevel(IntimacyLevel level) {
        this.level = level;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business methods
    public void addScore(int points) {
        this.intimacyScore += points;
        updateLevel();
    }
    
    private void updateLevel() {
        if (intimacyScore >= 1000) {
            this.level = IntimacyLevel.CLOSE_FRIEND;
        } else if (intimacyScore >= 500) {
            this.level = IntimacyLevel.FRIEND;
        } else if (intimacyScore >= 100) {
            this.level = IntimacyLevel.ACQUAINTANCE;
        } else {
            this.level = IntimacyLevel.STRANGER;
        }
    }
}
