package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 匹配会话实体
 */
@Entity
@Table(name = "match_sessions")
public class MatchSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", unique = true, nullable = false)
    private String sessionId;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Column(name = "match_type", nullable = false)
    private String matchType; // VIDEO, VOICE
    
    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, ENDED, TIMEOUT
    
    @Column(name = "call_price", nullable = false)
    private Double callPrice;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    public MatchSession() {}
    
    public MatchSession(String sessionId, Long user1Id, Long user2Id, 
                       String matchType, Double callPrice) {
        this.sessionId = sessionId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.matchType = matchType;
        this.callPrice = callPrice;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public Long getUser1Id() { return user1Id; }
    public void setUser1Id(Long user1Id) { this.user1Id = user1Id; }
    
    public Long getUser2Id() { return user2Id; }
    public void setUser2Id(Long user2Id) { this.user2Id = user2Id; }
    
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Double getCallPrice() { return callPrice; }
    public void setCallPrice(Double callPrice) { this.callPrice = callPrice; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
}
