package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户在线状态实体类 - 管理用户在线状态和设备信息
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "user_online_status")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserOnlineStatusEntity {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OnlineStatus status = OnlineStatus.OFFLINE;
    
    @Column(name = "last_seen", nullable = false)
    private LocalDateTime lastSeen;
    
    @Column(name = "device_type", length = 50)
    private String deviceType; // 设备类型：iOS, Android, Web
    
    @Column(name = "device_id", length = 100)
    private String deviceId; // 设备唯一标识
    
    @Column(name = "app_version", length = 20)
    private String appVersion; // 应用版本
    
    @Column(name = "os_version", length = 20)
    private String osVersion; // 操作系统版本
    
    @Column(name = "network_type", length = 20)
    private String networkType; // 网络类型：WiFi, 4G, 5G
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress; // IP地址
    
    @Column(name = "location", length = 100)
    private String location; // 位置信息
    
    @Column(name = "latitude")
    private Double latitude; // 纬度
    
    @Column(name = "longitude")
    private Double longitude; // 经度
    
    @Column(name = "battery_level")
    private Integer batteryLevel; // 电池电量百分比
    
    @Column(name = "is_charging")
    private Boolean isCharging = false; // 是否正在充电
    
    @Column(name = "screen_on")
    private Boolean screenOn = false; // 屏幕是否亮着
    
    @Column(name = "in_call")
    private Boolean inCall = false; // 是否在通话中
    
    @Column(name = "in_video_call")
    private Boolean inVideoCall = false; // 是否在视频通话中
    
    @Column(name = "do_not_disturb")
    private Boolean doNotDisturb = false; // 是否开启勿扰模式
    
    @Column(name = "quiet_hours_start")
    private String quietHoursStart; // 勿扰时间开始（HH:mm格式）
    
    @Column(name = "quiet_hours_end")
    private String quietHoursEnd; // 勿扰时间结束（HH:mm格式）
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user;
    
    // 在线状态枚举
    public enum OnlineStatus {
        ONLINE("在线"),
        AWAY("离开"),
        BUSY("忙碌"),
        INVISIBLE("隐身"),
        OFFLINE("离线");
        
        private final String description;
        
        OnlineStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 更新在线状态
     */
    public void updateOnlineStatus(OnlineStatus newStatus) {
        this.status = newStatus;
        this.isOnline = newStatus != OnlineStatus.OFFLINE;
        this.lastSeen = LocalDateTime.now();
    }
    
    /**
     * 设置为离线状态
     */
    public void setOffline() {
        this.status = OnlineStatus.OFFLINE;
        this.isOnline = false;
        this.lastSeen = LocalDateTime.now();
        this.inCall = false;
        this.inVideoCall = false;
    }
    
    /**
     * 设置为在线状态
     */
    public void setOnline() {
        this.status = OnlineStatus.ONLINE;
        this.isOnline = true;
        this.lastSeen = LocalDateTime.now();
    }
    
    /**
     * 检查是否在勿扰时间内
     */
    public boolean isInQuietHours() {
        if (!doNotDisturb || quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();
        int currentTime = currentHour * 60 + currentMinute;
        
        try {
            String[] startParts = quietHoursStart.split(":");
            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int startTime = startHour * 60 + startMinute;
            
            String[] endParts = quietHoursEnd.split(":");
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);
            int endTime = endHour * 60 + endMinute;
            
            if (startTime <= endTime) {
                // 同一天内的时间段
                return currentTime >= startTime && currentTime <= endTime;
            } else {
                // 跨天的时间段
                return currentTime >= startTime || currentTime <= endTime;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查是否可以接收消息
     */
    public boolean canReceiveMessage() {
        return isOnline && !isInQuietHours() && !inCall && !inVideoCall;
    }
    
    /**
     * 检查是否可以接收通话
     */
    public boolean canReceiveCall() {
        return isOnline && !inCall && !inVideoCall && !isInQuietHours();
    }
}
