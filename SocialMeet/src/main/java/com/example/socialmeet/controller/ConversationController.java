package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.ConversationDTO;
import com.example.socialmeet.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 会话控制器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(originPatterns = "*")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * 获取会话列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ConversationDTO>>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<ConversationDTO>> response = conversationService.getConversations(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取置顶会话
     */
    @GetMapping("/pinned")
    public ResponseEntity<ApiResponse<Page<ConversationDTO>>> getPinnedConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<ConversationDTO>> response = conversationService.getPinnedConversations(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取有未读消息的会话
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<ConversationDTO>>> getUnreadConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<ConversationDTO>> response = conversationService.getUnreadConversations(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 搜索会话
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ConversationDTO>>> searchConversations(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<ConversationDTO>> response = conversationService.searchConversations(keyword, page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 设置会话置顶状态
     */
    @PostMapping("/{conversationId}/pin")
    public ResponseEntity<ApiResponse<String>> setPinnedStatus(
            @PathVariable Long conversationId,
            @RequestParam Boolean pinned,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = conversationService.setPinnedStatus(conversationId, pinned, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 设置会话静音状态
     */
    @PostMapping("/{conversationId}/mute")
    public ResponseEntity<ApiResponse<String>> setMutedStatus(
            @PathVariable Long conversationId,
            @RequestParam Boolean muted,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = conversationService.setMutedStatus(conversationId, muted, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除会话
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<String>> deleteConversation(
            @PathVariable Long conversationId,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = conversationService.deleteConversation(conversationId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取总未读消息数
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getTotalUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<Long> response = conversationService.getTotalUnreadCount(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
