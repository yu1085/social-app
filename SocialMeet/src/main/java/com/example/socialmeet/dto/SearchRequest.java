package com.example.socialmeet.dto;

import com.example.socialmeet.entity.MessageEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索请求DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class SearchRequest {
    
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;
    
    private List<String> searchTypes = List.of("message", "conversation", "user");
    private MessageEntity.MessageType messageType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer pageSize = 20;
    private String sortBy = "time";
    private String sortOrder = "desc";
}
