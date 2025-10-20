package com.socialmeet.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 靓号转让DTO
 */
public class LuckyNumberTransferDTO {
    
    private Long id;
    private Long luckyNumberId;
    private String luckyNumber;
    private Long fromUserId;
    private String fromUserName;
    private Long toUserId;
    private String toUserName;
    private BigDecimal transferPrice;
    private String transferReason;
    private String status; // PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED
    private String statusDisplayName;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private LocalDateTime completedTime;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public LuckyNumberTransferDTO() {}
    
    public LuckyNumberTransferDTO(Long id, Long luckyNumberId, String luckyNumber,
                                 Long fromUserId, String fromUserName, Long toUserId, 
                                 String toUserName, BigDecimal transferPrice, 
                                 String transferReason, String status, String statusDisplayName,
                                 LocalDateTime requestTime, LocalDateTime responseTime,
                                 LocalDateTime completedTime, String remark,
                                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.luckyNumberId = luckyNumberId;
        this.luckyNumber = luckyNumber;
        this.fromUserId = fromUserId;
        this.fromUserName = fromUserName;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.transferPrice = transferPrice;
        this.transferReason = transferReason;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.completedTime = completedTime;
        this.remark = remark;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLuckyNumberId() { return luckyNumberId; }
    public void setLuckyNumberId(Long luckyNumberId) { this.luckyNumberId = luckyNumberId; }
    
    public String getLuckyNumber() { return luckyNumber; }
    public void setLuckyNumber(String luckyNumber) { this.luckyNumber = luckyNumber; }
    
    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    
    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }
    
    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    
    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }
    
    public BigDecimal getTransferPrice() { return transferPrice; }
    public void setTransferPrice(BigDecimal transferPrice) { this.transferPrice = transferPrice; }
    
    public String getTransferReason() { return transferReason; }
    public void setTransferReason(String transferReason) { this.transferReason = transferReason; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    
    public LocalDateTime getResponseTime() { return responseTime; }
    public void setResponseTime(LocalDateTime responseTime) { this.responseTime = responseTime; }
    
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
