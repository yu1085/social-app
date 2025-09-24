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
            @RequestHeader("Authorization") String authHeader) {
        
        System.out.println("=== 消息发送接口被调用 ===");
        System.out.println("Authorization Header: " + authHeader);
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        System.out.println("提取的JWT: " + token);
        System.out.println("JWT长度: " + token.length());
        System.out.println("JWT包含点号数量: " + (token.split("\\.").length - 1));
        
        System.out.println("发送消息请求参数:");
        System.out.println("  接收者ID: " + request.getReceiverId());
        System.out.println("  消息内容: " + request.getContent());
        System.out.println("  消息类型: " + request.getMessageType());
        System.out.println("  媒体URL: " + request.getMediaUrl());
        
        System.out.println("开始调用消息服务...");
        ApiResponse<MessageDTO> response = messageService.sendMessage(request, token);
        
        System.out.println("消息服务返回结果:");
        System.out.println("  成功状态: " + response.isSuccess());
        System.out.println("  错误信息: " + response.getMessage());
        
        if (response.isSuccess()) {
            System.out.println("消息发送成功，返回200状态码");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("消息发送失败，返回400状态码");
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
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
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Long> response = messageService.getUnreadCount(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}