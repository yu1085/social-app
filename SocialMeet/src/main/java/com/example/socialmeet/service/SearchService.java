package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.MessageDTO;
import com.example.socialmeet.dto.ConversationDTO;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.dto.SearchRequest;
import com.example.socialmeet.dto.SearchResultDTO;
import com.example.socialmeet.entity.MessageEntity;
import com.example.socialmeet.entity.ConversationEntity;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.MessageRepository;
import com.example.socialmeet.repository.ConversationRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 搜索服务层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class SearchService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 综合搜索
     */
    public ApiResponse<SearchResultDTO> search(SearchRequest request, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            SearchResultDTO result = new SearchResultDTO();
            result.setKeyword(request.getKeyword());
            result.setSearchTime(LocalDateTime.now());
            
            // 搜索消息
            if (request.getSearchTypes().contains("message")) {
                Pageable messagePageable = PageRequest.of(0, request.getPageSize());
                Page<MessageEntity> messages = messageRepository.searchMessagesByContent(userId, request.getKeyword(), messagePageable);
                List<MessageDTO> messageDTOs = messages.getContent().stream()
                        .map(this::convertMessageToDTO)
                        .collect(Collectors.toList());
                result.setMessages(messageDTOs);
                result.setMessageTotal(messages.getTotalElements());
            }
            
            // 搜索会话
            if (request.getSearchTypes().contains("conversation")) {
                Pageable conversationPageable = PageRequest.of(0, request.getPageSize());
                Page<ConversationEntity> conversations = conversationRepository.searchConversations(userId, request.getKeyword(), conversationPageable);
                List<ConversationDTO> conversationDTOs = conversations.getContent().stream()
                        .map(this::convertConversationToDTO)
                        .collect(Collectors.toList());
                result.setConversations(conversationDTOs);
                result.setConversationTotal(conversations.getTotalElements());
            }
            
            // 搜索用户
            if (request.getSearchTypes().contains("user")) {
                List<User> users = userRepository.findByNicknameContaining(request.getKeyword());
                List<UserDTO> userDTOs = users.stream()
                        .map(this::convertUserToDTO)
                        .collect(Collectors.toList());
                result.setUsers(userDTOs);
                result.setUserTotal((long) users.size());
            }
            
            return ApiResponse.success(result);
            
        } catch (Exception e) {
            return ApiResponse.error("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 高级搜索
     */
    public ApiResponse<SearchResultDTO> advancedSearch(SearchRequest request, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            SearchResultDTO result = new SearchResultDTO();
            result.setKeyword(request.getKeyword());
            result.setSearchTime(LocalDateTime.now());
            
            // 按时间范围搜索消息
            if (request.getSearchTypes().contains("message") && request.getStartTime() != null && request.getEndTime() != null) {
                List<MessageEntity> messages = messageRepository.findMessagesByTimeRange(userId, request.getStartTime(), request.getEndTime());
                List<MessageDTO> messageDTOs = messages.stream()
                        .filter(msg -> msg.getContent().toLowerCase().contains(request.getKeyword().toLowerCase()))
                        .map(this::convertMessageToDTO)
                        .collect(Collectors.toList());
                result.setMessages(messageDTOs);
                result.setMessageTotal((long) messageDTOs.size());
            }
            
            // 按消息类型搜索
            if (request.getSearchTypes().contains("message") && request.getMessageType() != null) {
                Pageable messagePageable = PageRequest.of(0, request.getPageSize());
                Page<MessageEntity> messages = messageRepository.findByMessageType(request.getMessageType(), messagePageable);
                List<MessageDTO> messageDTOs = messages.getContent().stream()
                        .filter(msg -> msg.getContent().toLowerCase().contains(request.getKeyword().toLowerCase()))
                        .map(this::convertMessageToDTO)
                        .collect(Collectors.toList());
                result.setMessages(messageDTOs);
                result.setMessageTotal((long) messageDTOs.size());
            }
            
            return ApiResponse.success(result);
            
        } catch (Exception e) {
            return ApiResponse.error("高级搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索建议
     */
    public ApiResponse<List<String>> getSearchSuggestions(String keyword, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<String> suggestions = new ArrayList<>();
            
            // 搜索历史消息中的关键词
            Pageable pageable = PageRequest.of(0, 10);
            Page<MessageEntity> messages = messageRepository.searchMessagesByContent(userId, keyword, pageable);
            List<String> messageKeywords = messages.getContent().stream()
                    .map(MessageEntity::getContent)
                    .filter(content -> content != null && content.length() > 0)
                    .map(content -> {
                        String[] words = content.split("\\s+");
                        for (String word : words) {
                            if (word.toLowerCase().contains(keyword.toLowerCase()) && word.length() > keyword.length()) {
                                return word;
                            }
                        }
                        return null;
                    })
                    .filter(word -> word != null)
                    .distinct()
                    .collect(Collectors.toList());
            suggestions.addAll(messageKeywords);
            
            // 搜索用户昵称
            List<User> users = userRepository.findByNicknameContaining(keyword);
            List<String> userSuggestions = users.stream()
                    .map(User::getNickname)
                    .filter(nickname -> nickname != null && nickname.toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            suggestions.addAll(userSuggestions);
            
            return ApiResponse.success(suggestions);
            
        } catch (Exception e) {
            return ApiResponse.error("获取搜索建议失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索历史记录
     */
    public ApiResponse<List<String>> getSearchHistory(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 这里可以从Redis或数据库中获取用户的搜索历史
            // 暂时返回空列表
            List<String> searchHistory = new ArrayList<>();
            
            return ApiResponse.success(searchHistory);
            
        } catch (Exception e) {
            return ApiResponse.error("获取搜索历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存搜索历史
     */
    public ApiResponse<String> saveSearchHistory(String keyword, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 这里可以将搜索关键词保存到Redis或数据库中
            // 暂时返回成功
            
            return ApiResponse.success("搜索历史已保存");
            
        } catch (Exception e) {
            return ApiResponse.error("保存搜索历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换为消息DTO
     */
    private MessageDTO convertMessageToDTO(MessageEntity message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setMediaThumbnail(message.getMediaThumbnail());
        dto.setMediaDuration(message.getMediaDuration());
        dto.setMediaSize(message.getMediaSize());
        dto.setMessageStatus(message.getMessageStatus());
        dto.setIsRead(message.getIsRead());
        dto.setIsDeleted(message.getIsDeleted());
        dto.setIsRecalled(message.getIsRecalled());
        dto.setSendTime(message.getSendTime());
        dto.setReadTime(message.getReadTime());
        dto.setRecallTime(message.getRecallTime());
        dto.setReplyToMessageId(message.getReplyToMessageId());
        dto.setForwardFromMessageId(message.getForwardFromMessageId());
        dto.setConversationId(message.getConversationId());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 转换为会话DTO
     */
    private ConversationDTO convertConversationToDTO(ConversationEntity conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setUser1Id(conversation.getUser1Id());
        dto.setUser2Id(conversation.getUser2Id());
        dto.setLastMessageId(conversation.getLastMessageId());
        dto.setLastMessageContent(conversation.getLastMessageContent());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        dto.setUnreadCountUser1(conversation.getUnreadCountUser1());
        dto.setUnreadCountUser2(conversation.getUnreadCountUser2());
        dto.setIsPinnedUser1(conversation.getIsPinnedUser1());
        dto.setIsPinnedUser2(conversation.getIsPinnedUser2());
        dto.setIsMutedUser1(conversation.getIsMutedUser1());
        dto.setIsMutedUser2(conversation.getIsMutedUser2());
        dto.setIsDeletedUser1(conversation.getIsDeletedUser1());
        dto.setIsDeletedUser2(conversation.getIsDeletedUser2());
        dto.setConversationType(conversation.getConversationType());
        dto.setConversationName(conversation.getConversationName());
        dto.setConversationAvatar(conversation.getConversationAvatar());
        dto.setIsActive(conversation.getIsActive());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * 转换为用户DTO
     */
    private UserDTO convertUserToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setGender(user.getGender());
        dto.setBirthDate(user.getBirthDate());
        dto.setBio(user.getBio());
        dto.setLocation(user.getLocation());
        dto.setLatitude(user.getLatitude());
        dto.setLongitude(user.getLongitude());
        dto.setAge(user.getAge());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setEducation(user.getEducation());
        dto.setIncome(user.getIncome());
        dto.setIsOnline(user.getIsOnline());
        dto.setLastSeen(user.getLastSeen());
        dto.setStatus(user.getStatus());
        dto.setIsVerified(user.getIsVerified());
        dto.setIsActive(user.getIsActive());
        dto.setCallPrice(user.getCallPrice());
        dto.setMessagePrice(user.getMessagePrice());
        dto.setVideoCallEnabled(user.getVideoCallEnabled());
        dto.setVoiceCallEnabled(user.getVoiceCallEnabled());
        dto.setMessageChargeEnabled(user.getMessageChargeEnabled());
        dto.setBeautyScore(user.getBeautyScore());
        dto.setReviewScore(user.getReviewScore());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setLikeCount(user.getLikeCount());
        dto.setCity(user.getCity());
        dto.setHometown(user.getHometown());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
}
