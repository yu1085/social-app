package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.UserView;
import com.example.socialmeet.repository.UserViewRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    @Autowired
    private UserViewRepository userViewRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/view")
    public ResponseEntity<ApiResponse<String>> recordView(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long viewedId,
            @RequestParam(required = false) String viewType,
            @RequestParam(required = false) Long relatedId) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long viewerId = jwtUtil.getUserIdFromToken(jwt);
            
            UserView.ViewType type = viewType != null ? 
                    UserView.ViewType.valueOf(viewType.toUpperCase()) : 
                    UserView.ViewType.PROFILE;
            
            UserView userView = new UserView(viewerId, viewedId, type, relatedId);
            userViewRepository.save(userView);
            
            return ResponseEntity.ok(ApiResponse.success("记录浏览成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("记录浏览失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/views")
    public ResponseEntity<ApiResponse<List<UserView>>> getViews(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<UserView> views = userViewRepository.findByViewedIdOrderByCreatedAtDesc(userId);
            
            return ResponseEntity.ok(ApiResponse.success(views));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取浏览记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/views/count")
    public ResponseEntity<ApiResponse<Long>> getViewCount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Long count = userViewRepository.countByViewedId(userId);
            
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取浏览次数失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/views/recent")
    public ResponseEntity<ApiResponse<List<UserView>>> getRecentViews(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "7") int days) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            LocalDateTime endDate = LocalDateTime.now();
            
            List<UserView> views = userViewRepository.findByViewedIdAndDateRange(userId, startDate, endDate);
            
            return ResponseEntity.ok(ApiResponse.success(views));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取最近浏览记录失败: " + e.getMessage()));
        }
    }
}
