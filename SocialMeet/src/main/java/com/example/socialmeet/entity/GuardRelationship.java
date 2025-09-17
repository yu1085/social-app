package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "guard_relationships")
public class GuardRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "guardian_id", nullable = false)
    private Long guardianId;
    
    @Column(name = "protected_id", nullable = false)
    private Long protectedId;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "status")
    private String status = "ACTIVE";
    
    @Column(name = "total_contribution", precision = 10, scale = 2)
    private BigDecimal totalContribution = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public GuardRelationship() {}
    
    public GuardRelationship(Long guardianId, Long protectedId) {
        this.guardianId = guardianId;
        this.protectedId = protectedId;
    }    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getGuardianId() {
        return guardianId;
    }
    
    public void setGuardianId(Long guardianId) {
        this.guardianId = guardianId;
    }
    
    public Long getProtectedId() {
        return protectedId;
    }
    
    public void setProtectedId(Long protectedId) {
        this.protectedId = protectedId;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getTotalContribution() {
        return totalContribution;
    }
    
    public void setTotalContribution(BigDecimal totalContribution) {
        this.totalContribution = totalContribution;
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
    public boolean isActive() {
        return status == "ACTIVE" && 
               (endDate == null || LocalDateTime.now().isBefore(endDate));
    }
    
    public void addContribution(BigDecimal amount) {
        this.totalContribution = this.totalContribution.add(amount);
    }
}
