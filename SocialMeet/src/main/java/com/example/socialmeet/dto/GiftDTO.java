package com.example.socialmeet.dto;

import com.example.socialmeet.entity.Gift;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiftDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Gift.GiftCategory category;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public GiftDTO() {}
    
    public GiftDTO(Gift gift) {
        this.id = gift.getId();
        this.name = gift.getName();
        this.description = gift.getDescription();
        this.imageUrl = gift.getImageUrl();
        this.price = gift.getPrice();
        this.category = gift.getCategory();
        this.isActive = gift.getIsActive();
        this.createdAt = gift.getCreatedAt();
        this.updatedAt = gift.getUpdatedAt();
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Gift.GiftCategory getCategory() {
        return category;
    }
    
    public void setCategory(Gift.GiftCategory category) {
        this.category = category;
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
}
