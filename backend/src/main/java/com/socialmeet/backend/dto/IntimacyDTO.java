package com.socialmeet.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * 亲密度信息DTO
 */
@Data
public class IntimacyDTO {

    /**
     * 目标用户ID
     */
    private Long targetUserId;

    /**
     * 目标用户信息
     */
    private UserDTO targetUser;

    /**
     * 当前温度(亲密度值)
     */
    private Integer currentTemperature;

    /**
     * 当前等级
     */
    private Integer currentLevel;

    /**
     * 当前等级名称
     */
    private String currentLevelName;

    /**
     * 下一等级所需温度
     */
    private Integer nextLevelTemperature;

    /**
     * 下一等级名称
     */
    private String nextLevelName;

    /**
     * 是否已达到最高等级
     */
    private Boolean maxLevel;

    // ========== 统计数据 ==========

    /**
     * 文字消息总数
     */
    private Integer messageCount;

    /**
     * 赠送礼物数量
     */
    private Integer giftCount;

    /**
     * 视频通话分钟数
     */
    private Integer videoCallMinutes;

    /**
     * 语音通话分钟数
     */
    private Integer voiceCallMinutes;

    /**
     * 累计消耗聊币
     */
    private Long totalCoinsSpent;

    // ========== 时间记录 ==========

    /**
     * 首次互动日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate firstInteractionDate;

    /**
     * 最后互动日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastInteractionDate;

    /**
     * 相识天数
     */
    private Integer daysKnown;
}
