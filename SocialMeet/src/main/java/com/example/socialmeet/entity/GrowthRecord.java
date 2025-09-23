package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 成长值记录实体类
 */
@Entity
@Table(name = "growth_records")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class GrowthRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "action_type", length = 50, nullable = false)
    private String actionType;
    
    @Column(name = "points", nullable = false)
    private Integer points;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "related_id")
    private Long relatedId; // 关联的业务ID
    
    @Column(name = "related_type", length = 50)
    private String relatedType; // 关联的业务类型
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 成长值获取类型枚举
    public enum ActionType {
        DAILY_LOGIN("每日登录", "每日登录获得成长值", 10),
        SEND_MESSAGE("发送消息", "发送消息获得成长值", 1),
        RECEIVE_MESSAGE("接收消息", "接收消息获得成长值", 1),
        SEND_GIFT("发送礼物", "发送礼物获得成长值", 5),
        RECEIVE_GIFT("接收礼物", "接收礼物获得成长值", 3),
        COMPLETE_PROFILE("完善资料", "完善个人资料获得成长值", 20),
        UPLOAD_PHOTO("上传照片", "上传照片获得成长值", 5),
        VERIFY_IDENTITY("身份认证", "完成身份认证获得成长值", 50),
        INVITE_FRIEND("邀请好友", "邀请好友注册获得成长值", 30),
        FIRST_RECHARGE("首次充值", "首次充值获得成长值", 100),
        VIP_SUBSCRIBE("VIP订阅", "订阅VIP获得成长值", 200),
        DAILY_TASK("每日任务", "完成每日任务获得成长值", 15),
        WEEKLY_TASK("每周任务", "完成每周任务获得成长值", 50),
        MONTHLY_TASK("每月任务", "完成每月任务获得成长值", 200),
        SPECIAL_EVENT("特殊活动", "参与特殊活动获得成长值", 100);
        
        private final String displayName;
        private final String description;
        private final Integer defaultPoints;
        
        ActionType(String displayName, String description, Integer defaultPoints) {
            this.displayName = displayName;
            this.description = description;
            this.defaultPoints = defaultPoints;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Integer getDefaultPoints() {
            return defaultPoints;
        }
    }
    
    // 构造函数
    public GrowthRecord() {}
    
    public GrowthRecord(Long userId, String actionType, Integer points, String description) {
        this.userId = userId;
        this.actionType = actionType;
        this.points = points;
        this.description = description;
    }
    
    public GrowthRecord(Long userId, String actionType, Integer points, String description, 
                       Long relatedId, String relatedType) {
        this.userId = userId;
        this.actionType = actionType;
        this.points = points;
        this.description = description;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
    }
}
