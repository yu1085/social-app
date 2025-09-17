package com.example.socialmeet.dto;

import com.example.socialmeet.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Double latitude;
    private Double longitude;
    private Integer age;
    private Integer height;
    private Integer weight;
    private String education;
    private String income;
    private Boolean isOnline;
    private LocalDateTime lastSeen;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    
    public UserDTO(User user) {
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
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.age = user.getAge();
        this.height = user.getHeight();
        this.weight = user.getWeight();
        this.education = user.getEducation();
        this.income = user.getIncome();
        this.isOnline = user.getIsOnline();
        this.lastSeen = user.getLastSeen();
        this.isVerified = user.getIsVerified();
        this.createdAt = user.getCreatedAt();
    }
    
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
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
