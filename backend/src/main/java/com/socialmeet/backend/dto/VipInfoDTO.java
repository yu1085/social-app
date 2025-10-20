package com.socialmeet.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VIP信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VipInfoDTO {

    private Boolean isVip;
    private Integer vipLevel;
    private String vipLevelName;
    
    private LocalDateTime vipExpireAt;
    
    private Long remainingDays;
    private String vipBenefits;
    private Boolean canUpgrade;
    private String nextLevelName;
    private Integer nextLevelRequirement;
    
    // 财富值相关字段
    private Integer wealthValue;
    private String wealthLevelName;
    private Integer wealthLevelId;
    private Integer wealthThresholdForBenefits;
    private String currentLevelStatus;
    private List<LevelStatusDTO> levelProgression;
    private List<VipBenefitDTO> currentLevelBenefits;
}
