package com.example.myapplication.dto;

import java.time.LocalDateTime;

/**
 * VIP信息DTO
 */
public class VipInfoDTO {
    
    private Boolean isVip;
    private Integer vipLevel;
    private String vipLevelName;
    
    private LocalDateTime vipExpireAt;
    
    private Long remainingDays;
    private String vipBenefits;
    private Boolean canUpgrade;
    private String nextLevelName;
    private Integer nextLevelRequirement;
    
    // 构造函数
    public VipInfoDTO() {}
    
    public VipInfoDTO(Boolean isVip, Integer vipLevel, String vipLevelName, LocalDateTime vipExpireAt,
                     Long remainingDays, String vipBenefits, Boolean canUpgrade, 
                     String nextLevelName, Integer nextLevelRequirement) {
        this.isVip = isVip;
        this.vipLevel = vipLevel;
        this.vipLevelName = vipLevelName;
        this.vipExpireAt = vipExpireAt;
        this.remainingDays = remainingDays;
        this.vipBenefits = vipBenefits;
        this.canUpgrade = canUpgrade;
        this.nextLevelName = nextLevelName;
        this.nextLevelRequirement = nextLevelRequirement;
    }
    
    // Getter和Setter方法
    public Boolean getIsVip() { return isVip; }
    public void setIsVip(Boolean isVip) { this.isVip = isVip; }
    
    public Integer getVipLevel() { return vipLevel; }
    public void setVipLevel(Integer vipLevel) { this.vipLevel = vipLevel; }
    
    public String getVipLevelName() { return vipLevelName; }
    public void setVipLevelName(String vipLevelName) { this.vipLevelName = vipLevelName; }
    
    public LocalDateTime getVipExpireAt() { return vipExpireAt; }
    public void setVipExpireAt(LocalDateTime vipExpireAt) { this.vipExpireAt = vipExpireAt; }
    
    public Long getRemainingDays() { return remainingDays; }
    public void setRemainingDays(Long remainingDays) { this.remainingDays = remainingDays; }
    
    public String getVipBenefits() { return vipBenefits; }
    public void setVipBenefits(String vipBenefits) { this.vipBenefits = vipBenefits; }
    
    public Boolean getCanUpgrade() { return canUpgrade; }
    public void setCanUpgrade(Boolean canUpgrade) { this.canUpgrade = canUpgrade; }
    
    public String getNextLevelName() { return nextLevelName; }
    public void setNextLevelName(String nextLevelName) { this.nextLevelName = nextLevelName; }
    
    public Integer getNextLevelRequirement() { return nextLevelRequirement; }
    public void setNextLevelRequirement(Integer nextLevelRequirement) { this.nextLevelRequirement = nextLevelRequirement; }
}
