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
    private User.Gender gender;
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
}
