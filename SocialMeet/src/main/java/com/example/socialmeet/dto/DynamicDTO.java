package com.example.socialmeet.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态数据传输对象
 */
@Data
public class DynamicDTO {
    
    private Long id;
    private Long userId;
    private String content;
    private List<String> images;
    private String location;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private String status;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据
    private UserDTO user;
    private String userNickname;
    private String userAvatar;
    private Boolean isLiked;
    private Boolean isFreeMinute;
    
    // 构造函数
    public DynamicDTO() {}
    
    public DynamicDTO(Long id, Long userId, String content, List<String> images, 
                     String location, Integer likeCount, Integer commentCount, 
                     Integer viewCount, String status, LocalDateTime publishTime) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.images = images;
        this.location = location;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.status = status;
        this.publishTime = publishTime;
    }
}
