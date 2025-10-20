package com.socialmeet.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    @Value("${file.upload.avatar-path:uploads/avatars/}")
    private String avatarUploadPath;

    @Value("${server.host:localhost}")
    private String serverHost;

    @Value("${server.port:8080}")
    private String serverPort;

    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    // 最大文件大小：5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 验证文件
        validateImageFile(file);

        // 确保上传目录存在
        File uploadDir = new File(avatarUploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String newFilename = String.format("avatar_%d_%s_%s.%s", userId, timestamp, uniqueId, extension);

        // 保存文件
        Path targetPath = Paths.get(avatarUploadPath, newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回访问URL
        String avatarUrl = String.format("http://%s:%s/uploads/avatars/%s",
                serverHost, serverPort, newFilename);

        log.info("头像上传成功 - userId: {}, filename: {}, url: {}", userId, newFilename, avatarUrl);
        return avatarUrl;
    }

    /**
     * 删除头像文件
     */
    public boolean deleteAvatar(String avatarUrl) {
        try {
            if (avatarUrl == null || avatarUrl.isEmpty()) {
                return true;
            }

            // 从URL中提取文件名
            String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(avatarUploadPath, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("头像删除成功 - filename: {}", filename);
                return true;
            }
        } catch (Exception e) {
            log.error("删除头像失败 - url: {}", avatarUrl, e);
        }
        return false;
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IOException("文件大小不能超过5MB");
        }

        // 检查文件扩展名
        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IOException("不支持的图片格式，仅支持: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }

        // 检查MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("文件必须是图片格式");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
