package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.entity.UserPhoto;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.repository.UserPhotoRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class PhotoController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPhotoRepository userPhotoRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final String UPLOAD_DIR = "uploads/photos/";
    
    /**
     * 上传用户照片
     */
    @PostMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam(value = "isAvatar", defaultValue = "false") boolean isAvatar,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            // 从token中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long tokenUserId = jwtUtil.getUserIdFromToken(token);
            
            // 验证用户ID是否匹配
            if (!tokenUserId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权限访问此用户资源"));
            }
            
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // 保存文件
            Files.copy(photo.getInputStream(), filePath);
            
            // 保存到数据库
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUserId(id);
            userPhoto.setPhotoUrl("/uploads/photos/" + filename);
            userPhoto.setIsAvatar(isAvatar);
            userPhoto.setUploadedAt(LocalDateTime.now());
            userPhoto.setCreatedAt(LocalDateTime.now());
            
            UserPhoto savedPhoto = userPhotoRepository.save(userPhoto);
            
            // 如果是头像，更新用户头像URL
            if (isAvatar) {
                user.setAvatarUrl(savedPhoto.getPhotoUrl());
                userRepository.save(user);
            }
            
            Map<String, Object> result = Map.of(
                "photoId", savedPhoto.getId(),
                "photoUrl", savedPhoto.getPhotoUrl(),
                "isAvatar", savedPhoto.getIsAvatar()
            );
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("上传照片失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户照片列表
     */
    @GetMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<List<UserPhoto>>> getUserPhotos(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            // 从token中获取用户信息
            Long tokenUserId = jwtUtil.getUserIdFromToken(token);
            
            // 验证用户ID是否匹配
            if (!tokenUserId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权限访问此用户资源"));
            }
            
            List<UserPhoto> photos = userPhotoRepository.findByUserIdOrderByCreatedAtDesc(id);
            return ResponseEntity.ok(ApiResponse.success(photos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取照片列表失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除照片
     */
    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<ApiResponse<String>> deletePhoto(
            @PathVariable Long id,
            @PathVariable Long photoId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            // 从token中获取用户信息
            Long tokenUserId = jwtUtil.getUserIdFromToken(token);
            
            // 验证用户ID是否匹配
            if (!tokenUserId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权限访问此用户资源"));
            }
            
            UserPhoto photo = userPhotoRepository.findById(photoId)
                    .orElseThrow(() -> new RuntimeException("照片不存在"));
            
            if (!photo.getUserId().equals(id)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限删除此照片"));
            }
            
            // 删除文件
            try {
                Path filePath = Paths.get("." + photo.getPhotoUrl());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // 忽略文件删除错误
            }
            
            // 删除数据库记录
            userPhotoRepository.delete(photo);
            
            return ResponseEntity.ok(ApiResponse.success("照片删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除照片失败: " + e.getMessage()));
        }
    }
    
    /**
     * 设置头像
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<ApiResponse<String>> setAvatar(
            @PathVariable Long id,
            @RequestParam Long photoId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 验证JWT token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(ApiResponse.error("未授权访问"));
            }
            
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token无效或已过期"));
            }
            
            // 从token中获取用户信息
            Long tokenUserId = jwtUtil.getUserIdFromToken(token);
            
            // 验证用户ID是否匹配
            if (!tokenUserId.equals(id)) {
                return ResponseEntity.status(403).body(ApiResponse.error("无权限访问此用户资源"));
            }
            
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            UserPhoto photo = userPhotoRepository.findById(photoId)
                    .orElseThrow(() -> new RuntimeException("照片不存在"));
            
            if (!photo.getUserId().equals(id)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无权限设置此照片为头像"));
            }
            
            // 取消其他照片的头像状态
            List<UserPhoto> userPhotos = userPhotoRepository.findByUserIdAndIsAvatarTrue(id);
            for (UserPhoto userPhoto : userPhotos) {
                userPhoto.setIsAvatar(false);
                userPhotoRepository.save(userPhoto);
            }
            
            // 设置新头像
            photo.setIsAvatar(true);
            userPhotoRepository.save(photo);
            
            // 更新用户头像URL
            user.setAvatarUrl(photo.getPhotoUrl());
            userRepository.save(user);
            
            return ResponseEntity.ok(ApiResponse.success("头像设置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("设置头像失败: " + e.getMessage()));
        }
    }
}
