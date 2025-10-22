package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 亲密度变更日志实体
 */
@Entity
@Table(name = "intimacy_logs",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_target_user_id", columnList = "target_user_id"),
           @Index(name = "idx_created_at", columnList = "created_at"),
           @Index(name = "idx_action_type", columnList = "action_type")
       })
@Data
public class IntimacyLog {

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
     * 行为类型: MESSAGE/GIFT/VIDEO_CALL/VOICE_CALL
     */
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    /**
     * 温度变化值
     */
    @Column(name = "temperature_change", nullable = false)
    private Integer temperatureChange;

    /**
     * 消耗聊币数
     */
    @Column(name = "coins_spent", nullable = false)
    private Integer coinsSpent;

    /**
     * 变化前温度
     */
    @Column(name = "before_temperature", nullable = false)
    private Integer beforeTemperature;

    /**
     * 变化后温度
     */
    @Column(name = "after_temperature", nullable = false)
    private Integer afterTemperature;

    /**
     * 变化前等级
     */
    @Column(name = "before_level", nullable = false)
    private Integer beforeLevel;

    /**
     * 变化后等级
     */
    @Column(name = "after_level", nullable = false)
    private Integer afterLevel;

    /**
     * 是否升级
     */
    @Column(name = "level_up", nullable = false)
    private Boolean levelUp = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
