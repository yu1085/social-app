package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.DynamicDTO;
import com.example.socialmeet.dto.PublishDynamicRequest;
import com.example.socialmeet.dto.DynamicFilterRequest;
import com.example.socialmeet.service.EnhancedDynamicService;
import com.example.socialmeet.util.PerformanceMonitor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 增强版动态控制器
 * 提供完整的广场动态功能
 */
@RestController
@RequestMapping("/api/v2/dynamics")
@CrossOrigin(originPatterns = "*")
public class EnhancedDynamicController {
    
    @Autowired
    private EnhancedDynamicService dynamicService;
    
    @Autowired
    private PerformanceMonitor performanceMonitor;
    
    /**
     * 发布动态
     */
    @PostMapping
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
                performanceMonitor.recordError("publishDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("发布动态失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 上传动态图片
     */
    @PostMapping("/upload-images")
    public CompletableFuture<ResponseEntity<ApiResponse<List<String>>>> uploadImages(
            @RequestParam("images") List<MultipartFile> images,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<List<String>> response = dynamicService.uploadImages(images, token);
                performanceMonitor.recordApiCall("uploadImages", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordError("uploadImages", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("上传图片失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 获取动态列表 - 支持多种筛选条件
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DynamicDTO>>> getDynamics(
            @RequestParam(defaultValue = "latest") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String keyword,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            DynamicFilterRequest filterRequest = DynamicFilterRequest.builder()
                .type(type)
                .page(page)
                .size(size)
                .location(location)
                .gender(gender)
                .minAge(minAge)
                .maxAge(maxAge)
                .keyword(keyword)
                .build();
            
            ApiResponse<Page<DynamicDTO>> response = dynamicService.getDynamics(filterRequest, token);
            performanceMonitor.recordApiCall("getDynamics", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordError("getDynamics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取动态列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取动态详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DynamicDTO>> getDynamicById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<DynamicDTO> response = dynamicService.getDynamicById(id, token);
            performanceMonitor.recordApiCall("getDynamicById", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordError("getDynamicById", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取动态详情失败: " + e.getMessage()));
        }
    }
    
    /**
     * 点赞/取消点赞动态
     */
    @PostMapping("/{id}/like")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> likeDynamic(
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
                performanceMonitor.recordError("likeDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("点赞操作失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 评论动态
     */
    @PostMapping("/{id}/comment")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> commentDynamic(
            @PathVariable Long id,
            @RequestBody String content,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<String> response = dynamicService.commentDynamic(id, content, token);
                performanceMonitor.recordApiCall("commentDynamic", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordError("commentDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("评论失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 获取动态评论列表
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Page<Object>>> getDynamicComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<Page<Object>> response = dynamicService.getDynamicComments(id, page, size, token);
            performanceMonitor.recordApiCall("getDynamicComments", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordError("getDynamicComments", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取评论失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除动态
     */
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> deleteDynamic(
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
                performanceMonitor.recordError("deleteDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("删除动态失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 举报动态
     */
    @PostMapping("/{id}/report")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> reportDynamic(
            @PathVariable Long id,
            @RequestBody String reason,
            @RequestHeader("Authorization") String token) {
        
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            try {
                ApiResponse<String> response = dynamicService.reportDynamic(id, reason, token);
                performanceMonitor.recordApiCall("reportDynamic", System.currentTimeMillis() - startTime);
                
                if (response.isSuccess()) {
                    return ResponseEntity.ok(response);
                } else {
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (Exception e) {
                performanceMonitor.recordError("reportDynamic", e.getMessage());
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("举报失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 获取用户动态列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<DynamicDTO>>> getUserDynamics(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<Page<DynamicDTO>> response = dynamicService.getUserDynamics(userId, page, size, token);
            performanceMonitor.recordApiCall("getUserDynamics", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordError("getUserDynamics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取用户动态失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取热门话题
     */
    @GetMapping("/trending-topics")
    public ResponseEntity<ApiResponse<List<String>>> getTrendingTopics(
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Authorization") String token) {
        
        long startTime = System.currentTimeMillis();
        try {
            ApiResponse<List<String>> response = dynamicService.getTrendingTopics(limit, token);
            performanceMonitor.recordApiCall("getTrendingTopics", System.currentTimeMillis() - startTime);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            performanceMonitor.recordError("getTrendingTopics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("获取热门话题失败: " + e.getMessage()));
        }
    }
    
    /**
     * 搜索动态
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<DynamicDTO>>> searchDynamics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
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
            performanceMonitor.recordError("searchDynamics", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("搜索动态失败: " + e.getMessage()));
        }
    }
}
