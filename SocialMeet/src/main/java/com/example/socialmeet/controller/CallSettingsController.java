package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.CallSettings;
import com.example.socialmeet.service.CallSettingsService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通话设置控制器
 */
@RestController
@RequestMapping("/api/users/settings/call")
@CrossOrigin(originPatterns = "*")
public class CallSettingsController {
    
    @Autowired
    private CallSettingsService callSettingsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取用户的通话设置
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CallSettings>> getCallSettings(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            CallSettings settings = callSettingsService.getUserCallSettings(userId);
            
            if (settings == null) {
                // 老用户没有通话设置记录，返回当前价格信息
                Map<String, Object> currentPrices = callSettingsService.getUserCallPrices(userId);
                CallSettings currentSettings = new CallSettings(userId);
                currentSettings.setVideoCallPrice(((Number) currentPrices.get("videoCallPrice")).doubleValue());
                currentSettings.setVoiceCallPrice(((Number) currentPrices.get("voiceCallPrice")).doubleValue());
                currentSettings.setMessagePrice(((Number) currentPrices.get("messagePrice")).doubleValue());
                currentSettings.setVideoCallEnabled((Boolean) currentPrices.get("videoCallEnabled"));
                currentSettings.setVoiceCallEnabled((Boolean) currentPrices.get("voiceCallEnabled"));
                currentSettings.setMessageChargeEnabled((Boolean) currentPrices.get("messageChargeEnabled"));
                currentSettings.setFreeCallDuration(0); // 老用户默认0秒免费
                currentSettings.setAutoAnswerEnabled(false); // 老用户默认不自动接听
                
                System.out.println("=== 老用户 " + userId + " 当前价格: " + currentSettings.getVideoCallPrice() + "/分钟 ===");
                return ResponseEntity.ok(ApiResponse.success(currentSettings));
            }
            
            return ResponseEntity.ok(ApiResponse.success(settings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取通话设置失败: " + e.getMessage()));
        }
    }
    
    /**
     * 更新用户的通话设置
     */
    @PutMapping
    public ResponseEntity<ApiResponse<CallSettings>> updateCallSettings(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> updateData) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            CallSettings settings = callSettingsService.updateUserCallSettings(userId, updateData);
            return ResponseEntity.ok(ApiResponse.success(settings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("更新通话设置失败: " + e.getMessage()));
        }
    }
}
