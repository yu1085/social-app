package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 动态实体
 */
@Entity
@Table(name = "dynamics")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Dynamic {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON格式存储图片URL列表
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "PUBLISHED"; // PUBLISHED, DRAFT, DELETED
    
    @Column(name = "publish_time", nullable = false)
    private LocalDateTime publishTime;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user;
    
    // 是否已点赞（不存储，通过查询获取）
    @Transient
    private Boolean isLiked = false;
    
    // 是否免费1分钟（不存储，通过查询获取）
    @Transient
    private Boolean isFreeMinute = false;
}
