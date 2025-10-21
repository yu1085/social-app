package com.socialmeet.backend.dto;

import com.socialmeet.backend.entity.Post;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private Long id;
    private Long userId;
    private String content;
    private List<String> images;
    private String location;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
    private Boolean isFreeMinute;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 用户信息
    private String userNickname;
    private String userAvatar;
    private String userGender;
    private Integer userAge;
    private String userLocation;
    private Boolean userIsVerified;
    private Boolean userIsVip;

    /**
     * 从实体转换为DTO（不包含用户信息）
     */
    public static PostDTO fromEntity(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .location(post.getLocation())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isFreeMinute(post.getIsFreeMinute())
                .status(post.getStatus() != null ? post.getStatus().name() : null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * 转换为实体
     */
    public Post toEntity() {
        return Post.builder()
                .id(this.id)
                .userId(this.userId)
                .content(this.content)
                .location(this.location)
                .likeCount(this.likeCount != null ? this.likeCount : 0)
                .commentCount(this.commentCount != null ? this.commentCount : 0)
                .isFreeMinute(this.isFreeMinute != null ? this.isFreeMinute : false)
                .status(this.status != null ? Post.PostStatus.valueOf(this.status) : Post.PostStatus.PUBLISHED)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
