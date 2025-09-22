package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.MessageDTO;
import com.example.socialmeet.dto.SendMessageRequest;
import com.example.socialmeet.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(originPatterns = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<MessageDTO> response = messageService.sendMessage(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取消息列表
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getMessages(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Page<MessageDTO>> response = messageService.getMessages(otherUserId, page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取会话消息
     */
    @GetMapping("/conversation/detail/{conversationId}")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Page<MessageDTO>> response = messageService.getConversationMessages(conversationId, page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{otherUserId}")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(
            @PathVariable Long otherUserId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = messageService.markMessagesAsRead(otherUserId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/recall/{messageId}")
    public ResponseEntity<ApiResponse<String>> recallMessage(
            @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = messageService.recallMessage(messageId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(
            @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = messageService.deleteMessage(messageId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 搜索消息
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> searchMessages(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Page<MessageDTO>> response = messageService.searchMessages(keyword, page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Long> response = messageService.getUnreadCount(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}