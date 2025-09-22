package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    @Id
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(length = 100)
    private String password;
    
    @Column(length = 100)
    private String nickname;
    
    @Column(unique = true, length = 20)
    private String phone;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    
    @Column(length = 500)
    private String bio;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "height")
    private Integer height; // 身高(cm)
    
    @Column(name = "weight")
    private Integer weight; // 体重(kg)
    
    @Column(name = "education", length = 50)
    private String education; // 学历
    
    @Column(name = "income", length = 50)
    private String income; // 收入
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "status", length = 20)
    private String status = "ONLINE"; // 状态：ONLINE(在线/空闲), BUSY(忙), OFFLINE(离线)
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 新增的增强功能字段
    @Column(name = "call_price")
    private Integer callPrice = 0; // 通话价格（分）
    
    @Column(name = "message_price")
    private Integer messagePrice = 0; // 消息价格（分）
    
    @Column(name = "video_call_enabled")
    private Boolean videoCallEnabled = false; // 视频通话开关
    
    @Column(name = "voice_call_enabled")
    private Boolean voiceCallEnabled = false; // 语音通话开关
    
    @Column(name = "message_charge_enabled")
    private Boolean messageChargeEnabled = false; // 消息收费开关
    
    @Column(name = "beauty_score")
    private Integer beautyScore = 0; // 颜值评分
    
    @Column(name = "review_score")
    private Double reviewScore = 0.0; // 综合评分
    
    @Column(name = "follower_count")
    private Integer followerCount = 0; // 粉丝数
    
    @Column(name = "like_count")
    private Integer likeCount = 0; // 获赞数
    
    @Column(name = "city", length = 50)
    private String city; // 城市
    
    @Column(name = "hometown", length = 50)
    private String hometown; // 家乡
    
    // 新增的社交应用字段
    @Column(name = "real_name", length = 50)
    private String realName; // 真实姓名
    
    @Column(name = "zodiac_sign", length = 20)
    private String zodiacSign; // 星座
    
    @Column(name = "occupation", length = 100)
    private String occupation; // 职业
    
    @Column(name = "relationship_status", length = 50)
    private String relationshipStatus; // 情感状态
    
    @Column(name = "residence_status", length = 50)
    private String residenceStatus; // 居住情况
    
    @Column(name = "house_ownership")
    private Boolean houseOwnership = false; // 是否购房
    
    @Column(name = "car_ownership")
    private Boolean carOwnership = false; // 是否购车
    
    @Column(name = "hobbies", length = 500)
    private String hobbies; // 兴趣爱好
    
    @Column(name = "languages", length = 200)
    private String languages; // 掌握语言（JSON字符串）
    
    @Column(name = "blood_type", length = 10)
    private String bloodType; // 血型
    
    @Column(name = "smoking")
    private Boolean smoking = false; // 是否吸烟
    
    @Column(name = "drinking")
    private Boolean drinking = false; // 是否饮酒
    
    @Column(name = "tags", length = 1000)
    private String tags; // 兴趣标签（JSON字符串）
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt; // 最后登录时间
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public LocalDateTime getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getIncome() {
        return income;
    }
    
    public void setIncome(String income) {
        this.income = income;
    }
    
    public Boolean getIsOnline() {
        return isOnline;
    }
    
    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    // 新增字段的getter和setter方法
    public Integer getCallPrice() {
        return callPrice;
    }
    
    public void setCallPrice(Integer callPrice) {
        this.callPrice = callPrice;
    }
    
    public Integer getMessagePrice() {
        return messagePrice;
    }
    
    public void setMessagePrice(Integer messagePrice) {
        this.messagePrice = messagePrice;
    }
    
    public Boolean getVideoCallEnabled() {
        return videoCallEnabled;
    }
    
    public void setVideoCallEnabled(Boolean videoCallEnabled) {
        this.videoCallEnabled = videoCallEnabled;
    }
    
    public Boolean getVoiceCallEnabled() {
        return voiceCallEnabled;
    }
    
    public void setVoiceCallEnabled(Boolean voiceCallEnabled) {
        this.voiceCallEnabled = voiceCallEnabled;
    }
    
    public Boolean getMessageChargeEnabled() {
        return messageChargeEnabled;
    }
    
    public void setMessageChargeEnabled(Boolean messageChargeEnabled) {
        this.messageChargeEnabled = messageChargeEnabled;
    }
    
    public Integer getBeautyScore() {
        return beautyScore;
    }
    
    public void setBeautyScore(Integer beautyScore) {
        this.beautyScore = beautyScore;
    }
    
    public Double getReviewScore() {
        return reviewScore;
    }
    
    public void setReviewScore(Double reviewScore) {
        this.reviewScore = reviewScore;
    }
    
    public Integer getFollowerCount() {
        return followerCount;
    }
    
    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getHometown() {
        return hometown;
    }
    
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
    
    // 新增字段的getter和setter方法
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getZodiacSign() {
        return zodiacSign;
    }
    
    public void setZodiacSign(String zodiacSign) {
        this.zodiacSign = zodiacSign;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getRelationshipStatus() {
        return relationshipStatus;
    }
    
    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }
    
    public String getResidenceStatus() {
        return residenceStatus;
    }
    
    public void setResidenceStatus(String residenceStatus) {
        this.residenceStatus = residenceStatus;
    }
    
    public Boolean getHouseOwnership() {
        return houseOwnership;
    }
    
    public void setHouseOwnership(Boolean houseOwnership) {
        this.houseOwnership = houseOwnership;
    }
    
    public Boolean getCarOwnership() {
        return carOwnership;
    }
    
    public void setCarOwnership(Boolean carOwnership) {
        this.carOwnership = carOwnership;
    }
    
    public String getHobbies() {
        return hobbies;
    }
    
    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }
    
    public String getLanguages() {
        return languages;
    }
    
    public void setLanguages(String languages) {
        this.languages = languages;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    
    public Boolean getSmoking() {
        return smoking;
    }
    
    public void setSmoking(Boolean smoking) {
        this.smoking = smoking;
    }
    
    public Boolean getDrinking() {
        return drinking;
    }
    
    public void setDrinking(Boolean drinking) {
        this.drinking = drinking;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
