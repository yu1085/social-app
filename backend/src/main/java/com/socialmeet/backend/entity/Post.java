package com.socialmeet.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 动态/帖子实体
 */
@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发布者用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 动态内容
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 图片URL列表 (JSON格式存储)
     */
    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    /**
     * 位置信息
     */
    @Column(name = "location", length = 255)
    private String location;

    /**
     * 点赞数
     */
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    /**
     * 评论数
     */
    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    /**
     * 查看数
     */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    /**
     * 是否为"一分钟免费"动态
     */
    @Column(name = "is_free_minute", nullable = false)
    @Builder.Default
    private Boolean isFreeMinute = false;

    /**
     * 动态状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.PUBLISHED;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 动态状态枚举
     */
    public enum PostStatus {
        PUBLISHED,      // 已发布
        DELETED,        // 已删除
        HIDDEN          // 已隐藏
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
