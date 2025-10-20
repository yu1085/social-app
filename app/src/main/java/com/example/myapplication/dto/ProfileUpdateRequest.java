package com.example.myapplication.dto;

import java.time.LocalDate;

/**
 * 个人资料更新请求DTO
 */
public class ProfileUpdateRequest {
    
    private String nickname;
    private String gender;
    private String avatarUrl;
    
    private LocalDate birthday;
    
    private String location;
    private String signature;
    private Integer height;
    private Integer weight;
    private String incomeLevel;
    private String education;
    private String maritalStatus;
    
    // 构造函数
    public ProfileUpdateRequest() {}
    
    public ProfileUpdateRequest(String nickname, String gender, String avatarUrl, LocalDate birthday, 
                              String location, String signature, Integer height, Integer weight, 
                              String incomeLevel, String education, String maritalStatus) {
        this.nickname = nickname;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.birthday = birthday;
        this.location = location;
        this.signature = signature;
        this.height = height;
        this.weight = weight;
        this.incomeLevel = incomeLevel;
        this.education = education;
        this.maritalStatus = maritalStatus;
    }
    
    // Getter和Setter方法
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
    
    public String getIncomeLevel() { return incomeLevel; }
    public void setIncomeLevel(String incomeLevel) { this.incomeLevel = incomeLevel; }
    
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
}
