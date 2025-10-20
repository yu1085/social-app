package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.*;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.FileUploadService;
import com.socialmeet.backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料控制器
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;
    private final FileUploadService fileUploadService;
    private final JwtUtil jwtUtil;

    /**
     * 获取用户完整资料信息
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Map<String, Object> profile = profileService.getUserProfile(userId);
            
            log.info("获取用户资料成功 - userId: {}", userId);
            return ApiResponse.success(profile);

        } catch (Exception e) {
            log.error("获取用户资料失败", e);
            return ApiResponse.error("获取用户资料失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户资料
     */
    @PutMapping
    public ApiResponse<UserDTO> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProfileUpdateRequest request) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            UserDTO updatedUser = profileService.updateProfile(userId, request);
            
            log.info("更新用户资料成功 - userId: {}", userId);
            return ApiResponse.success(updatedUser);

        } catch (Exception e) {
            log.error("更新用户资料失败", e);
            return ApiResponse.error("更新用户资料失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户设置
     */
    @GetMapping("/settings")
    public ApiResponse<UserSettingsDTO> getUserSettings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            UserSettingsDTO settings = profileService.getUserSettings(userId);
            
            log.info("获取用户设置成功 - userId: {}", userId);
            return ApiResponse.success(settings);

        } catch (Exception e) {
            log.error("获取用户设置失败", e);
            return ApiResponse.error("获取用户设置失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户设置
     */
    @PutMapping("/settings")
    public ApiResponse<UserSettingsDTO> updateUserSettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserSettingsDTO settingsDTO) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            UserSettingsDTO updatedSettings = profileService.updateUserSettings(userId, settingsDTO);
            
            log.info("更新用户设置成功 - userId: {}", userId);
            return ApiResponse.success(updatedSettings);

        } catch (Exception e) {
            log.error("更新用户设置失败", e);
            return ApiResponse.error("更新用户设置失败: " + e.getMessage());
        }
    }

    /**
     * 获取钱包信息
     */
    @GetMapping("/wallet")
    public ApiResponse<WalletDTO> getWalletInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            WalletDTO wallet = profileService.getWalletInfo(userId);
            
            log.info("获取钱包信息成功 - userId: {}", userId);
            return ApiResponse.success(wallet);

        } catch (Exception e) {
            log.error("获取钱包信息失败", e);
            return ApiResponse.error("获取钱包信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取VIP信息
     */
    @GetMapping("/vip")
    public ApiResponse<VipInfoDTO> getVipInfo(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Map<String, Object> profile = profileService.getUserProfile(userId);
            VipInfoDTO vipInfo = (VipInfoDTO) profile.get("vipInfo");
            
            log.info("获取VIP信息成功 - userId: {}", userId);
            return ApiResponse.success(vipInfo);

        } catch (Exception e) {
            log.error("获取VIP信息失败", e);
            return ApiResponse.error("获取VIP信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getUserStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Map<String, Object> stats = profileService.getUserStats(userId);

            log.info("获取用户统计信息成功 - userId: {}", userId);
            return ApiResponse.success(stats);

        } catch (Exception e) {
            log.error("获取用户统计信息失败", e);
            return ApiResponse.error("获取用户统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/upload-avatar")
    public ApiResponse<Map<String, String>> uploadAvatar(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            // 上传文件
            String avatarUrl = fileUploadService.uploadAvatar(file, userId);

            // 更新用户头像URL
            ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
            updateRequest.setAvatarUrl(avatarUrl);
            profileService.updateProfile(userId, updateRequest);

            // 返回头像URL
            Map<String, String> result = new HashMap<>();
            result.put("avatarUrl", avatarUrl);

            log.info("上传头像成功 - userId: {}, avatarUrl: {}", userId, avatarUrl);
            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("上传头像失败", e);
            return ApiResponse.error("上传头像失败: " + e.getMessage());
        }
    }
}
