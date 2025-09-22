package com.example.socialmeet.dto;

import com.example.socialmeet.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 */
@Data
public class UserDTO {
    
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String gender;
    private LocalDateTime birthDate;
    private String bio;
    private String location;
    private Integer age;
    private Boolean isOnline;
    private String status;
    private LocalDateTime lastSeen;
    private Integer callPrice;
    private Integer messagePrice;
    private Boolean videoCallEnabled;
    private Boolean voiceCallEnabled;
    private Boolean messageChargeEnabled;
    private String city;
    private String hometown;
    private Integer beautyScore;
    private Double reviewScore;
    private Integer followerCount;
    private Integer likeCount;
    private Double latitude;
    private Double longitude;
    private Integer height;
    private Integer weight;
    private String education;
    private String income;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 新增的社交应用字段
    private String realName;
    private String zodiacSign;
    private String occupation;
    private String relationshipStatus;
    private String residenceStatus;
    private Boolean houseOwnership;
    private Boolean carOwnership;
    private String hobbies;
    private String languages;
    private String bloodType;
    private Boolean smoking;
    private Boolean drinking;
    private String tags;
    private LocalDateTime lastLoginAt;
    
    // 构造函数
    public UserDTO() {}
    
    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.phone = user.getPhone();
            this.email = user.getEmail();
            this.avatarUrl = user.getAvatarUrl();
            this.gender = user.getGender();
            this.birthDate = user.getBirthDate();
            this.bio = user.getBio();
            this.location = user.getLocation();
            this.age = user.getAge();
            this.isOnline = user.getIsOnline();
            this.status = user.getStatus();
            this.lastSeen = user.getLastSeen();
            this.callPrice = user.getCallPrice();
            this.messagePrice = user.getMessagePrice();
            this.videoCallEnabled = user.getVideoCallEnabled();
            this.voiceCallEnabled = user.getVoiceCallEnabled();
            this.messageChargeEnabled = user.getMessageChargeEnabled();
            this.city = user.getCity();
            this.hometown = user.getHometown();
            this.beautyScore = user.getBeautyScore();
            this.reviewScore = user.getReviewScore();
            this.followerCount = user.getFollowerCount();
            this.likeCount = user.getLikeCount();
            this.latitude = user.getLatitude();
            this.longitude = user.getLongitude();
            this.height = user.getHeight();
            this.weight = user.getWeight();
            this.education = user.getEducation();
            this.income = user.getIncome();
            this.isVerified = user.getIsVerified();
            this.isActive = user.getIsActive();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
            
            // 新增字段
            this.realName = user.getRealName();
            this.zodiacSign = user.getZodiacSign();
            this.occupation = user.getOccupation();
            this.relationshipStatus = user.getRelationshipStatus();
            this.residenceStatus = user.getResidenceStatus();
            this.houseOwnership = user.getHouseOwnership();
            this.carOwnership = user.getCarOwnership();
            this.hobbies = user.getHobbies();
            this.languages = user.getLanguages();
            this.bloodType = user.getBloodType();
            this.smoking = user.getSmoking();
            this.drinking = user.getDrinking();
            this.tags = user.getTags();
            this.lastLoginAt = user.getLastLoginAt();
        }
    }
}