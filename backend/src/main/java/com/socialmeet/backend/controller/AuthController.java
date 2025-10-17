package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.LoginResponse;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理登录、注册、验证码等认证相关请求
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("服务正常运行");
    }

    /**
     * 发送验证码
     * @param phone 手机号
     */
    @PostMapping("/send-code")
    public ApiResponse<String> sendVerificationCode(@RequestParam String phone) {
        try {
            log.info("收到发送验证码请求 - 手机号: {}", phone);

            // 验证手机号格式
            if (!isValidPhone(phone)) {
                return ApiResponse.error("手机号格式不正确");
            }

            String result = authService.sendVerificationCode(phone);
            return ApiResponse.success(result);

        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return ApiResponse.error("发送验证码失败: " + e.getMessage());
        }
    }

    /**
     * 验证码登录/注册
     * @param phone 手机号
     * @param code 验证码
     */
    @PostMapping("/login-with-code")
    public ApiResponse<LoginResponse> loginWithVerificationCode(
            @RequestParam String phone,
            @RequestParam String code) {
        try {
            log.info("收到验证码登录请求 - 手机号: {}", phone);

            // 验证手机号格式
            if (!isValidPhone(phone)) {
                return ApiResponse.error("手机号格式不正确");
            }

            // 验证验证码格式
            if (code == null || code.trim().isEmpty()) {
                return ApiResponse.error("验证码不能为空");
            }

            LoginResponse response = authService.loginWithVerificationCode(phone, code);
            return ApiResponse.success("登录成功", response);

        } catch (Exception e) {
            log.error("登录失败", e);
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

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
     * 上传 JPush Registration ID
     * 用于将设备的 JPush Registration ID 与用户账号关联
     */
    @PostMapping("/update-registration-id")
    public ApiResponse<String> updateRegistrationId(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String registrationId) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("更新用户 Registration ID - userId: {}, registrationId: {}", userId, registrationId);

            authService.updateRegistrationId(userId, registrationId);
            return ApiResponse.success("Registration ID 更新成功");

        } catch (Exception e) {
            log.error("更新 Registration ID 失败", e);
            return ApiResponse.error("更新 Registration ID 失败: " + e.getMessage());
        }
    }

    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // 中国手机号格式：1开头，第二位是3-9，总共11位
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
