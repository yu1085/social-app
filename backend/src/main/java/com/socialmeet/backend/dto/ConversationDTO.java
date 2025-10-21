package com.socialmeet.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话列表DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long userId;              // 对方用户ID
    private String nickname;          // 对方昵称
    private String avatar;            // 对方头像
    private String lastMessage;       // 最后一条消息
    private LocalDateTime lastMessageTime;  // 最后消息时间
    private Long unreadCount;         // 未读数量
    private Boolean isOnline;         // 是否在线
    private Double currentPrice;      // 当前价格(通话/分钟)
}
