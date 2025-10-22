package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.IntimacyActionRequest;
import com.socialmeet.backend.dto.IntimacyDTO;
import com.socialmeet.backend.dto.IntimacyRewardDTO;
import com.socialmeet.backend.entity.IntimacyLevel;
import com.socialmeet.backend.service.IntimacyService;
import com.socialmeet.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 亲密度系统控制器
 */
@RestController
@RequestMapping("/api/intimacy")
@RequiredArgsConstructor
@Slf4j
public class IntimacyController {

    private final IntimacyService intimacyService;
    private final JwtUtil jwtUtil;

    /**
     * 记录亲密度行为
     * POST /api/intimacy/action
     */
    @PostMapping("/action")
    public ApiResponse<IntimacyDTO> recordAction(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody IntimacyActionRequest request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            IntimacyDTO result = intimacyService.recordAction(userId, request);
            return ApiResponse.success("行为记录成功", result);
        } catch (Exception e) {
            log.error("记录亲密度行为失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询用户的亲密度列表
     * GET /api/intimacy/list?limit=20
     */
    @GetMapping("/list")
    public ApiResponse<List<IntimacyDTO>> getIntimacyList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Integer limit) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<IntimacyDTO> list = intimacyService.getIntimacyList(userId, limit);
            return ApiResponse.success(list);
        } catch (Exception e) {
            log.error("查询亲密度列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询与指定用户的亲密度
     * GET /api/intimacy/{targetUserId}
     */
    @GetMapping("/{targetUserId}")
    public ApiResponse<IntimacyDTO> getIntimacy(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long targetUserId) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            IntimacyDTO intimacy = intimacyService.getIntimacy(userId, targetUserId);

            if (intimacy == null) {
                return ApiResponse.error("暂无亲密度记录");
            }

            return ApiResponse.success(intimacy);
        } catch (Exception e) {
            log.error("查询亲密度失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询所有等级配置
     * GET /api/intimacy/levels
     */
    @GetMapping("/levels")
    public ApiResponse<List<IntimacyLevel>> getAllLevels() {
        try {
            List<IntimacyLevel> levels = intimacyService.getAllLevels();
            return ApiResponse.success(levels);
        } catch (Exception e) {
            log.error("查询等级配置失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 查询用户的未领取奖励列表
     * GET /api/intimacy/rewards/unclaimed
     */
    @GetMapping("/rewards/unclaimed")
    public ApiResponse<List<IntimacyRewardDTO>> getUnclaimedRewards(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<IntimacyRewardDTO> rewards = intimacyService.getUnclaimedRewards(userId);
            return ApiResponse.success(rewards);
        } catch (Exception e) {
            log.error("查询未领取奖励失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 领取奖励
     * POST /api/intimacy/rewards/{rewardId}/claim
     */
    @PostMapping("/rewards/{rewardId}/claim")
    public ApiResponse<IntimacyRewardDTO> claimReward(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long rewardId) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            IntimacyRewardDTO reward = intimacyService.claimReward(userId, rewardId);
            return ApiResponse.success("领取成功", reward);
        } catch (Exception e) {
            log.error("领取奖励失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
}
