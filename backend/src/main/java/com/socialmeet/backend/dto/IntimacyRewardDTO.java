package com.socialmeet.backend.dto;

import lombok.Data;

/**
 * 亲密度奖励DTO
 */
@Data
public class IntimacyRewardDTO {

    /**
     * 奖励ID
     */
    private Long id;

    /**
     * 目标用户ID
     */
    private Long targetUserId;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 奖励类型
     */
    private String rewardType;

    /**
     * 奖励内容JSON
     */
    private String rewardValue;

    /**
     * 是否已领取
     */
    private Boolean isClaimed;

    /**
     * 领取时间
     */
    private String claimedAt;
}
