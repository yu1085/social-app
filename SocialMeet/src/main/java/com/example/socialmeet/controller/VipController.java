package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.VipLevelDTO;
import com.example.socialmeet.dto.VipSubscriptionDTO;
import com.example.socialmeet.service.VipService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vip")
@CrossOrigin(origins = "*")
public class VipController {
    
    @Autowired
    private VipService vipService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/levels")
    public ResponseEntity<ApiResponse<List<VipLevelDTO>>> getVipLevels() {
        try {
            List<VipLevelDTO> levels = vipService.getAllVipLevels();
            return ResponseEntity.ok(ApiResponse.success(levels));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/levels/{id}")
    public ResponseEntity<ApiResponse<VipLevelDTO>> getVipLevelById(@PathVariable Long id) {
        try {
            VipLevelDTO level = vipService.getVipLevelById(id);
            if (level == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("VIP等级不存在"));
            }
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<VipSubscriptionDTO>> subscribeVip(
            @RequestHeader("Authorization") String token,
            @RequestParam Long vipLevelId) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            VipSubscriptionDTO subscription = vipService.subscribeVip(userId, vipLevelId);
            return ResponseEntity.ok(ApiResponse.success(subscription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("订阅VIP失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<VipSubscriptionDTO>> getCurrentVipSubscription(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            VipSubscriptionDTO subscription = vipService.getCurrentVipSubscription(userId);
            return ResponseEntity.ok(ApiResponse.success(subscription));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP订阅失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<VipSubscriptionDTO>>> getVipHistory(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            List<VipSubscriptionDTO> history = vipService.getUserVipHistory(userId);
            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP历史失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkVipStatus(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            boolean isVip = vipService.isVipUser(userId);
            return ResponseEntity.ok(ApiResponse.success(isVip));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("检查VIP状态失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/level")
    public ResponseEntity<ApiResponse<Integer>> getVipLevel(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Integer level = vipService.getVipLevel(userId);
            return ResponseEntity.ok(ApiResponse.success(level));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取VIP等级失败: " + e.getMessage()));
        }
    }
}
