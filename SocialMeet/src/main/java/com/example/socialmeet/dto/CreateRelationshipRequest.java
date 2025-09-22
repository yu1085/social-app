package com.example.socialmeet.dto;

import com.example.socialmeet.entity.UserRelationshipEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建关系请求DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class CreateRelationshipRequest {
    
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
    
    @NotNull(message = "关系类型不能为空")
    private UserRelationshipEntity.RelationshipType relationshipType;
    
    private String notes;
    private String tags;
}
