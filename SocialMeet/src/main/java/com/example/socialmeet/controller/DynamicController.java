package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.DynamicDTO;
import com.example.socialmeet.dto.PublishDynamicRequest;
import com.example.socialmeet.service.OptimizedDynamicService;
import com.example.socialmeet.util.PerformanceMonitor;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * 动态控制器 - 统一优化版本
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/dynamics")
@CrossOrigin(originPatterns = "*")
// @Tag(name = "动态管理", description = "广场动态相关API接口")
public class DynamicController {
    
    @Autowired
    private OptimizedDynamicService dynamicService;
    
    @Autowired
    private PerformanceMonitor performanceMonitor;
    
    /**
     * 发布动态
     * 
     * @param request 发布动态请求
     * @param token 用户认证token
     * @return 动态信息
     */
    @PostMapping
    // @Operation(summary = "发布动态", description = "用户发布新的广场动态")
    public CompletableFuture<ResponseEntity<ApiResponse<DynamicDTO>>> publishDynamic(
            @Valid @RequestBody PublishDynamicRequest request,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<DynamicDTO> response = dynamicService.publishDynamic(request, token);
                performanceMonitor.recordApiCall("publishDynamic", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordApiError("publishDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("发布动态失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 获取动态列表
     * 
     * @param type 动态类型 (latest, hot, nearby, following, liked)
     * @param page 页码
     * @param size 每页大小
     * @param token 用户认证token
     * @return 动态列表
     */
    @GetMapping
    // @Operation(summary = "获取动态列表", description = "根据类型获取动态列表")
    public ResponseEntity<ApiResponse<Page<DynamicDTO>>> getDynamics(
            // @Parameter(description = "动态类型: latest(最新), hot(热门), nearby(附近), following(关注), liked(已点赞)")
            @RequestParam(defaultValue = "latest") String type,
            // @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            // @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<Page<DynamicDTO>> response = dynamicService.getDynamics(type, page, size, token);
            performanceMonitor.recordApiCall("getDynamics", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordApiError("getDynamics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取动态列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取动态详情
     * 
     * @param id 动态ID
     * @param token 用户认证token
     * @return 动态详情
     */
    @GetMapping("/{id}")
    // @Operation(summary = "获取动态详情", description = "根据ID获取动态详细信息")
    public ResponseEntity<ApiResponse<DynamicDTO>> getDynamicDetail(
            // @Parameter(description = "动态ID")
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<DynamicDTO> response = dynamicService.getDynamicById(id);
            performanceMonitor.recordApiCall("getDynamicDetail", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordApiError("getDynamicDetail", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取动态详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 点赞/取消点赞动态
     * 
     * @param id 动态ID
     * @param token 用户认证token
     * @return 操作结果
     */
    @PostMapping("/{id}/like")
    // @Operation(summary = "点赞动态", description = "点赞或取消点赞动态")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> likeDynamic(
            // @Parameter(description = "动态ID")
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<String> response = dynamicService.likeDynamic(id, token);
                performanceMonitor.recordApiCall("likeDynamic", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordApiError("likeDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("操作失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 删除动态
     * 
     * @param id 动态ID
     * @param token 用户认证token
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    // @Operation(summary = "删除动态", description = "删除用户自己的动态")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> deleteDynamic(
            // @Parameter(description = "动态ID")
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<String> response = dynamicService.deleteDynamic(id, token);
                performanceMonitor.recordApiCall("deleteDynamic", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordApiError("deleteDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("删除动态失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 搜索动态
     * 
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param token 用户认证token
     * @return 搜索结果
     */
    @GetMapping("/search")
    // @Operation(summary = "搜索动态", description = "根据关键词搜索动态")
    public ResponseEntity<ApiResponse<Page<DynamicDTO>>> searchDynamics(
            // @Parameter(description = "搜索关键词")
            @RequestParam String keyword,
            // @Parameter(description = "页码，从0开始")
            @RequestParam(defaultValue = "0") int page,
            // @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<Page<DynamicDTO>> response = dynamicService.searchDynamics(keyword, page, size, token);
            performanceMonitor.recordApiCall("searchDynamics", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordApiError("searchDynamics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("搜索动态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取性能统计
     * 
     * @return 性能统计信息
     */
    @GetMapping("/performance/stats")
    // @Operation(summary = "获取性能统计", description = "获取API性能统计信息")
    public ResponseEntity<PerformanceMonitor.PerformanceStats> getPerformanceStats() {
        PerformanceMonitor.PerformanceStats stats = performanceMonitor.getPerformanceStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 重置性能统计
     * 
     * @return 操作结果
     */
    @PostMapping("/performance/reset")
    // @Operation(summary = "重置性能统计", description = "重置API性能统计信息")
    public ResponseEntity<String> resetPerformanceStats() {
        performanceMonitor.resetStats();
        return ResponseEntity.ok("性能统计已重置");
    }
}
