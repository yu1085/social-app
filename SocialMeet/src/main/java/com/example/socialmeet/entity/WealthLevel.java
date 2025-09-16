package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wealth_levels")
public class WealthLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "level", nullable = false, unique = true)
    private Integer level;
    
    @Column(name = "min_contribution", precision = 10, scale = 2, nullable = false)
    private BigDecimal minContribution;
    
    @Column(name = "max_contribution", precision = 10, scale = 2)
    private BigDecimal maxContribution;
    
    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;
    
    @Column(name = "icon_url", length = 500)
    private String iconUrl;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
    public WealthLevel() {}
    
    public WealthLevel(String name, Integer level, BigDecimal minContribution, BigDecimal maxContribution, String benefits) {
        this.name = name;
        this.level = level;
        this.minContribution = minContribution;
        this.maxContribution = maxContribution;
        this.benefits = benefits;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public BigDecimal getMinContribution() {
        return minContribution;
    }
    
    public void setMinContribution(BigDecimal minContribution) {
        this.minContribution = minContribution;
    }
    
    public BigDecimal getMaxContribution() {
        return maxContribution;
    }
    
    public void setMaxContribution(BigDecimal maxContribution) {
        this.maxContribution = maxContribution;
    }
    
    public String getBenefits() {
        return benefits;
    }
    
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    public boolean matchesContribution(BigDecimal contribution) {
        if (maxContribution == null) {
            return contribution.compareTo(minContribution) >= 0;
        }
        return contribution.compareTo(minContribution) >= 0 && 
               contribution.compareTo(maxContribution) < 0;
    }
}
