package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.MessageDTO;
import com.socialmeet.backend.entity.Message;
import com.socialmeet.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MessageController {
    
    private final MessageService messageService;
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ApiResponse<MessageDTO> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(defaultValue = "TEXT") Message.MessageType messageType) {
        
        try {
            log.info("收到发送消息请求 - senderId: {}, receiverId: {}, content: {}", 
                    senderId, receiverId, content);
            
            MessageDTO message = messageService.sendMessage(senderId, receiverId, content, messageType);
            return ApiResponse.success(message);
            
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取聊天记录
     */
    @GetMapping("/history")
    public ApiResponse<List<MessageDTO>> getChatHistory(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        
        try {
            log.info("获取聊天记录 - userId1: {}, userId2: {}", userId1, userId2);
            
            List<MessageDTO> messages = messageService.getChatHistory(userId1, userId2);
            return ApiResponse.success(messages);
            
        } catch (Exception e) {
            log.error("获取聊天记录失败", e);
            return ApiResponse.error("获取聊天记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/mark-read")
    public ApiResponse<String> markMessagesAsRead(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        
        try {
            log.info("标记消息为已读 - userId: {}, otherUserId: {}", userId, otherUserId);
            
            messageService.markMessagesAsRead(userId, otherUserId);
            return ApiResponse.success("消息已标记为已读");
            
        } catch (Exception e) {
            log.error("标记消息为已读失败", e);
            return ApiResponse.error("标记消息为已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadMessageCount(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        
        try {
            Long count = messageService.getUnreadMessageCount(userId, otherUserId);
            return ApiResponse.success(count);
            
        } catch (Exception e) {
            log.error("获取未读消息数量失败", e);
            return ApiResponse.error("获取未读消息数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有未读消息
     */
    @GetMapping("/unread")
    public ApiResponse<List<MessageDTO>> getUnreadMessages(@RequestParam Long userId) {
        
        try {
            List<MessageDTO> messages = messageService.getUnreadMessages(userId);
            return ApiResponse.success(messages);
            
        } catch (Exception e) {
            log.error("获取未读消息失败", e);
            return ApiResponse.error("获取未读消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试JPush推送
     */
    @PostMapping("/test-push")
    public ApiResponse<String> testPush(@RequestParam Long userId, @RequestParam String registrationId) {
        
        try {
            log.info("收到测试推送请求 - userId: {}, registrationId: {}", userId, registrationId);
            
            // 这里需要注入JPushService，暂时返回提示
            return ApiResponse.success("测试推送功能已添加，请检查后端日志");
            
        } catch (Exception e) {
            log.error("测试推送失败", e);
            return ApiResponse.error("测试推送失败: " + e.getMessage());
        }
    }
}
