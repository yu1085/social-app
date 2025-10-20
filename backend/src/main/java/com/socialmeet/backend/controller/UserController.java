package com.socialmeet.backend.controller;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.dto.UserPhotoDTO;
import com.socialmeet.backend.dto.UploadPhotoResponse;
import com.socialmeet.backend.entity.UserPhoto;
import com.socialmeet.backend.security.JwtUtil;
import com.socialmeet.backend.service.AuthService;
import com.socialmeet.backend.service.JPushService;
import com.socialmeet.backend.service.FileUploadService;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.UserPhotoRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户控制器
 * 处理用户相关的请求
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final JPushService jPushService;
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final FileUploadService fileUploadService;

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

            // 发送测试推送（多设备支持）
            boolean success = jPushService.sendTestNotification(userId);
            
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

    // ========== 用户照片相关接口 ==========

    /**
     * 获取用户相册
     */
    @GetMapping("/{id}/photos")
    public ApiResponse<List<UserPhotoDTO>> getUserPhotos(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取用户相册 - 用户ID: {}", id);

            List<UserPhoto> photos = userPhotoRepository.findByUserIdOrderByUploadTimeDesc(id);
            List<UserPhotoDTO> photoDTOs = photos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("用户 {} 的相册共有 {} 张照片", id, photoDTOs.size());
            return ApiResponse.success(photoDTOs);

        } catch (Exception e) {
            log.error("获取用户相册失败 - 用户ID: {}", id, e);
            return ApiResponse.error("获取用户相册失败: " + e.getMessage());
        }
    }

    /**
     * 上传照片
     */
    @PostMapping("/{id}/photos")
    @Transactional
    public ApiResponse<UploadPhotoResponse> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(value = "isAvatar", defaultValue = "false") boolean isAvatar,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (!userId.equals(id)) {
                return ApiResponse.error("无权限上传照片");
            }

            log.info("用户 {} 上传照片，是否设为头像: {}", userId, isAvatar);

            // 上传照片文件
            String photoUrl = fileUploadService.uploadAvatar(photo, userId);

            // 保存照片记录
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUserId(userId);
            userPhoto.setPhotoUrl(photoUrl);
            userPhoto.setIsAvatar(isAvatar);
            userPhoto = userPhotoRepository.save(userPhoto);

            // 如果设为头像，更新用户头像URL并清除其他照片的头像标记
            if (isAvatar) {
                // 清除其他照片的头像标记
                List<UserPhoto> existingPhotos = userPhotoRepository.findByUserIdOrderByUploadTimeDesc(userId);
                for (UserPhoto existing : existingPhotos) {
                    if (!existing.getId().equals(userPhoto.getId()) && existing.getIsAvatar()) {
                        existing.setIsAvatar(false);
                        userPhotoRepository.save(existing);
                    }
                }

                // 更新用户头像URL
                var user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("用户不存在"));
                user.setAvatarUrl(photoUrl);
                userRepository.save(user);
            }

            UploadPhotoResponse response = new UploadPhotoResponse(
                    userPhoto.getId(),
                    photoUrl,
                    isAvatar,
                    "照片上传成功"
            );

            log.info("照片上传成功 - photoId: {}, photoUrl: {}", userPhoto.getId(), photoUrl);
            return ApiResponse.success(response);

        } catch (Exception e) {
            log.error("上传照片失败 - 用户ID: {}", id, e);
            return ApiResponse.error("上传照片失败: " + e.getMessage());
        }
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{id}/photos/{photoId}")
    @Transactional
    public ApiResponse<String> deletePhoto(
            @PathVariable Long id,
            @PathVariable Long photoId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (!userId.equals(id)) {
                return ApiResponse.error("无权限删除照片");
            }

            log.info("用户 {} 删除照片 {}", userId, photoId);

            // 查找照片
            UserPhoto photo = userPhotoRepository.findByIdAndUserId(photoId, userId)
                    .orElseThrow(() -> new RuntimeException("照片不存在或无权限删除"));

            // 如果是头像，需要清除用户的头像URL
            if (photo.getIsAvatar()) {
                var user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("用户不存在"));
                user.setAvatarUrl(null);
                userRepository.save(user);
            }

            // 删除照片记录
            userPhotoRepository.delete(photo);

            log.info("照片删除成功 - photoId: {}", photoId);
            return ApiResponse.success("照片删除成功");

        } catch (Exception e) {
            log.error("删除照片失败 - 用户ID: {}, photoId: {}", id, photoId, e);
            return ApiResponse.error("删除照片失败: " + e.getMessage());
        }
    }

    /**
     * 设置为头像
     */
    @PutMapping("/{id}/photos/{photoId}/avatar")
    @Transactional
    public ApiResponse<String> setAsAvatar(
            @PathVariable Long id,
            @PathVariable Long photoId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证身份
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (!userId.equals(id)) {
                return ApiResponse.error("无权限设置头像");
            }

            log.info("用户 {} 设置照片 {} 为头像", userId, photoId);

            // 查找照片
            UserPhoto photo = userPhotoRepository.findByIdAndUserId(photoId, userId)
                    .orElseThrow(() -> new RuntimeException("照片不存在或无权限设置"));

            // 清除其他照片的头像标记
            List<UserPhoto> existingPhotos = userPhotoRepository.findByUserIdOrderByUploadTimeDesc(userId);
            for (UserPhoto existing : existingPhotos) {
                if (existing.getIsAvatar()) {
                    existing.setIsAvatar(false);
                    userPhotoRepository.save(existing);
                }
            }

            // 设置当前照片为头像
            photo.setIsAvatar(true);
            userPhotoRepository.save(photo);

            // 更新用户头像URL
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            user.setAvatarUrl(photo.getPhotoUrl());
            userRepository.save(user);

            log.info("头像设置成功 - photoId: {}, photoUrl: {}", photoId, photo.getPhotoUrl());
            return ApiResponse.success("头像设置成功");

        } catch (Exception e) {
            log.error("设置头像失败 - 用户ID: {}, photoId: {}", id, photoId, e);
            return ApiResponse.error("设置头像失败: " + e.getMessage());
        }
    }

    /**
     * 将UserPhoto实体转换为DTO
     */
    private UserPhotoDTO convertToDTO(UserPhoto photo) {
        return new UserPhotoDTO(
                photo.getId(),
                photo.getUserId(),
                photo.getPhotoUrl(),
                photo.getIsAvatar(),
                photo.getUploadTime()
        );
    }
}
