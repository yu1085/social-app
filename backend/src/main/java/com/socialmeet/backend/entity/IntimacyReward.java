package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 亲密度奖励发放记录实体
 */
@Entity
@Table(name = "intimacy_rewards",
       indexes = {
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_is_claimed", columnList = "is_claimed"),
           @Index(name = "idx_level", columnList = "level")
       })
@Data
public class IntimacyReward {

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
     * 等级
     */
    @Column(nullable = false)
    private Integer level;

    /**
     * 奖励类型
     */
    @Column(name = "reward_type", nullable = false, length = 50)
    private String rewardType;

    /**
     * 奖励内容JSON
     */
    @Column(name = "reward_value", columnDefinition = "TEXT")
    private String rewardValue;

    /**
     * 是否已领取
     */
    @Column(name = "is_claimed", nullable = false)
    private Boolean isClaimed = false;

    /**
     * 领取时间
     */
    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
