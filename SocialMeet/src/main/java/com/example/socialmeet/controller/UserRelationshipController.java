package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserRelationshipDTO;
import com.example.socialmeet.dto.CreateRelationshipRequest;
import com.example.socialmeet.entity.UserRelationshipEntity;
import com.example.socialmeet.service.UserRelationshipService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关系控制器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/relationships")
@CrossOrigin(originPatterns = "*")
public class UserRelationshipController {
    
    @Autowired
    private UserRelationshipService userRelationshipService;
    
    /**
     * 创建关系
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserRelationshipDTO>> createRelationship(
            @Valid @RequestBody CreateRelationshipRequest request,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<UserRelationshipDTO> response = userRelationshipService.createRelationship(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取关系列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserRelationshipDTO>>> getRelationships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<UserRelationshipDTO>> response = userRelationshipService.getRelationships(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取知友列表
     */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<UserRelationshipDTO>>> getFriends(
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<List<UserRelationshipDTO>> response = userRelationshipService.getFriends(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取喜欢列表
     */
    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<List<UserRelationshipDTO>>> getLikes(
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<List<UserRelationshipDTO>> response = userRelationshipService.getLikes(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取亲密关系列表
     */
    @GetMapping("/intimate")
    public ResponseEntity<ApiResponse<List<UserRelationshipDTO>>> getIntimateRelationships(
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<List<UserRelationshipDTO>> response = userRelationshipService.getIntimateRelationships(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取高亲密度关系
     */
    @GetMapping("/high-intimacy")
    public ResponseEntity<ApiResponse<List<UserRelationshipDTO>>> getHighIntimacyRelationships(
            @RequestParam(defaultValue = "50") Integer minIntimacyScore,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<List<UserRelationshipDTO>> response = userRelationshipService.getHighIntimacyRelationships(minIntimacyScore, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取最近聊天的关系
     */
    @GetMapping("/recent-chat")
    public ResponseEntity<ApiResponse<Page<UserRelationshipDTO>>> getRecentChatRelationships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Page<UserRelationshipDTO>> response = userRelationshipService.getRecentChatRelationships(page, size, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新关系备注
     */
    @PutMapping("/{relationshipId}/notes")
    public ResponseEntity<ApiResponse<String>> updateRelationshipNotes(
            @PathVariable Long relationshipId,
            @RequestParam String notes,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<String> response = userRelationshipService.updateRelationshipNotes(relationshipId, notes, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新关系标签
     */
    @PutMapping("/{relationshipId}/tags")
    public ResponseEntity<ApiResponse<String>> updateRelationshipTags(
            @PathVariable Long relationshipId,
            @RequestParam String tags,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<String> response = userRelationshipService.updateRelationshipTags(relationshipId, tags, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 删除关系
     */
    @DeleteMapping("/{relationshipId}")
    public ResponseEntity<ApiResponse<String>> deleteRelationship(
            @PathVariable Long relationshipId,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<String> response = userRelationshipService.deleteRelationship(relationshipId, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取关系统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getRelationshipStatistics(
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Object> response = userRelationshipService.getRelationshipStatistics(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 检查两个用户是否有指定类型的关系
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> hasRelationship(
            @RequestParam Long targetUserId,
            @RequestParam UserRelationshipEntity.RelationshipType relationshipType,
            @RequestHeader("Authorization") String authHeader) {
        
        // 提取纯token
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        ApiResponse<Boolean> response = userRelationshipService.hasRelationship(targetUserId, relationshipType, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
