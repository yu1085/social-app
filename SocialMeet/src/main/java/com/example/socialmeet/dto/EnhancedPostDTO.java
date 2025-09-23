package com.example.socialmeet.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 增强的动态DTO，包含用户信息
 */
public class EnhancedPostDTO {
    private Long id;
    private Long userId;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private String location;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 用户信息
    private String userName;
    private String userAvatar;
    private Integer userAge;
    private Double distance;
    private String userStatus;
    private Boolean isOnline;
    
    // 评论列表
    private List<CommentDTO> comments;
    
    // 筛选相关
    private String publishTimeText;
    private Boolean isFreeMinute;
    
    // Constructors
    public EnhancedPostDTO() {}
    
    public EnhancedPostDTO(Long id, Long userId, String content, String imageUrl, String videoUrl, 
                          String location, Integer likeCount, Integer commentCount, Boolean isLiked, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.location = location;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public Boolean getIsLiked() {
        return isLiked;
    }
    
    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public Integer getUserAge() {
        return userAge;
    }
    
    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }
    
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public String getUserStatus() {
        return userStatus;
    }
    
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
    
    public Boolean getIsOnline() {
        return isOnline;
    }
    
    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    public List<CommentDTO> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
    
    public String getPublishTimeText() {
        return publishTimeText;
    }
    
    public void setPublishTimeText(String publishTimeText) {
        this.publishTimeText = publishTimeText;
    }
    
    public Boolean getIsFreeMinute() {
        return isFreeMinute;
    }
    
    public void setIsFreeMinute(Boolean isFreeMinute) {
        this.isFreeMinute = isFreeMinute;
    }
    
    /**
     * 评论DTO
     */
    public static class CommentDTO {
        private Long id;
        private Long userId;
        private String userName;
        private String userAvatar;
        private String content;
        private Long parentId;
        private Integer likeCount;
        private Boolean isLiked;
        private LocalDateTime createdAt;
        private List<CommentDTO> replies;
        
        public CommentDTO() {}
        
        public CommentDTO(Long id, Long userId, String userName, String userAvatar, String content, 
                         Long parentId, Integer likeCount, Boolean isLiked, LocalDateTime createdAt) {
            this.id = id;
            this.userId = userId;
            this.userName = userName;
            this.userAvatar = userAvatar;
            this.content = content;
            this.parentId = parentId;
            this.likeCount = likeCount;
            this.isLiked = isLiked;
            this.createdAt = createdAt;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public void setUserName(String userName) {
            this.userName = userName;
        }
        
        public String getUserAvatar() {
            return userAvatar;
        }
        
        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public Long getParentId() {
            return parentId;
        }
        
        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }
        
        public Integer getLikeCount() {
            return likeCount;
        }
        
        public void setLikeCount(Integer likeCount) {
            this.likeCount = likeCount;
        }
        
        public Boolean getIsLiked() {
            return isLiked;
        }
        
        public void setIsLiked(Boolean isLiked) {
            this.isLiked = isLiked;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public List<CommentDTO> getReplies() {
            return replies;
        }
        
        public void setReplies(List<CommentDTO> replies) {
            this.replies = replies;
        }
    }
}
