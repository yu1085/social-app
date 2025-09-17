package com.example.socialmeet.dto;

public class UserSettingsDTO {
    private Long id;
    private Long userId;
    private Boolean callEnabled;
    private Double callPricePerMinute;
    private Integer callMinDuration;
    private Boolean videoCallEnabled;
    private Double videoCallPricePerMinute;
    private Boolean voiceCallEnabled;
    private Double voiceCallPricePerMinute;
    private Double messagePricePerMessage;
    private Boolean autoAcceptCalls;
    private Boolean showOnlineStatus;
    private Boolean allowStrangerCalls;

    // Constructors
    public UserSettingsDTO() {}

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
}
