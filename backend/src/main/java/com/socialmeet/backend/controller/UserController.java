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
    private final com.socialmeet.backend.service.UserRelationshipService userRelationshipService;

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
            String photoUrl = fileUploadService.uploadImage(photo, userId);

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
     * 获取在线人数统计
     */
    @GetMapping("/online-stats")
    public ApiResponse<Map<String, Integer>> getOnlineStats() {
        try {
            log.info("获取在线人数统计");

            // 模拟在线人数（实际应用中应该基于用户最后活跃时间）
            Map<String, Integer> stats = new HashMap<>();
            stats.put("videoOnline", 13264); // 视频速配在线人数
            stats.put("voiceOnline", 1153);  // 语音速配在线人数
            stats.put("totalOnline", 14417); // 总在线人数

            return ApiResponse.success(stats);

        } catch (Exception e) {
            log.error("获取在线人数统计失败", e);
            return ApiResponse.error("获取在线人数统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐用户列表
     * 用于首页推荐卡片
     */
    @GetMapping("/recommended")
    public ApiResponse<List<UserDTO>> getRecommendedUsers(
            @RequestParam(defaultValue = "4") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取推荐用户列表 - size: {}", size);

            // 简单实现：返回最新注册的用户（排除当前用户）
            Long currentUserId = null;
            if (authHeader != null) {
                try {
                    String token = jwtUtil.extractTokenFromHeader(authHeader);
                    if (token != null) {
                        currentUserId = jwtUtil.getUserIdFromToken(token);
                    }
                } catch (Exception e) {
                    log.warn("解析token失败，继续返回推荐用户", e);
                }
            }

            // 获取用户列表（排除当前用户，按创建时间倒序）
            List<UserDTO> users = authService.searchUsers(null, null, null, null, null, 0, size + 10);

            // 获取黑名单用户ID列表
            List<Long> blacklistedUserIds = currentUserId != null
                ? userRelationshipService.getBlacklistedUserIds(currentUserId)
                : List.of();

            // 过滤掉当前用户和黑名单用户
            final Long finalCurrentUserId = currentUserId;
            List<UserDTO> recommendedUsers = users.stream()
                    .filter(user -> finalCurrentUserId == null || !user.getId().equals(finalCurrentUserId))
                    .filter(user -> !blacklistedUserIds.contains(user.getId()))
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("推荐用户列表获取成功 - 返回 {} 个用户, 过滤了 {} 个黑名单用户",
                    recommendedUsers.size(), blacklistedUserIds.size());
            return ApiResponse.success(recommendedUsers);

        } catch (Exception e) {
            log.error("获取推荐用户列表失败", e);
            return ApiResponse.error("获取推荐用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取知友列表
     */
    @GetMapping("/acquaintances")
    public ApiResponse<List<UserDTO>> getAcquaintances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取知友列表 - page: {}, size: {}", page, size);

            // 优先从关系表获取，如果没有则返回随机用户
            Long currentUserId = null;
            if (authHeader != null) {
                try {
                    String token = jwtUtil.extractTokenFromHeader(authHeader);
                    if (token != null) {
                        currentUserId = jwtUtil.getUserIdFromToken(token);
                    }
                } catch (Exception e) {
                    log.warn("解析token失败", e);
                }
            }

            // 获取黑名单用户ID列表
            List<Long> blacklistedUserIds = currentUserId != null
                ? userRelationshipService.getBlacklistedUserIds(currentUserId)
                : List.of();

            List<UserDTO> users;
            if (currentUserId != null) {
                users = userRelationshipService.getFriendsList(currentUserId);
                if (users.isEmpty()) {
                    // 如果没有知友关系，返回随机用户列表
                    users = authService.searchUsers(null, null, null, null, null, page, size + blacklistedUserIds.size());
                }
            } else {
                users = authService.searchUsers(null, null, null, null, null, page, size);
            }

            // 过滤黑名单用户
            List<UserDTO> filteredUsers = users.stream()
                    .filter(user -> !blacklistedUserIds.contains(user.getId()))
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("知友列表获取成功 - 返回 {} 个用户, 过滤了 {} 个黑名单用户",
                    filteredUsers.size(), blacklistedUserIds.size());
            return ApiResponse.success(filteredUsers);

        } catch (Exception e) {
            log.error("获取知友列表失败", e);
            return ApiResponse.error("获取知友列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取喜欢列表
     */
    @GetMapping("/likes")
    public ApiResponse<List<UserDTO>> getLikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取喜欢列表 - page: {}, size: {}", page, size);

            // 优先从关系表获取，如果没有则返回女性用户列表
            Long currentUserId = null;
            if (authHeader != null) {
                try {
                    String token = jwtUtil.extractTokenFromHeader(authHeader);
                    if (token != null) {
                        currentUserId = jwtUtil.getUserIdFromToken(token);
                    }
                } catch (Exception e) {
                    log.warn("解析token失败", e);
                }
            }

            // 获取黑名单用户ID列表
            List<Long> blacklistedUserIds = currentUserId != null
                ? userRelationshipService.getBlacklistedUserIds(currentUserId)
                : List.of();

            List<UserDTO> users;
            if (currentUserId != null) {
                users = userRelationshipService.getLikesList(currentUserId);
                if (users.isEmpty()) {
                    // 如果没有喜欢关系，返回女性用户列表
                    users = authService.searchUsers(null, "FEMALE", null, null, null, page, size + blacklistedUserIds.size());
                }
            } else {
                users = authService.searchUsers(null, "FEMALE", null, null, null, page, size);
            }

            // 过滤黑名单用户
            List<UserDTO> filteredUsers = users.stream()
                    .filter(user -> !blacklistedUserIds.contains(user.getId()))
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("喜欢列表获取成功 - 返回 {} 个用户, 过滤了 {} 个黑名单用户",
                    filteredUsers.size(), blacklistedUserIds.size());
            return ApiResponse.success(filteredUsers);

        } catch (Exception e) {
            log.error("获取喜欢列表失败", e);
            return ApiResponse.error("获取喜欢列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取亲密列表
     */
    @GetMapping("/intimate")
    public ApiResponse<List<UserDTO>> getIntimate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            log.info("获取亲密列表 - page: {}, size: {}", page, size);

            // 优先从关系表获取，如果没有则返回用户列表
            Long currentUserId = null;
            if (authHeader != null) {
                try {
                    String token = jwtUtil.extractTokenFromHeader(authHeader);
                    if (token != null) {
                        currentUserId = jwtUtil.getUserIdFromToken(token);
                    }
                } catch (Exception e) {
                    log.warn("解析token失败", e);
                }
            }

            // 获取黑名单用户ID列表
            List<Long> blacklistedUserIds = currentUserId != null
                ? userRelationshipService.getBlacklistedUserIds(currentUserId)
                : List.of();

            List<UserDTO> users;
            if (currentUserId != null) {
                users = userRelationshipService.getIntimateList(currentUserId);
                if (users.isEmpty()) {
                    // 如果没有亲密关系，返回用户列表
                    users = authService.searchUsers(null, null, null, null, null, page, size + blacklistedUserIds.size());
                }
            } else {
                users = authService.searchUsers(null, null, null, null, null, page, size);
            }

            // 过滤黑名单用户
            List<UserDTO> filteredUsers = users.stream()
                    .filter(user -> !blacklistedUserIds.contains(user.getId()))
                    .limit(size)
                    .collect(Collectors.toList());

            log.info("亲密列表获取成功 - 返回 {} 个用户, 过滤了 {} 个黑名单用户",
                    filteredUsers.size(), blacklistedUserIds.size());
            return ApiResponse.success(filteredUsers);

        } catch (Exception e) {
            log.error("获取亲密列表失败", e);
            return ApiResponse.error("获取亲密列表失败: " + e.getMessage());
        }
    }

    // ========== 用户关系管理接口 ==========

    /**
     * 添加知友
     */
    @PostMapping("/{targetUserId}/friend")
    public ApiResponse<String> addFriend(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.addFriend(userId, targetUserId);

            if (success) {
                log.info("添加知友成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("添加知友成功");
            } else {
                return ApiResponse.error("添加知友失败");
            }

        } catch (Exception e) {
            log.error("添加知友失败", e);
            return ApiResponse.error("添加知友失败: " + e.getMessage());
        }
    }

    /**
     * 删除知友
     */
    @DeleteMapping("/{targetUserId}/friend")
    public ApiResponse<String> removeFriend(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.removeFriend(userId, targetUserId);

            if (success) {
                log.info("删除知友成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("删除知友成功");
            } else {
                return ApiResponse.error("删除知友失败");
            }

        } catch (Exception e) {
            log.error("删除知友失败", e);
            return ApiResponse.error("删除知友失败: " + e.getMessage());
        }
    }

    /**
     * 添加喜欢
     */
    @PostMapping("/{targetUserId}/like")
    public ApiResponse<String> addLike(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.addLike(userId, targetUserId);

            if (success) {
                log.info("添加喜欢成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("添加喜欢成功");
            } else {
                return ApiResponse.error("添加喜欢失败");
            }

        } catch (Exception e) {
            log.error("添加喜欢失败", e);
            return ApiResponse.error("添加喜欢失败: " + e.getMessage());
        }
    }

    /**
     * 取消喜欢
     */
    @DeleteMapping("/{targetUserId}/like")
    public ApiResponse<String> removeLike(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.removeLike(userId, targetUserId);

            if (success) {
                log.info("取消喜欢成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("取消喜欢成功");
            } else {
                return ApiResponse.error("取消喜欢失败");
            }

        } catch (Exception e) {
            log.error("取消喜欢失败", e);
            return ApiResponse.error("取消喜欢失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已喜欢
     */
    @GetMapping("/{targetUserId}/is-liked")
    public ApiResponse<Boolean> isLiked(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean isLiked = userRelationshipService.isLiked(userId, targetUserId);

            return ApiResponse.success(isLiked);

        } catch (Exception e) {
            log.error("检查喜欢状态失败", e);
            return ApiResponse.error("检查喜欢状态失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否是知友
     */
    @GetMapping("/{targetUserId}/is-friend")
    public ApiResponse<Boolean> isFriend(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean isFriend = userRelationshipService.isFriend(userId, targetUserId);

            return ApiResponse.success(isFriend);

        } catch (Exception e) {
            log.error("检查知友状态失败", e);
            return ApiResponse.error("检查知友状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除知友
     */
    @DeleteMapping("/friends/batch")
    public ApiResponse<String> removeFriendsBatch(
            @RequestBody List<Long> targetUserIds,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            if (targetUserIds == null || targetUserIds.isEmpty()) {
                return ApiResponse.error("未选择要删除的知友");
            }

            int successCount = 0;
            for (Long targetUserId : targetUserIds) {
                boolean success = userRelationshipService.removeFriend(userId, targetUserId);
                if (success) {
                    successCount++;
                }
            }

            log.info("批量删除知友完成 - userId: {}, 成功删除: {}/{}", userId, successCount, targetUserIds.size());
            return ApiResponse.success(String.format("成功删除%d个知友", successCount));

        } catch (Exception e) {
            log.error("批量删除知友失败", e);
            return ApiResponse.error("批量删除知友失败: " + e.getMessage());
        }
    }

    /**
     * 批量取消喜欢
     */
    @DeleteMapping("/likes/batch")
    public ApiResponse<String> removeLikesBatch(
            @RequestBody List<Long> targetUserIds,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            if (targetUserIds == null || targetUserIds.isEmpty()) {
                return ApiResponse.error("未选择要取消喜欢的用户");
            }

            int successCount = 0;
            for (Long targetUserId : targetUserIds) {
                boolean success = userRelationshipService.removeLike(userId, targetUserId);
                if (success) {
                    successCount++;
                }
            }

            log.info("批量取消喜欢完成 - userId: {}, 成功取消: {}/{}", userId, successCount, targetUserIds.size());
            return ApiResponse.success(String.format("成功取消%d个喜欢", successCount));

        } catch (Exception e) {
            log.error("批量取消喜欢失败", e);
            return ApiResponse.error("批量取消喜欢失败: " + e.getMessage());
        }
    }

    // ========== 订阅状态通知相关API ==========

    /**
     * 订阅用户状态通知
     */
    @PostMapping("/{targetUserId}/subscribe")
    public ApiResponse<String> subscribeUser(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.subscribeUser(userId, targetUserId);

            if (success) {
                log.info("订阅状态通知成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("订阅成功");
            } else {
                return ApiResponse.error("订阅失败");
            }

        } catch (Exception e) {
            log.error("订阅状态通知失败", e);
            return ApiResponse.error("订阅失败: " + e.getMessage());
        }
    }

    /**
     * 取消订阅用户状态通知
     */
    @DeleteMapping("/{targetUserId}/subscribe")
    public ApiResponse<String> unsubscribeUser(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.unsubscribeUser(userId, targetUserId);

            if (success) {
                log.info("取消订阅成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("取消订阅成功");
            } else {
                return ApiResponse.error("取消订阅失败");
            }

        } catch (Exception e) {
            log.error("取消订阅失败", e);
            return ApiResponse.error("取消订阅失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否已订阅
     */
    @GetMapping("/{targetUserId}/is-subscribed")
    public ApiResponse<Boolean> isSubscribed(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean isSubscribed = userRelationshipService.isSubscribed(userId, targetUserId);

            return ApiResponse.success(isSubscribed);

        } catch (Exception e) {
            log.error("检查订阅状态失败", e);
            return ApiResponse.error("检查订阅状态失败: " + e.getMessage());
        }
    }

    // ========== 备注相关API ==========

    /**
     * 设置用户备注
     */
    @PostMapping("/{targetUserId}/remark")
    public ApiResponse<String> setUserRemark(
            @PathVariable Long targetUserId,
            @RequestParam String remark,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.setUserRemark(userId, targetUserId, remark);

            if (success) {
                log.info("设置备注成功 - userId: {}, targetUserId: {}, remark: {}", userId, targetUserId, remark);
                return ApiResponse.success("备注设置成功");
            } else {
                return ApiResponse.error("设置备注失败");
            }

        } catch (Exception e) {
            log.error("设置备注失败", e);
            return ApiResponse.error("设置备注失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户备注
     */
    @GetMapping("/{targetUserId}/remark")
    public ApiResponse<String> getUserRemark(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            String remark = userRelationshipService.getUserRemark(userId, targetUserId);

            return ApiResponse.success(remark != null ? remark : "");

        } catch (Exception e) {
            log.error("获取备注失败", e);
            return ApiResponse.error("获取备注失败: " + e.getMessage());
        }
    }

    // ========== 黑名单相关API ==========

    /**
     * 加入黑名单
     */
    @PostMapping("/{targetUserId}/blacklist")
    public ApiResponse<String> addToBlacklist(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.addToBlacklist(userId, targetUserId);

            if (success) {
                log.info("加入黑名单成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("已加入黑名单");
            } else {
                return ApiResponse.error("加入黑名单失败");
            }

        } catch (Exception e) {
            log.error("加入黑名单失败", e);
            return ApiResponse.error("加入黑名单失败: " + e.getMessage());
        }
    }

    /**
     * 移出黑名单
     */
    @DeleteMapping("/{targetUserId}/blacklist")
    public ApiResponse<String> removeFromBlacklist(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userRelationshipService.removeFromBlacklist(userId, targetUserId);

            if (success) {
                log.info("移出黑名单成功 - userId: {}, targetUserId: {}", userId, targetUserId);
                return ApiResponse.success("已移出黑名单");
            } else {
                return ApiResponse.error("移出黑名单失败");
            }

        } catch (Exception e) {
            log.error("移出黑名单失败", e);
            return ApiResponse.error("移出黑名单失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否在黑名单
     */
    @GetMapping("/{targetUserId}/is-blacklisted")
    public ApiResponse<Boolean> isBlacklisted(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean isBlacklisted = userRelationshipService.isBlacklisted(userId, targetUserId);

            return ApiResponse.success(isBlacklisted);

        } catch (Exception e) {
            log.error("检查黑名单状态失败", e);
            return ApiResponse.error("检查黑名单状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取黑名单列表
     */
    @GetMapping("/blacklist")
    public ApiResponse<List<UserDTO>> getBlacklistUsers(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            List<UserDTO> blacklistedUsers = userRelationshipService.getBlacklistUsers(userId);

            log.info("获取黑名单列表成功 - userId: {}, 黑名单用户数: {}", userId, blacklistedUsers.size());
            return ApiResponse.success(blacklistedUsers);

        } catch (Exception e) {
            log.error("获取黑名单列表失败", e);
            return ApiResponse.error("获取黑名单列表失败: " + e.getMessage());
        }
    }

    /**
     * 批量移出黑名单
     */
    @DeleteMapping("/blacklist/batch")
    public ApiResponse<String> batchRemoveFromBlacklist(
            @RequestBody List<Long> targetUserIds,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            if (targetUserIds == null || targetUserIds.isEmpty()) {
                return ApiResponse.error("未选择要移出的用户");
            }

            int successCount = userRelationshipService.batchRemoveFromBlacklist(userId, targetUserIds);

            log.info("批量移出黑名单完成 - userId: {}, 成功移出: {}/{}", userId, successCount, targetUserIds.size());
            return ApiResponse.success(String.format("成功移出%d个用户", successCount));

        } catch (Exception e) {
            log.error("批量移出黑名单失败", e);
            return ApiResponse.error("批量移出黑名单失败: " + e.getMessage());
        }
    }

    /**
     * 查询账号状态
     */
    @GetMapping("/{targetUserId}/account-status")
    public ApiResponse<Map<String, Object>> getAccountStatus(
            @PathVariable Long targetUserId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractTokenFromHeader(authHeader);
            if (token == null) {
                return ApiResponse.error("未提供有效的认证令牌");
            }

            // 获取用户信息
            var user = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("userId", user.getId());
            statusInfo.put("username", user.getUsername());
            statusInfo.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");
            statusInfo.put("isOnline", user.getIsOnline());
            statusInfo.put("isVip", user.getIsVip());
            statusInfo.put("isVerified", false); // TODO: 实现认证逻辑
            statusInfo.put("accountAge", "正常"); // TODO: 根据创建时间计算
            statusInfo.put("message", "账号状态正常");

            log.info("查询账号状态 - userId: {}, targetUserId: {}", jwtUtil.getUserIdFromToken(token), targetUserId);
            return ApiResponse.success("查询成功", statusInfo);

        } catch (Exception e) {
            log.error("查询账号状态失败", e);
            return ApiResponse.error("查询账号状态失败: " + e.getMessage());
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
