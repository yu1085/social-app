package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.AuthService;
import com.socialmeet.backend.service.JPushService;
import com.socialmeet.backend.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户相关的请求
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final JPushService jPushService;
    private final UserRepository userRepository;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<UserDTO> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            UserDTO user = authService.getUserProfile(userId);
            return ApiResponse.success(user);

        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<UserDTO> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserDTO userDTO) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            UserDTO updatedUser = authService.updateUserProfile(userId, userDTO);
            return ApiResponse.success("更新成功", updatedUser);

        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ApiResponse.error("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("═══════════════════════════════════════");
            log.info("收到获取用户详情请求 - 用户ID: {}", id);
            log.info("Authorization头: {}", authHeader);
            log.info("═══════════════════════════════════════");
            
            UserDTO user = authService.getUserProfile(id);
            
            log.info("用户详情获取成功 - ID: {}, 用户名: {}, 昵称: {}", 
                    user.getId(), user.getUsername(), user.getNickname());
            
            return ApiResponse.success(user);

        } catch (Exception e) {
            log.error("获取用户信息失败 - 用户ID: {}", id, e);
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 搜索用户列表
     */
    @GetMapping("/search")
    public ApiResponse<java.util.List<UserDTO>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("═══════════════════════════════════════");
            log.info("收到搜索用户请求 - keyword: {}, gender: {}, location: {}, minAge: {}, maxAge: {}, page: {}, size: {}", 
                    keyword, gender, location, minAge, maxAge, page, size);
            log.info("═══════════════════════════════════════");
            
            java.util.List<UserDTO> users = authService.searchUsers(
                    keyword, gender, location, minAge, maxAge, page, size);
            
            log.info("搜索用户成功 - 返回 {} 个用户", users.size());
            for (int i = 0; i < users.size(); i++) {
                UserDTO user = users.get(i);
                log.info("  用户{} - ID: {}, 用户名: {}, 昵称: {}", 
                        i, user.getId(), user.getUsername(), user.getNickname());
            }
            
            return ApiResponse.success(users);

        } catch (Exception e) {
            log.error("搜索用户失败", e);
            return ApiResponse.error("搜索用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户的 JPush Registration ID
     */
    @PostMapping("/registration-id")
    public ApiResponse<String> updateRegistrationId(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String registrationId) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            authService.updateRegistrationId(userId, registrationId);

            log.info("✅ 用户 {} 的 Registration ID 已更新: {}", userId, registrationId);
            return ApiResponse.success("Registration ID 更新成功");

        } catch (Exception e) {
            log.error("更新 Registration ID 失败", e);
            return ApiResponse.error("更新 Registration ID 失败: " + e.getMessage());
        }
    }

    /**
     * 查看用户Registration ID状态
     */
    @GetMapping("/registration-status")
    public ApiResponse<Map<String, Object>> getRegistrationStatus(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 获取用户的Registration ID
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            String registrationId = user.getJpushRegistrationId();
            
            Map<String, Object> status = new HashMap<>();
            status.put("userId", userId);
            status.put("registrationId", registrationId);
            status.put("isValid", registrationId != null && !registrationId.trim().isEmpty() && !"0".equals(registrationId));
            status.put("username", user.getUsername());
            
            return ApiResponse.success("获取成功", status);

        } catch (Exception e) {
            log.error("获取Registration状态失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 测试JPush推送功能
     */
    @PostMapping("/test-push")
    public ApiResponse<String> testPush(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 获取用户的Registration ID
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            String registrationId = user.getJpushRegistrationId();
            
            if (registrationId == null || registrationId.trim().isEmpty() || "0".equals(registrationId)) {
                return ApiResponse.error("用户未注册推送服务，Registration ID: " + registrationId);
            }

            // 发送测试推送
            boolean success = jPushService.sendTestNotification(userId, registrationId);
            
            if (success) {
                log.info("✅ 测试推送发送成功 - userId: {}, registrationId: {}", userId, registrationId);
                return ApiResponse.success("测试推送发送成功，Registration ID: " + registrationId);
            } else {
                log.error("❌ 测试推送发送失败 - userId: {}, registrationId: {}", userId, registrationId);
                return ApiResponse.error("测试推送发送失败");
            }

        } catch (Exception e) {
            log.error("测试推送失败", e);
            return ApiResponse.error("测试推送失败: " + e.getMessage());
        }
    }
}
