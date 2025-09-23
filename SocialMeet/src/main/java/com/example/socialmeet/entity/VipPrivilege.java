package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * VIP特权实体类
 */
@Entity
@Table(name = "vip_privileges")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class VipPrivilege {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vip_level_id", nullable = false)
    private Long vipLevelId;
    
    @Column(name = "privilege_type", length = 50, nullable = false)
    private String privilegeType;
    
    @Column(name = "privilege_name", length = 100, nullable = false)
    private String privilegeName;
    
    @Column(name = "privilege_description", columnDefinition = "TEXT")
    private String privilegeDescription;
    
    @Column(name = "privilege_value", length = 200)
    private String privilegeValue; // 特权值 (如折扣比例、次数限制等)
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 特权类型枚举
    public enum PrivilegeType {
        DISCOUNT("折扣特权", "购买商品享受折扣"),
        FREE_GIFTS("免费礼物", "每日免费礼物次数"),
        PRIORITY_MATCH("优先匹配", "优先匹配其他用户"),
        EXCLUSIVE_AVATAR("专属头像", "VIP专属头像框"),
        EXCLUSIVE_BADGE("专属徽章", "VIP专属徽章"),
        AD_FREE("免广告", "免除广告干扰"),
        CUSTOMER_SERVICE("专属客服", "VIP专属客服"),
        EARLY_ACCESS("提前体验", "提前体验新功能"),
        BONUS_COINS("额外金币", "每日额外金币奖励"),
        EXTENDED_PROFILE("扩展资料", "更多个人资料选项"),
        UNLIMITED_MESSAGES("无限消息", "无限制发送消息"),
        VIDEO_CALL_PRIORITY("视频通话优先", "视频通话优先权"),
        GIFT_LIMIT_INCREASE("礼物限额提升", "提高礼物发送限额"),
        PROFILE_VERIFICATION("资料认证", "优先资料认证"),
        EXCLUSIVE_EVENTS("专属活动", "参与VIP专属活动");
        
        private final String displayName;
        private final String description;
        
        PrivilegeType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public VipPrivilege() {}
    
    public VipPrivilege(Long vipLevelId, String privilegeType, String privilegeName, 
                       String privilegeDescription, String privilegeValue) {
        this.vipLevelId = vipLevelId;
        this.privilegeType = privilegeType;
        this.privilegeName = privilegeName;
        this.privilegeDescription = privilegeDescription;
        this.privilegeValue = privilegeValue;
    }
}
