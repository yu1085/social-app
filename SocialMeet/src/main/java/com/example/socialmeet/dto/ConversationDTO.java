package com.example.socialmeet.dto;

import com.example.socialmeet.entity.ConversationEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class ConversationDTO {
    
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Long lastMessageId;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Integer unreadCountUser1;
    private Integer unreadCountUser2;
    private Boolean isPinnedUser1;
    private Boolean isPinnedUser2;
    private Boolean isMutedUser1;
    private Boolean isMutedUser2;
    private Boolean isDeletedUser1;
    private Boolean isDeletedUser2;
    private ConversationEntity.ConversationType conversationType;
    private String conversationName;
    private String conversationAvatar;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private String user1Nickname;
    private String user1Avatar;
    private String user2Nickname;
    private String user2Avatar;
    private Integer unreadCountForCurrentUser;
    private Boolean isPinnedForCurrentUser;
    private Boolean isMutedForCurrentUser;
}
