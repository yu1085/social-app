package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.LevelStatusDTO;
import com.socialmeet.backend.dto.VipBenefitDTO;
import com.socialmeet.backend.dto.WealthLevelDataDTO;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.WealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财富等级控制器
 */
@RestController
@RequestMapping("/api/wealth-level")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WealthController {
    
    private final WealthService wealthService;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取我的财富等级
     */
    @GetMapping("/my-level")
    public ApiResponse<WealthLevelDataDTO> getMyWealthLevel(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("获取我的财富等级");
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            WealthLevelDataDTO wealthLevel = wealthService.getCurrentWealthLevel(userId);
            
            return ApiResponse.success("获取成功", wealthLevel);
            
        } catch (Exception e) {
            log.error("获取财富等级失败", e);
            return ApiResponse.error("获取财富等级失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取财富等级进度
     */
    @GetMapping("/progress")
    public ApiResponse<List<LevelStatusDTO>> getWealthLevelProgress(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("获取财富等级进度");
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            List<LevelStatusDTO> progress = wealthService.getWealthLevelProgress(userId);
            
            return ApiResponse.success("获取成功", progress);
            
        } catch (Exception e) {
            log.error("获取财富等级进度失败", e);
            return ApiResponse.error("获取财富等级进度失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取VIP特权列表
     */
    @GetMapping("/privileges")
    public ApiResponse<List<VipBenefitDTO>> getUserPrivileges(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("获取VIP特权列表");
            
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            List<VipBenefitDTO> privileges = wealthService.getVipBenefits(userId);
            
            return ApiResponse.success("获取成功", privileges);
            
        } catch (Exception e) {
            log.error("获取VIP特权失败", e);
            return ApiResponse.error("获取VIP特权失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取财富值说明
     */
    @GetMapping("/rules")
    public ApiResponse<Object> getWealthRules() {
        try {
            log.info("获取财富值说明");
            
            // 返回财富值计算规则
            return ApiResponse.success("获取成功", new Object() {
                public final String rule = "每成功购买 100 聊币，即可获得 1 财富值";
                public final String[] exclusions = {
                    "充值赠送的聊币不计算财富值",
                    "活动赠送的聊币不计算财富值", 
                    "购买VIP/SVIP的聊币不计算财富值"
                };
            });
            
        } catch (Exception e) {
            log.error("获取财富值说明失败", e);
            return ApiResponse.error("获取财富值说明失败: " + e.getMessage());
        }
    }
}
