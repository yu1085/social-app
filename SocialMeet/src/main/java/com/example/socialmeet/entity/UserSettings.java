package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 来电设置
    @Column(name = "call_enabled")
    private Boolean callEnabled = true;

    @Column(name = "call_price_per_minute")
    private Double callPricePerMinute = 1.0;

    @Column(name = "call_min_duration")
    private Integer callMinDuration = 1;

    // 视频通话设置
    @Column(name = "video_call_enabled")
    private Boolean videoCallEnabled = true;

    @Column(name = "video_call_price_per_minute")
    private Double videoCallPricePerMinute = 2.0;

    // 语音通话设置
    @Column(name = "voice_call_enabled")
    private Boolean voiceCallEnabled = true;

    @Column(name = "voice_call_price_per_minute")
    private Double voiceCallPricePerMinute = 0.5;

    // 消息设置
    @Column(name = "message_price_per_message")
    private Double messagePricePerMessage = 0.1;

    // 其他设置
    @Column(name = "auto_accept_calls")
    private Boolean autoAcceptCalls = false;

    @Column(name = "show_online_status")
    private Boolean showOnlineStatus = true;

    @Column(name = "allow_stranger_calls")
    private Boolean allowStrangerCalls = true;

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
    public UserSettings() {}

    public UserSettings(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
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

    public Boolean getCallEnabled() {
        return callEnabled;
    }

    public void setCallEnabled(Boolean callEnabled) {
        this.callEnabled = callEnabled;
    }

    public Double getCallPricePerMinute() {
        return callPricePerMinute;
    }

    public void setCallPricePerMinute(Double callPricePerMinute) {
        this.callPricePerMinute = callPricePerMinute;
    }

    public Integer getCallMinDuration() {
        return callMinDuration;
    }

    public void setCallMinDuration(Integer callMinDuration) {
        this.callMinDuration = callMinDuration;
    }

    public Boolean getVideoCallEnabled() {
        return videoCallEnabled;
    }

    public void setVideoCallEnabled(Boolean videoCallEnabled) {
        this.videoCallEnabled = videoCallEnabled;
    }

    public Double getVideoCallPricePerMinute() {
        return videoCallPricePerMinute;
    }

    public void setVideoCallPricePerMinute(Double videoCallPricePerMinute) {
        this.videoCallPricePerMinute = videoCallPricePerMinute;
    }

    public Boolean getVoiceCallEnabled() {
        return voiceCallEnabled;
    }

    public void setVoiceCallEnabled(Boolean voiceCallEnabled) {
        this.voiceCallEnabled = voiceCallEnabled;
    }

    public Double getVoiceCallPricePerMinute() {
        return voiceCallPricePerMinute;
    }

    public void setVoiceCallPricePerMinute(Double voiceCallPricePerMinute) {
        this.voiceCallPricePerMinute = voiceCallPricePerMinute;
    }

    public Double getMessagePricePerMessage() {
        return messagePricePerMessage;
    }

    public void setMessagePricePerMessage(Double messagePricePerMessage) {
        this.messagePricePerMessage = messagePricePerMessage;
    }

    public Boolean getAutoAcceptCalls() {
        return autoAcceptCalls;
    }

    public void setAutoAcceptCalls(Boolean autoAcceptCalls) {
        this.autoAcceptCalls = autoAcceptCalls;
    }

    public Boolean getShowOnlineStatus() {
        return showOnlineStatus;
    }

    public void setShowOnlineStatus(Boolean showOnlineStatus) {
        this.showOnlineStatus = showOnlineStatus;
    }

    public Boolean getAllowStrangerCalls() {
        return allowStrangerCalls;
    }

    public void setAllowStrangerCalls(Boolean allowStrangerCalls) {
        this.allowStrangerCalls = allowStrangerCalls;
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
