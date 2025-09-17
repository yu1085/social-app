package com.example.socialmeet.dto;

import com.example.socialmeet.entity.GuardRelationship;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GuardRelationshipDTO {
    private Long id;
    private Long guardianId;
    private Long protectedId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private BigDecimal totalContribution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 守护者信息
    private String guardianNickname;
    private String guardianAvatar;
    
    // 被守护者信息
    private String protectedNickname;
    private String protectedAvatar;
    
    // Constructors
    public GuardRelationshipDTO() {}
    
    public GuardRelationshipDTO(GuardRelationship relationship) {
        this.id = relationship.getId();
        this.guardianId = relationship.getGuardianId();
        this.protectedId = relationship.getProtectedId();
        this.startDate = relationship.getStartDate();
        this.endDate = relationship.getEndDate();
        this.status = relationship.getStatus();
        this.totalContribution = relationship.getTotalContribution();
        this.createdAt = relationship.getCreatedAt();
        this.updatedAt = relationship.getUpdatedAt();
    }
    
    // Getters and Setters
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
    
    public String getGuardianNickname() {
        return guardianNickname;
    }
    
    public void setGuardianNickname(String guardianNickname) {
        this.guardianNickname = guardianNickname;
    }
    
    public String getGuardianAvatar() {
        return guardianAvatar;
    }
    
    public void setGuardianAvatar(String guardianAvatar) {
        this.guardianAvatar = guardianAvatar;
    }
    
    public String getProtectedNickname() {
        return protectedNickname;
    }
    
    public void setProtectedNickname(String protectedNickname) {
        this.protectedNickname = protectedNickname;
    }
    
    public String getProtectedAvatar() {
        return protectedAvatar;
    }
    
    public void setProtectedAvatar(String protectedAvatar) {
        this.protectedAvatar = protectedAvatar;
    }
}
