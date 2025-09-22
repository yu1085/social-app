package com.example.socialmeet.dto;

import com.example.socialmeet.entity.UserRelationshipEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户关系DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class UserRelationshipDTO {
    
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private UserRelationshipEntity.RelationshipType relationshipType;
    private Boolean isMutual;
    private Long initiatedBy;
    private String status;
    private Integer intimacyScore;
    private Integer chatFrequency;
    private LocalDateTime lastChatTime;
    private LocalDateTime lastInteractionTime;
    private Integer interactionCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer callCount;
    private Integer callDuration;
    private Integer giftCount;
    private Integer giftValue;
    private String notes;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private String user1Nickname;
    private String user1Avatar;
    private String user2Nickname;
    private String user2Avatar;
    private String relationshipDescription;
    private String intimacyLevel;
}
