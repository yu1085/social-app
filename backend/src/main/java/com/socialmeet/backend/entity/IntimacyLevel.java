package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 亲密度等级配置实体
 */
@Entity
@Table(name = "intimacy_levels")
@Data
public class IntimacyLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 等级 (1-6)
     */
    @Column(nullable = false, unique = true)
    private Integer level;

    /**
     * 等级名称: 相遇/相识/相知/相恋/相伴/相守
     */
    @Column(name = "level_name", nullable = false, length = 50)
    private String levelName;

    /**
     * 所需温度(亲密度值)
     */
    @Column(name = "required_temperature", nullable = false)
    private Integer requiredTemperature;

    /**
     * 奖励类型: MESSAGE_COUPON/CALL_COUPON/DAILY_CALL_COUPON/FREE_MESSAGE/VIP_MEMBERSHIP
     */
    @Column(name = "reward_type", length = 50)
    private String rewardType;

    /**
     * 奖励内容JSON
     */
    @Column(name = "reward_value", columnDefinition = "TEXT")
    private String rewardValue;

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
