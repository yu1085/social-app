package com.socialmeet.backend.dto;

/**
 * 购买请求DTO
 */
public class PurchaseRequest {
    
    private Long luckyNumberId;
    private Integer validityDays; // 购买时长（天）
    
    // 构造函数
    public PurchaseRequest() {}
    
    public PurchaseRequest(Long luckyNumberId, Integer validityDays) {
        this.luckyNumberId = luckyNumberId;
        this.validityDays = validityDays;
    }
    
    // Getters and Setters
    public Long getLuckyNumberId() { return luckyNumberId; }
    public void setLuckyNumberId(Long luckyNumberId) { this.luckyNumberId = luckyNumberId; }
    
    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }
}
