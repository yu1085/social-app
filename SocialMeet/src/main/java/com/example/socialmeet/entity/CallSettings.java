package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 通话设置实体
 */
@Entity
@Table(name = "call_settings")
public class CallSettings {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    // 视频接听设置
    @Column(name = "video_call_enabled", nullable = false)
    private Boolean videoCallEnabled = true;  // 默认开启视频接听
    
    @Column(name = "video_call_price", nullable = false)
    private Double videoCallPrice = 100.0;  // 默认100/分钟
    
    // 语音接听设置
    @Column(name = "voice_call_enabled", nullable = false)
    private Boolean voiceCallEnabled = true;  // 默认开启语音接听
    
    @Column(name = "voice_call_price", nullable = false)
    private Double voiceCallPrice = 100.0;  // 默认100/分钟
    
    // 私信收费设置
    @Column(name = "message_charge_enabled", nullable = false)
    private Boolean messageChargeEnabled = false;
    
    @Column(name = "message_price", nullable = false)
    private Double messagePrice = 0.0;
    
    // 免费接听时长（分钟）
    @Column(name = "free_call_duration", nullable = false)
    private Integer freeCallDuration = 0;
    
    // 自动接听设置
    @Column(name = "auto_answer_enabled", nullable = false)
    private Boolean autoAnswerEnabled = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 构造函数
    public CallSettings() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public CallSettings(Long userId) {
        this();
        this.userId = userId;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getVideoCallEnabled() {
        return videoCallEnabled;
    }
    
    public void setVideoCallEnabled(Boolean videoCallEnabled) {
        this.videoCallEnabled = videoCallEnabled;
    }
    
    public Double getVideoCallPrice() {
        return videoCallPrice;
    }
    
    public void setVideoCallPrice(Double videoCallPrice) {
        this.videoCallPrice = videoCallPrice;
    }
    
    public Boolean getVoiceCallEnabled() {
        return voiceCallEnabled;
    }
    
    public void setVoiceCallEnabled(Boolean voiceCallEnabled) {
        this.voiceCallEnabled = voiceCallEnabled;
    }
    
    public Double getVoiceCallPrice() {
        return voiceCallPrice;
    }
    
    public void setVoiceCallPrice(Double voiceCallPrice) {
        this.voiceCallPrice = voiceCallPrice;
    }
    
    public Boolean getMessageChargeEnabled() {
        return messageChargeEnabled;
    }
    
    public void setMessageChargeEnabled(Boolean messageChargeEnabled) {
        this.messageChargeEnabled = messageChargeEnabled;
    }
    
    public Double getMessagePrice() {
        return messagePrice;
    }
    
    public void setMessagePrice(Double messagePrice) {
        this.messagePrice = messagePrice;
    }
    
    public Integer getFreeCallDuration() {
        return freeCallDuration;
    }
    
    public void setFreeCallDuration(Integer freeCallDuration) {
        this.freeCallDuration = freeCallDuration;
    }
    
    public Boolean getAutoAnswerEnabled() {
        return autoAnswerEnabled;
    }
    
    public void setAutoAnswerEnabled(Boolean autoAnswerEnabled) {
        this.autoAnswerEnabled = autoAnswerEnabled;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
