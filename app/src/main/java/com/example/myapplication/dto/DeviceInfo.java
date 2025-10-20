package com.example.myapplication.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 设备信息DTO
 */
public class DeviceInfo {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("registrationId")
    private String registrationId;
    
    @SerializedName("deviceName")
    private String deviceName;
    
    @SerializedName("deviceType")
    private String deviceType;
    
    @SerializedName("appVersion")
    private String appVersion;
    
    @SerializedName("osVersion")
    private String osVersion;
    
    @SerializedName("isActive")
    private Boolean isActive;
    
    @SerializedName("lastActiveAt")
    private String lastActiveAt;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // 构造函数
    public DeviceInfo() {}

    public DeviceInfo(Long id, Long userId, String registrationId, String deviceName, 
                     String deviceType, String appVersion, String osVersion, 
                     Boolean isActive, String lastActiveAt, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.registrationId = registrationId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.isActive = isActive;
        this.lastActiveAt = lastActiveAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter 和 Setter 方法
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

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(String lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", registrationId='" + registrationId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", isActive=" + isActive +
                ", lastActiveAt='" + lastActiveAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
