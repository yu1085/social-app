package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.MatchRequest;
import com.example.socialmeet.entity.MatchSession;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.service.MatchService;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/match")
@CrossOrigin(originPatterns = "*")
public class MatchController {
    
    @Autowired
    private MatchService matchService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建匹配请求
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createMatchRequest(
            @RequestHeader("Authorization") String token,
            @RequestParam String matchType,
            @RequestParam Integer preferenceLevel) {
        
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 创建匹配请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("匹配类型: " + matchType);
            System.out.println("偏好等级: " + preferenceLevel);
            
            MatchRequest matchRequest = matchService.createMatchRequest(userId, matchType, preferenceLevel);
            
            Map<String, Object> result = new HashMap<>();
            result.put("requestId", matchRequest.getId());
            result.put("status", matchRequest.getStatus());
            result.put("preferenceLevel", matchRequest.getPreferenceLevel());
            result.put("priceRange", new Double[]{matchRequest.getMinPrice(), matchRequest.getMaxPrice()});
            
            System.out.println("匹配请求创建成功: " + matchRequest.getId());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            System.err.println("创建匹配请求失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("创建匹配请求失败: " + e.getMessage()));
        }
    }
    
    /**
     * 尝试匹配
     */
    @PostMapping("/try-match")
    public ResponseEntity<ApiResponse<Map<String, Object>>> tryMatch(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 尝试匹配 ===");
            System.out.println("用户ID: " + userId);
            
            MatchSession matchSession = matchService.tryMatch(userId);
            
            if (matchSession == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("matched", false);
                result.put("message", "暂时没有可匹配的用户，请稍后再试");
                
                System.out.println("没有找到可匹配的用户");
                return ResponseEntity.ok(ApiResponse.success(result));
            }
            
            // 获取匹配用户的详细信息
            User matchedUser = userRepository.findById(matchSession.getUser2Id()).orElse(null);
            
            Map<String, Object> result = new HashMap<>();
            result.put("matched", true);
            result.put("sessionId", matchSession.getSessionId());
            result.put("matchedUserId", matchSession.getUser2Id());
            result.put("matchType", matchSession.getMatchType());
            result.put("callPrice", matchSession.getCallPrice());
            
            if (matchedUser != null) {
                result.put("matchedUserName", matchedUser.getNickname());
                result.put("matchedUserAvatar", matchedUser.getAvatarUrl());
                result.put("matchedUserAge", matchedUser.getAge());
                result.put("matchedUserLocation", matchedUser.getLocation());
            }
            
            System.out.println("匹配成功: " + matchSession.getSessionId());
            System.out.println("匹配用户ID: " + matchSession.getUser2Id());
            System.out.println("匹配用户昵称: " + (matchedUser != null ? matchedUser.getNickname() : "未知"));
            System.out.println("通话价格: " + matchSession.getCallPrice());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            System.err.println("匹配失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("匹配失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取匹配结果
     */
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMatchResult(
            @RequestHeader("Authorization") String token) {
        
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            MatchSession matchSession = matchService.getMatchResult(userId);
            
            if (matchSession == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("matched", false);
                result.put("message", "没有活跃的匹配会话");
                
                return ResponseEntity.ok(ApiResponse.success(result));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("matched", true);
            result.put("sessionId", matchSession.getSessionId());
            result.put("matchedUserId", matchSession.getUser2Id());
            result.put("matchType", matchSession.getMatchType());
            result.put("callPrice", matchSession.getCallPrice());
            result.put("createdAt", matchSession.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            System.err.println("获取匹配结果失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("获取匹配结果失败: " + e.getMessage()));
        }
    }
    
    /**
     * 结束匹配会话
     */
    @PostMapping("/end-session")
    public ResponseEntity<ApiResponse<String>> endMatchSession(
            @RequestHeader("Authorization") String token,
            @RequestParam String sessionId) {
        
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 结束匹配会话 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("会话ID: " + sessionId);
            
            matchService.endMatchSession(sessionId);
            
            System.out.println("匹配会话已结束: " + sessionId);
            
            return ResponseEntity.ok(ApiResponse.success("匹配会话已结束"));
            
        } catch (Exception e) {
            System.err.println("结束匹配会话失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error("结束匹配会话失败: " + e.getMessage()));
        }
    }
}
