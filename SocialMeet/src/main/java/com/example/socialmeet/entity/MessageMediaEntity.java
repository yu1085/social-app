package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 消息媒体文件实体类 - 管理消息中的媒体文件信息
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "message_media")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MessageMediaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "media_type", nullable = false, length = 20)
    private String mediaType; // 媒体类型：image, video, audio, file
    
    @Column(name = "file_name", length = 255)
    private String fileName; // 原始文件名
    
    @Column(name = "file_url", length = 500, nullable = false)
    private String fileUrl; // 文件URL
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl; // 缩略图URL
    
    @Column(name = "file_size")
    private Long fileSize; // 文件大小（字节）
    
    @Column(name = "duration")
    private Integer duration; // 媒体时长（秒）
    
    @Column(name = "width")
    private Integer width; // 图片/视频宽度
    
    @Column(name = "height")
    private Integer height; // 图片/视频高度
    
    @Column(name = "mime_type", length = 100)
    private String mimeType; // MIME类型
    
    @Column(name = "file_hash", length = 64)
    private String fileHash; // 文件哈希值
    
    @Column(name = "is_compressed", nullable = false)
    private Boolean isCompressed = false; // 是否已压缩
    
    @Column(name = "compression_ratio")
    private Double compressionRatio; // 压缩比例
    
    @Column(name = "upload_progress", nullable = false)
    private Integer uploadProgress = 0; // 上传进度（0-100）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private UploadStatus uploadStatus = UploadStatus.PENDING;
    
    @Column(name = "upload_time")
    private LocalDateTime uploadTime; // 上传完成时间
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime; // 文件过期时间
    
    @Column(name = "download_count", nullable = false)
    private Integer downloadCount = 0; // 下载次数
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; // 是否已删除
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 关联消息信息（不存储，通过查询获取）
    @Transient
    private MessageEntity message;
    
    // 上传状态枚举
    public enum UploadStatus {
        PENDING("等待上传"),
        UPLOADING("上传中"),
        COMPLETED("上传完成"),
        FAILED("上传失败"),
        CANCELLED("上传取消");
        
        private final String description;
        
        UploadStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 获取文件大小显示文本
     */
    public String getFileSizeText() {
        if (fileSize == null || fileSize == 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.1f %s", size, units[unitIndex]);
    }
    
    /**
     * 获取时长显示文本
     */
    public String getDurationText() {
        if (duration == null || duration == 0) {
            return "0秒";
        }
        
        int hours = duration / 3600;
        int minutes = (duration % 3600) / 60;
        int seconds = duration % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * 检查文件是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 标记上传完成
     */
    public void markUploadCompleted() {
        this.uploadStatus = UploadStatus.COMPLETED;
        this.uploadProgress = 100;
        this.uploadTime = LocalDateTime.now();
    }
    
    /**
     * 标记上传失败
     */
    public void markUploadFailed() {
        this.uploadStatus = UploadStatus.FAILED;
    }
    
    /**
     * 更新上传进度
     */
    public void updateUploadProgress(int progress) {
        this.uploadProgress = Math.max(0, Math.min(100, progress));
        if (progress > 0) {
            this.uploadStatus = UploadStatus.UPLOADING;
        }
    }
}
