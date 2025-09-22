package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 通话记录实体类 - 管理用户通话历史
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "call_records")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CallRecordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "caller_id", nullable = false)
    private Long callerId;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false)
    private CallType callType = CallType.VOICE;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration", nullable = false)
    private Integer duration = 0; // 通话时长（秒）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "call_status", nullable = false)
    private CallStatus callStatus = CallStatus.INITIATED;
    
    @Column(name = "is_missed", nullable = false)
    private Boolean isMissed = false;
    
    @Column(name = "is_answered", nullable = false)
    private Boolean isAnswered = false;
    
    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected = false;
    
    @Column(name = "call_price", nullable = false)
    private Integer callPrice = 0; // 通话单价（分/分钟）
    
    @Column(name = "total_cost", nullable = false)
    private Integer totalCost = 0; // 总费用（分）
    
    @Column(name = "quality_score")
    private Double qualityScore; // 通话质量评分（1-5分）
    
    @Column(name = "network_quality", length = 20)
    private String networkQuality; // 网络质量：EXCELLENT, GOOD, FAIR, POOR
    
    @Column(name = "caller_device_type", length = 50)
    private String callerDeviceType; // 主叫设备类型
    
    @Column(name = "receiver_device_type", length = 50)
    private String receiverDeviceType; // 被叫设备类型
    
    @Column(name = "caller_location", length = 100)
    private String callerLocation; // 主叫位置
    
    @Column(name = "receiver_location", length = 100)
    private String receiverLocation; // 被叫位置
    
    @Column(name = "call_notes", columnDefinition = "TEXT")
    private String callNotes; // 通话备注
    
    @Column(name = "is_recorded", nullable = false)
    private Boolean isRecorded = false; // 是否录音
    
    @Column(name = "recording_url", length = 500)
    private String recordingUrl; // 录音文件URL
    
    @Column(name = "recording_duration")
    private Integer recordingDuration; // 录音时长（秒）
    
    @Column(name = "is_deleted_caller", nullable = false)
    private Boolean isDeletedCaller = false; // 主叫是否删除记录
    
    @Column(name = "is_deleted_receiver", nullable = false)
    private Boolean isDeletedReceiver = false; // 被叫是否删除记录
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User caller;
    
    @Transient
    private User receiver;
    
    // 通话类型枚举
    public enum CallType {
        VOICE("语音通话"),
        VIDEO("视频通话"),
        GROUP_VOICE("群语音通话"),
        GROUP_VIDEO("群视频通话");
        
        private final String description;
        
        CallType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 通话状态枚举
    public enum CallStatus {
        INITIATED("已发起"),
        RINGING("响铃中"),
        ANSWERED("已接通"),
        REJECTED("已拒绝"),
        MISSED("未接听"),
        CANCELLED("已取消"),
        ENDED("已结束"),
        FAILED("通话失败");
        
        private final String description;
        
        CallStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 计算通话费用
     */
    public void calculateCost() {
        if (duration > 0 && callPrice > 0) {
            // 按分钟计费，不足1分钟按1分钟计算
            int minutes = (duration + 59) / 60; // 向上取整
            this.totalCost = minutes * callPrice;
        }
    }
    
    /**
     * 结束通话
     */
    public void endCall() {
        this.endTime = LocalDateTime.now();
        if (this.startTime != null) {
            this.duration = (int) java.time.Duration.between(this.startTime, this.endTime).getSeconds();
        }
        this.callStatus = CallStatus.ENDED;
        calculateCost();
    }
    
    /**
     * 标记为未接听
     */
    public void markAsMissed() {
        this.callStatus = CallStatus.MISSED;
        this.isMissed = true;
        this.isAnswered = false;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * 标记为已拒绝
     */
    public void markAsRejected() {
        this.callStatus = CallStatus.REJECTED;
        this.isRejected = true;
        this.isAnswered = false;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * 标记为已接通
     */
    public void markAsAnswered() {
        this.callStatus = CallStatus.ANSWERED;
        this.isAnswered = true;
        this.isMissed = false;
        this.isRejected = false;
    }
    
    /**
     * 获取通话时长显示文本
     */
    public String getDurationText() {
        if (duration == 0) {
            return "0秒";
        }
        
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分钟");
        }
        if (seconds > 0 || sb.length() == 0) {
            sb.append(seconds).append("秒");
        }
        
        return sb.toString();
    }
    
    /**
     * 获取费用显示文本（元）
     */
    public String getCostText() {
        if (totalCost == 0) {
            return "免费";
        }
        return String.format("%.2f元", totalCost / 100.0);
    }
}
