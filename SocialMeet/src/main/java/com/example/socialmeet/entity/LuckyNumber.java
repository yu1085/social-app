package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lucky_numbers")
public class LuckyNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "number", nullable = false, unique = true)
    private String number;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private LuckyNumberTier tier;
    
    @Column(name = "price", nullable = false)
    private Long price;
    
    @Column(name = "is_limited", nullable = false)
    private Boolean isLimited = false;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "icon", nullable = false)
    private String icon = "靓";
    
    @Column(name = "icon_color", nullable = false)
    private String iconColor = "#FFD700";
    
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
    public LuckyNumber() {}
    
    public LuckyNumber(String number, LuckyNumberTier tier, Long price) {
        this.number = number;
        this.tier = tier;
        this.price = price;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    
    public LuckyNumberTier getTier() { return tier; }
    public void setTier(LuckyNumberTier tier) { this.tier = tier; }
    
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
    
    public Boolean getIsLimited() { return isLimited; }
    public void setIsLimited(Boolean isLimited) { this.isLimited = isLimited; }
    
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getIconColor() { return iconColor; }
    public void setIconColor(String iconColor) { this.iconColor = iconColor; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 靓号等级枚举
    public enum LuckyNumberTier {
        LIMITED("限量", 1, "#FF6B6B", "#FFD700"),
        TOP("顶级", 2, "#9C27B0", "#E1BEE7"),
        SUPER("超级", 3, "#673AB7", "#C5CAE9"),
        NORMAL("普通", 4, "#FF9800", "#FFE0B2");
        
        private final String displayName;
        private final int sortOrder;
        private final String color;
        private final String iconColor;
        
        LuckyNumberTier(String displayName, int sortOrder, String color, String iconColor) {
            this.displayName = displayName;
            this.sortOrder = sortOrder;
            this.color = color;
            this.iconColor = iconColor;
        }
        
        public String getDisplayName() { return displayName; }
        public int getSortOrder() { return sortOrder; }
        public String getColor() { return color; }
        public String getIconColor() { return iconColor; }
    }
}
