package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.entity.UserDevice;
import com.socialmeet.backend.service.UserDeviceService;
import com.socialmeet.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设备管理控制器
 */
@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final UserDeviceService userDeviceService;
    private final JwtUtil jwtUtil;

    {
        log.info("🚀🚀🚀 DeviceController 实例化块被调用 🚀🚀🚀");
    }

    /**
     * 注册或更新设备
     */
    @PostMapping("/register")
    public ApiResponse<String> registerDevice(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String registrationId,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false, defaultValue = "ANDROID") String deviceType) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            
            // 设置默认设备名称
            if (deviceName == null || deviceName.trim().isEmpty()) {
                deviceName = "Android设备_" + System.currentTimeMillis();
            }

            userDeviceService.registerOrUpdateDevice(userId, registrationId, deviceName, deviceType);
            
            log.info("✅ 设备注册成功 - userId: {}, registrationId: {}", userId, registrationId);
            return ApiResponse.success("设备注册成功");

        } catch (Exception e) {
            log.error("设备注册失败", e);
            return ApiResponse.error("设备注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户设备列表
     */
    @GetMapping("/list")
    public ApiResponse<List<UserDevice>> getDeviceList(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            List<UserDevice> devices = userDeviceService.getActiveDevices(userId);
            
            return ApiResponse.success("获取成功", devices);

        } catch (Exception e) {
            log.error("获取设备列表失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 停用设备
     */
    @PostMapping("/deactivate")
    public ApiResponse<String> deactivateDevice(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String registrationId) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            userDeviceService.deactivateDevice(userId, registrationId);
            
            return ApiResponse.success("设备已停用");

        } catch (Exception e) {
            log.error("停用设备失败", e);
            return ApiResponse.error("停用设备失败: " + e.getMessage());
        }
    }

    /**
     * 设备控制器健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("DeviceController is working!");
    }

    /**
     * 获取设备统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getDeviceStats(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            List<UserDevice> devices = userDeviceService.getActiveDevices(userId);
            
            // 计算各种设备统计
            long totalDevices = devices.size();
            long androidDevices = devices.stream()
                    .filter(d -> "ANDROID".equals(d.getDeviceType()))
                    .count();
            long iosDevices = devices.stream()
                    .filter(d -> "IOS".equals(d.getDeviceType()))
                    .count();
            long inactiveDevices = devices.stream()
                    .filter(d -> d.getIsActive() != null && !d.getIsActive())
                    .count();
            
            // 获取最后活跃设备
            String lastActiveDevice = devices.stream()
                    .filter(d -> d.getLastActiveAt() != null)
                    .max((d1, d2) -> d1.getLastActiveAt().compareTo(d2.getLastActiveAt()))
                    .map(UserDevice::getDeviceName)
                    .orElse("无");
            
            // 获取设备类型分布
            Map<String, Long> deviceTypeDistribution = devices.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            d -> d.getDeviceType() != null ? d.getDeviceType() : "未知",
                            java.util.stream.Collectors.counting()
                    ));
            
            Map<String, Object> stats = Map.of(
                "totalDevices", totalDevices,
                "activeDevices", totalDevices - inactiveDevices,
                "inactiveDevices", inactiveDevices,
                "androidDevices", androidDevices,
                "iosDevices", iosDevices,
                "otherDevices", totalDevices - androidDevices - iosDevices,
                "lastActiveDevice", lastActiveDevice,
                "deviceTypeDistribution", deviceTypeDistribution,
                "lastUpdated", java.time.LocalDateTime.now().toString()
            );
            
            log.info("设备统计 - userId: {}, totalDevices: {}, activeDevices: {}, androidDevices: {}, iosDevices: {}", 
                    userId, totalDevices, totalDevices - inactiveDevices, androidDevices, iosDevices);
            
            return ApiResponse.success("获取成功", stats);

        } catch (Exception e) {
            log.error("获取设备统计失败", e);
            return ApiResponse.error("获取失败: " + e.getMessage());
        }
    }
}
