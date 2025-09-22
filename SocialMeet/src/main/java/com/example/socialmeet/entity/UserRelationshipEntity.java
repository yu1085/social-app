package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户关系实体类 - 管理用户之间的各种关系
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "user_relationships")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserRelationshipEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;
    
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;
    
    @Column(name = "is_mutual", nullable = false)
    private Boolean isMutual = false; // 是否相互关注/喜欢
    
    @Column(name = "initiated_by", nullable = false)
    private Long initiatedBy; // 关系发起者
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE"; // 关系状态：ACTIVE, BLOCKED, DELETED
    
    @Column(name = "intimacy_score", nullable = false)
    private Integer intimacyScore = 0; // 亲密度评分（0-100）
    
    @Column(name = "chat_frequency", nullable = false)
    private Integer chatFrequency = 0; // 聊天频次（消息数量）
    
    @Column(name = "last_chat_time")
    private LocalDateTime lastChatTime; // 最后聊天时间
    
    @Column(name = "last_interaction_time")
    private LocalDateTime lastInteractionTime; // 最后互动时间
    
    @Column(name = "interaction_count", nullable = false)
    private Integer interactionCount = 0; // 互动次数
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0; // 点赞次数
    
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0; // 评论次数
    
    @Column(name = "call_count", nullable = false)
    private Integer callCount = 0; // 通话次数
    
    @Column(name = "call_duration", nullable = false)
    private Integer callDuration = 0; // 通话总时长（秒）
    
    @Column(name = "gift_count", nullable = false)
    private Integer giftCount = 0; // 送礼次数
    
    @Column(name = "gift_value", nullable = false)
    private Integer giftValue = 0; // 送礼总价值（分）
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // 备注信息
    
    @Column(name = "tags", length = 500)
    private String tags; // 标签（逗号分隔）
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user1;
    
    @Transient
    private User user2;
    
    // 关系类型枚举
    public enum RelationshipType {
        FRIEND("知友"), // 交友知心，畅聊互动
        LIKE("喜欢"), // 你曾对ta一见钟情
        INTIMATE("亲密"), // 看看谁聊的最频繁
        FOLLOW("关注"), // 关注关系
        BLOCK("拉黑"), // 拉黑关系
        MATCH("匹配"); // 匹配关系
        
        private final String description;
        
        RelationshipType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 更新亲密度评分
     */
    public void updateIntimacyScore() {
        int score = 0;
        
        // 聊天频次权重：30%
        score += Math.min(chatFrequency * 2, 30);
        
        // 互动次数权重：25%
        score += Math.min(interactionCount, 25);
        
        // 通话时长权重：20%
        score += Math.min(callDuration / 60, 20); // 每分钟通话增加1分
        
        // 送礼价值权重：15%
        score += Math.min(giftValue / 100, 15); // 每1元礼物增加1分
        
        // 关系时长权重：10%
        long daysSinceCreated = java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
        score += Math.min((int) daysSinceCreated, 10);
        
        this.intimacyScore = Math.min(score, 100);
    }
    
    /**
     * 增加聊天频次
     */
    public void incrementChatFrequency() {
        this.chatFrequency++;
        this.lastChatTime = LocalDateTime.now();
        updateIntimacyScore();
    }
    
    /**
     * 增加互动次数
     */
    public void incrementInteraction() {
        this.interactionCount++;
        this.lastInteractionTime = LocalDateTime.now();
        updateIntimacyScore();
    }
    
    /**
     * 增加点赞次数
     */
    public void incrementLike() {
        this.likeCount++;
        incrementInteraction();
    }
    
    /**
     * 增加评论次数
     */
    public void incrementComment() {
        this.commentCount++;
        incrementInteraction();
    }
    
    /**
     * 增加通话记录
     */
    public void addCallRecord(int duration) {
        this.callCount++;
        this.callDuration += duration;
        incrementInteraction();
    }
    
    /**
     * 增加送礼记录
     */
    public void addGift(int value) {
        this.giftCount++;
        this.giftValue += value;
        incrementInteraction();
    }
    
    /**
     * 获取关系描述
     */
    public String getRelationshipDescription() {
        switch (relationshipType) {
            case FRIEND:
                return "知友 - 交友知心，畅聊互动";
            case LIKE:
                return "喜欢 - 你曾对ta一见钟情";
            case INTIMATE:
                return "亲密 - 看看谁聊的最频繁";
            case FOLLOW:
                return "关注";
            case BLOCK:
                return "已拉黑";
            case MATCH:
                return "匹配";
            default:
                return "未知关系";
        }
    }
    
    /**
     * 获取亲密度等级
     */
    public String getIntimacyLevel() {
        if (intimacyScore >= 90) {
            return "超级亲密";
        } else if (intimacyScore >= 70) {
            return "非常亲密";
        } else if (intimacyScore >= 50) {
            return "比较亲密";
        } else if (intimacyScore >= 30) {
            return "一般亲密";
        } else if (intimacyScore >= 10) {
            return "有点亲密";
        } else {
            return "不太熟悉";
        }
    }
}
