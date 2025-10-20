package com.example.myapplication.dto;

/**
 * 用户设置DTO
 */
public class UserSettingsDTO {
    
    private Boolean voiceCallEnabled;
    private Boolean videoCallEnabled;
    private Boolean messageChargeEnabled;
    private Double voiceCallPrice;
    private Double videoCallPrice;
    private Double messagePrice;
    
    // 构造函数
    public UserSettingsDTO() {}
    
    public UserSettingsDTO(Boolean voiceCallEnabled, Boolean videoCallEnabled, Boolean messageChargeEnabled,
                          Double voiceCallPrice, Double videoCallPrice, Double messagePrice) {
        this.voiceCallEnabled = voiceCallEnabled;
        this.videoCallEnabled = videoCallEnabled;
        this.messageChargeEnabled = messageChargeEnabled;
        this.voiceCallPrice = voiceCallPrice;
        this.videoCallPrice = videoCallPrice;
        this.messagePrice = messagePrice;
    }
    
    // Getter和Setter方法
    public Boolean getVoiceCallEnabled() { return voiceCallEnabled; }
    public void setVoiceCallEnabled(Boolean voiceCallEnabled) { this.voiceCallEnabled = voiceCallEnabled; }
    
    public Boolean getVideoCallEnabled() { return videoCallEnabled; }
    public void setVideoCallEnabled(Boolean videoCallEnabled) { this.videoCallEnabled = videoCallEnabled; }
    
    public Boolean getMessageChargeEnabled() { return messageChargeEnabled; }
    public void setMessageChargeEnabled(Boolean messageChargeEnabled) { this.messageChargeEnabled = messageChargeEnabled; }
    
    public Double getVoiceCallPrice() { return voiceCallPrice; }
    public void setVoiceCallPrice(Double voiceCallPrice) { this.voiceCallPrice = voiceCallPrice; }
    
    public Double getVideoCallPrice() { return videoCallPrice; }
    public void setVideoCallPrice(Double videoCallPrice) { this.videoCallPrice = videoCallPrice; }
    
    public Double getMessagePrice() { return messagePrice; }
    public void setMessagePrice(Double messagePrice) { this.messagePrice = messagePrice; }
}
