package com.socialmeet.backend.dto;

import lombok.Data;

/**
 * 亲密度行为记录请求
 */
@Data
public class IntimacyActionRequest {

    /**
     * 目标用户ID
     */
    private Long targetUserId;

    /**
     * 行为类型: MESSAGE/GIFT/VIDEO_CALL/VOICE_CALL
     */
    private String actionType;

    /**
     * 消耗的聊币数
     */
    private Integer coinsSpent;

    /**
     * 行为数量（例如：消息条数、通话分钟数等）
     */
    private Integer actionCount;
}
