package com.socialmeet.backend.dto;

import java.math.BigDecimal;

/**
 * VIP特权DTO
 */
public class VipBenefitDTO {
    
    private String benefitName;
    private String benefitDescription;
    private String iconIdentifier;
    private String discountText;
    private BigDecimal discountValue;
    private String benefitType;
    private Boolean isLocked;
    private Integer sortOrder;
    
    // 构造函数
    public VipBenefitDTO() {}
    
    public VipBenefitDTO(String benefitName, String benefitDescription, String iconIdentifier, 
                        String discountText, BigDecimal discountValue, String benefitType, 
                        Boolean isLocked, Integer sortOrder) {
        this.benefitName = benefitName;
        this.benefitDescription = benefitDescription;
        this.iconIdentifier = iconIdentifier;
        this.discountText = discountText;
        this.discountValue = discountValue;
        this.benefitType = benefitType;
        this.isLocked = isLocked;
        this.sortOrder = sortOrder;
    }
    
    // Getters and Setters
    public String getBenefitName() { return benefitName; }
    public void setBenefitName(String benefitName) { this.benefitName = benefitName; }
    
    public String getBenefitDescription() { return benefitDescription; }
    public void setBenefitDescription(String benefitDescription) { this.benefitDescription = benefitDescription; }
    
    public String getIconIdentifier() { return iconIdentifier; }
    public void setIconIdentifier(String iconIdentifier) { this.iconIdentifier = iconIdentifier; }
    
    public String getDiscountText() { return discountText; }
    public void setDiscountText(String discountText) { this.discountText = discountText; }
    
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    
    public String getBenefitType() { return benefitType; }
    public void setBenefitType(String benefitType) { this.benefitType = benefitType; }
    
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
