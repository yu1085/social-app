package com.example.myapplication.dto;

import java.util.List;

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
    private String createdAt;
    private String updatedAt;

    // 用户信息
    private String userNickname;
    private String userAvatar;
    private String userGender;
    private Integer userAge;
    private String userLocation;
    private Boolean userIsVerified;
    private Boolean userIsVip;

    public PostDTO() {}

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public Boolean getIsFreeMinute() {
        return isFreeMinute;
    }

    public void setIsFreeMinute(Boolean isFreeMinute) {
        this.isFreeMinute = isFreeMinute;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public Boolean getUserIsVerified() {
        return userIsVerified;
    }

    public void setUserIsVerified(Boolean userIsVerified) {
        this.userIsVerified = userIsVerified;
    }

    public Boolean getUserIsVip() {
        return userIsVip;
    }

    public void setUserIsVip(Boolean userIsVip) {
        this.userIsVip = userIsVip;
    }
}
