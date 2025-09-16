package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_views")
public class UserView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "viewer_id", nullable = false)
    private Long viewerId;
    
    @Column(name = "viewed_id", nullable = false)
    private Long viewedId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "view_type")
    private ViewType viewType = ViewType.PROFILE;
    
    @Column(name = "related_id")
    private Long relatedId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public UserView() {}
    
    public UserView(Long viewerId, Long viewedId, ViewType viewType, Long relatedId) {
        this.viewerId = viewerId;
        this.viewedId = viewedId;
        this.viewType = viewType;
        this.relatedId = relatedId;
    }
    
    // Enums
    public enum ViewType {
        PROFILE, POST, MESSAGE
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getViewerId() {
        return viewerId;
    }
    
    public void setViewerId(Long viewerId) {
        this.viewerId = viewerId;
    }
    
    public Long getViewedId() {
        return viewedId;
    }
    
    public void setViewedId(Long viewedId) {
        this.viewedId = viewedId;
    }
    
    public ViewType getViewType() {
        return viewType;
    }
    
    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }
    
    public Long getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
