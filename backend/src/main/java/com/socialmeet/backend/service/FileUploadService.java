package com.socialmeet.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload.base-dir:uploads}")
    private String baseUploadDir;

    @Value("${file.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
    private static final String[] ALLOWED_VIDEO_TYPES = {"video/mp4", "video/mpeg", "video/quicktime"};

    /**
     * 上传图片
     *
     * @param file     上传的文件
     * @param userId   用户ID
     * @return 图片URL
     */
    public String uploadImage(MultipartFile file, Long userId) throws IOException {
        return uploadFile(file, userId, "images", ALLOWED_IMAGE_TYPES);
    }

    /**
     * 上传视频
     *
     * @param file     上传的文件
     * @param userId   用户ID
     * @return 视频URL
     */
    public String uploadVideo(MultipartFile file, Long userId) throws IOException {
        return uploadFile(file, userId, "videos", ALLOWED_VIDEO_TYPES);
    }

    /**
     * 通用文件上传方法
     *
     * @param file         上传的文件
     * @param userId       用户ID
     * @param category     分类（images/videos）
     * @param allowedTypes 允许的文件类型
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file, Long userId, String category, String[] allowedTypes) throws IOException {
        // 1. 验证文件
        validateFile(file, allowedTypes);

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = generateFilename(userId, extension);

        // 3. 创建目录结构: uploads/images/2025/01/15/
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = category + "/" + dateDir + "/" + filename;
        Path uploadPath = Paths.get(baseUploadDir, category, dateDir);

        // 4. 创建目录
        Files.createDirectories(uploadPath);

        // 5. 保存文件
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("文件上传成功: userId={}, path={}", userId, relativePath);

        // 6. 返回访问URL
        return baseUrl + "/" + relativePath;
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String[] allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        boolean typeAllowed = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                typeAllowed = true;
                break;
            }
        }

        if (!typeAllowed) {
            throw new IllegalArgumentException("不支持的文件类型: " + contentType);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }

        return "";
    }

    /**
     * 生成唯一文件名
     */
    private String generateFilename(Long userId, String extension) {
        return userId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(baseUploadDir, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", relativePath);
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
        }
    }
}
