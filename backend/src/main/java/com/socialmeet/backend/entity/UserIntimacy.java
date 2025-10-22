package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户亲密度记录实体
 */
@Entity
@Table(name = "user_intimacy",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "target_user_id"}),
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_target_user_id", columnList = "target_user_id"),
           @Index(name = "idx_current_level", columnList = "current_level"),
           @Index(name = "idx_current_temperature", columnList = "current_temperature")
       })
@Data
public class UserIntimacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 目标用户ID
     */
    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    /**
     * 当前温度(亲密度值)
     */
    @Column(name = "current_temperature", nullable = false)
    private Integer currentTemperature = 0;

    /**
     * 当前等级
     */
    @Column(name = "current_level", nullable = false)
    private Integer currentLevel = 1;

    // ========== 统计数据 ==========

    /**
     * 文字消息总数
     */
    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0;

    /**
     * 赠送礼物数量
     */
    @Column(name = "gift_count", nullable = false)
    private Integer giftCount = 0;

    /**
     * 视频通话分钟数
     */
    @Column(name = "video_call_minutes", nullable = false)
    private Integer videoCallMinutes = 0;

    /**
     * 语音通话分钟数
     */
    @Column(name = "voice_call_minutes", nullable = false)
    private Integer voiceCallMinutes = 0;

    /**
     * 累计消耗聊币
     */
    @Column(name = "total_coins_spent", nullable = false)
    private Long totalCoinsSpent = 0L;

    // ========== 时间记录 ==========

    /**
     * 首次互动日期
     */
    @Column(name = "first_interaction_date")
    private LocalDate firstInteractionDate;

    /**
     * 最后互动日期
     */
    @Column(name = "last_interaction_date")
    private LocalDate lastInteractionDate;

    /**
     * 相识天数
     */
    @Column(name = "days_known", nullable = false)
    private Integer daysKnown = 0;

    @Column(name = "created_at", updatable = false)
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
}
