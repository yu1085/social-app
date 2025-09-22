package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 处理各种类型的文件上传
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(originPatterns = "*")
public class FileUploadController {
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 上传图片
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证token
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("无效的认证令牌"));
            }
            
            // 验证文件类型
            if (!isImageFile(file)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("不支持的文件类型"));
            }
            
            // 验证文件大小 (最大5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件大小超过限制"));
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir, "images");
            Files.createDirectories(uploadPath);
            
            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL
            String fileUrl = "/uploads/images/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(ApiResponse.success("图片上传成功", result));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器错误: " + e.getMessage()));
        }
    }
    
    /**
     * 上传视频
     */
    @PostMapping("/video")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证token
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("无效的认证令牌"));
            }
            
            // 验证文件类型
            if (!isVideoFile(file)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("不支持的文件类型"));
            }
            
            // 验证文件大小 (最大50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件大小超过限制"));
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir, "videos");
            Files.createDirectories(uploadPath);
            
            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL
            String fileUrl = "/uploads/videos/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(ApiResponse.success("视频上传成功", result));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器错误: " + e.getMessage()));
        }
    }
    
    /**
     * 上传音频
     */
    @PostMapping("/audio")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证token
            if (!jwtUtil.validateToken(token.replace("Bearer ", ""))) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("无效的认证令牌"));
            }
            
            // 验证文件类型
            if (!isAudioFile(file)) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("不支持的文件类型"));
            }
            
            // 验证文件大小 (最大10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件大小超过限制"));
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir, "audios");
            Files.createDirectories(uploadPath);
            
            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL
            String fileUrl = "/uploads/audios/" + filename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", filename);
            result.put("size", String.valueOf(file.getSize()));
            
            return ResponseEntity.ok(ApiResponse.success("音频上传成功", result));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("服务器错误: " + e.getMessage()));
        }
    }
    
    /**
     * 检查是否为图片文件
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    /**
     * 检查是否为视频文件
     */
    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
    
    /**
     * 检查是否为音频文件
     */
    private boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("audio/");
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
