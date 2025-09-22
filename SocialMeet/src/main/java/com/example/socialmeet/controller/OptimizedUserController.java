package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.service.OptimizedUserService;
import com.example.socialmeet.util.PerformanceMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 优化后的用户控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class OptimizedUserController {
    
    @Autowired
    private OptimizedUserService userService;
    
    @Autowired
    private PerformanceMonitor performanceMonitor;
    
    /**
     * 获取用户详情 - 带缓存
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        ApiResponse<UserDTO> response = userService.getUserById(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户卡片列表 - 带缓存
     */
    @GetMapping("/cards")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUserCards(
            @RequestParam(defaultValue = "女") String gender,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        ApiResponse<List<UserDTO>> response = userService.getUserCards(gender, category, page, size);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 搜索用户 - 带缓存
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        ApiResponse<List<UserDTO>> response = userService.searchUsers(keyword, location, gender, page, size);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新用户信息 - 异步处理
     */
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<UserDTO>>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO) {
        
        return CompletableFuture.supplyAsync(() -> {
            ApiResponse<UserDTO> response = userService.updateUser(id, userDTO);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    /**
     * 更新用户状态 - 异步处理
     */
    @PutMapping("/{id}/status")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        return CompletableFuture.supplyAsync(() -> {
            ApiResponse<String> response = userService.updateUserStatus(id, status);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    /**
     * 关注用户 - 异步处理
     */
    @PostMapping("/{id}/follow")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> followUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            // 从token获取当前用户ID
            Long currentUserId = getCurrentUserIdFromToken(token);
            if (currentUserId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }
            
            ApiResponse<String> response = userService.followUser(currentUserId, id);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    /**
     * 取消关注用户 - 异步处理
     */
    @DeleteMapping("/{id}/follow")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> unfollowUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            // 从token获取当前用户ID
            Long currentUserId = getCurrentUserIdFromToken(token);
            if (currentUserId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("用户未登录"));
            }
            
            ApiResponse<String> response = userService.unfollowUser(currentUserId, id);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        });
    }
    
    /**
     * 获取在线用户数量 - 带缓存
     */
    @GetMapping("/online/count")
    public ResponseEntity<ApiResponse<Long>> getOnlineUserCount() {
        ApiResponse<Long> response = userService.getOnlineUserCount();
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取用户统计信息 - 带缓存
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<Object>> getUserStats(@PathVariable Long id) {
        ApiResponse<Object> response = userService.getUserStats(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取性能统计
     */
    @GetMapping("/performance/stats")
    public ResponseEntity<PerformanceMonitor.PerformanceStats> getPerformanceStats() {
        PerformanceMonitor.PerformanceStats stats = performanceMonitor.getPerformanceStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 重置性能统计
     */
    @PostMapping("/performance/reset")
    public ResponseEntity<String> resetPerformanceStats() {
        performanceMonitor.resetStats();
        return ResponseEntity.ok("性能统计已重置");
    }
    
    /**
     * 从token获取当前用户ID
     */
    private Long getCurrentUserIdFromToken(String token) {
        // 这里应该实现JWT token解析逻辑
        // 暂时返回null，实际项目中需要实现
        return null;
    }
}
