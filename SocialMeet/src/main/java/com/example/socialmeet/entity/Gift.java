package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gifts")
public class Gift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "sub_category", length = 50)
    private String subCategory; // 子分类
    
    @Column(name = "rarity", length = 20)
    private String rarity = "COMMON"; // 稀有度：COMMON, RARE, EPIC, LEGENDARY
    
    @Column(name = "effect_type", length = 50)
    private String effectType; // 特效类型
    
    @Column(name = "animation_url", length = 500)
    private String animationUrl; // 动画资源URL
    
    @Column(name = "sound_url", length = 500)
    private String soundUrl; // 音效资源URL
    
    @Column(name = "is_limited")
    private Boolean isLimited = false; // 是否限量
    
    @Column(name = "limited_quantity")
    private Integer limitedQuantity; // 限量数量
    
    @Column(name = "sold_quantity")
    private Integer soldQuantity = 0; // 已售数量
    
    @Column(name = "is_hot")
    private Boolean isHot = false; // 是否热门
    
    @Column(name = "is_new")
    private Boolean isNew = false; // 是否新品
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序
    
    @Column(name = "tags", length = 500)
    private String tags; // 标签，JSON格式
    
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
    public Gift() {}
    
    public Gift(String name, String description, BigDecimal price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }    // Getters and Setters
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
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
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
    
    // 新增字段的getter和setter方法
    public String getSubCategory() {
        return subCategory;
    }
    
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
    
    public String getRarity() {
        return rarity;
    }
    
    public void setRarity(String rarity) {
        this.rarity = rarity;
    }
    
    public String getEffectType() {
        return effectType;
    }
    
    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }
    
    public String getAnimationUrl() {
        return animationUrl;
    }
    
    public void setAnimationUrl(String animationUrl) {
        this.animationUrl = animationUrl;
    }
    
    public String getSoundUrl() {
        return soundUrl;
    }
    
    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }
    
    public Boolean getIsLimited() {
        return isLimited;
    }
    
    public void setIsLimited(Boolean isLimited) {
        this.isLimited = isLimited;
    }
    
    public Integer getLimitedQuantity() {
        return limitedQuantity;
    }
    
    public void setLimitedQuantity(Integer limitedQuantity) {
        this.limitedQuantity = limitedQuantity;
    }
    
    public Integer getSoldQuantity() {
        return soldQuantity;
    }
    
    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }
    
    public Boolean getIsHot() {
        return isHot;
    }
    
    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }
    
    public Boolean getIsNew() {
        return isNew;
    }
    
    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    // 礼物分类枚举
    public enum GiftCategory {
        EMOTION("情感表达", "表达情感的礼物"),
        CELEBRATION("庆祝", "庆祝类礼物"),
        ROMANCE("浪漫", "浪漫类礼物"),
        FRIENDSHIP("友谊", "友谊类礼物"),
        HOLIDAY("节日", "节日类礼物"),
        SPECIAL("特殊", "特殊类礼物"),
        LIMITED("限量", "限量版礼物");
        
        private final String displayName;
        private final String description;
        
        GiftCategory(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 稀有度枚举
    public enum Rarity {
        COMMON("普通", "普通礼物", 1),
        RARE("稀有", "稀有礼物", 2),
        EPIC("史诗", "史诗礼物", 3),
        LEGENDARY("传说", "传说礼物", 4);
        
        private final String displayName;
        private final String description;
        private final Integer level;
        
        Rarity(String displayName, String description, Integer level) {
            this.displayName = displayName;
            this.description = description;
            this.level = level;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Integer getLevel() {
            return level;
        }
    }
}
