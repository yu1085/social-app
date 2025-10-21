package com.socialmeet.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 通话记录DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRecordDTO {
    private Long id;
    private String sessionId;
    private Long userId;              // 对方用户ID
    private String nickname;          // 对方昵称
    private String avatar;            // 对方头像
    private String callType;          // 通话类型: VOICE/VIDEO
    private String callStatus;        // 通话状态: RINGING/CONNECTED/ENDED/CANCELLED/MISSED
    private Integer duration;         // 通话时长(秒)
    private BigDecimal pricePerMin;   // 每分钟价格
    private BigDecimal totalCost;     // 总费用
    private LocalDateTime callTime;   // 通话时间
    private Boolean isMissed;         // 是否未接
    private String callStatusText;    // 通话状态文本
}
