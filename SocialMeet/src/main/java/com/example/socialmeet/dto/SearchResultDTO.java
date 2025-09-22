package com.example.socialmeet.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 搜索结果DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class SearchResultDTO {
    
    private String keyword;
    private LocalDateTime searchTime;
    private List<MessageDTO> messages;
    private List<ConversationDTO> conversations;
    private List<UserDTO> users;
    private Long messageTotal = 0L;
    private Long conversationTotal = 0L;
    private Long userTotal = 0L;
    private Long totalResults = 0L;
}
