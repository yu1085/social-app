package com.example.socialmeet.dto;

import com.example.socialmeet.entity.GiftRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiftRecordDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long giftId;
    private String giftName;
    private String giftImageUrl;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String message;
    private LocalDateTime createdAt;
    
    // 发送者信息
    private String senderNickname;
    private String senderAvatar;
    
    // 接收者信息
    private String receiverNickname;
    private String receiverAvatar;
    
    // Constructors
    public GiftRecordDTO() {}
    
    public GiftRecordDTO(GiftRecord giftRecord) {
        this.id = giftRecord.getId();
        this.senderId = giftRecord.getSenderId();
        this.receiverId = giftRecord.getReceiverId();
        this.giftId = giftRecord.getGiftId();
        this.quantity = giftRecord.getQuantity();
        this.totalAmount = giftRecord.getTotalAmount();
        this.message = giftRecord.getMessage();
        this.createdAt = giftRecord.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public Long getGiftId() {
        return giftId;
    }
    
    public void setGiftId(Long giftId) {
        this.giftId = giftId;
    }
    
    public String getGiftName() {
        return giftName;
    }
    
    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }
    
    public String getGiftImageUrl() {
        return giftImageUrl;
    }
    
    public void setGiftImageUrl(String giftImageUrl) {
        this.giftImageUrl = giftImageUrl;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getSenderNickname() {
        return senderNickname;
    }
    
    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }
    
    public String getSenderAvatar() {
        return senderAvatar;
    }
    
    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
    
    public String getReceiverNickname() {
        return receiverNickname;
    }
    
    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }
    
    public String getReceiverAvatar() {
        return receiverAvatar;
    }
    
    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
    }
}
