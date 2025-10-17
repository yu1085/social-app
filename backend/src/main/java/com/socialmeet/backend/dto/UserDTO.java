package com.socialmeet.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.socialmeet.backend.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 * 用于API响应，不包含敏感信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String constellation;
    private String location;
    private Integer height;
    private Integer weight;
    private String incomeLevel;
    private String education;
    private String maritalStatus;
    private String signature;
    private Boolean isVerified;
    private Boolean isVip;
    private Integer vipLevel;
    private Integer wealthLevel;
    private Double balance;
    private Boolean isOnline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastActiveAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 从User实体转换为UserDTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setBirthday(user.getBirthday());
        dto.setConstellation(user.getConstellation());
        dto.setLocation(user.getLocation());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setIncomeLevel(user.getIncomeLevel());
        dto.setEducation(user.getEducation());
        dto.setMaritalStatus(user.getMaritalStatus());
        dto.setSignature(user.getSignature());
        dto.setIsVerified(user.getIsVerified());
        dto.setIsVip(user.getIsVip());
        dto.setVipLevel(user.getVipLevel());
        dto.setWealthLevel(user.getWealthLevel());
        dto.setBalance(user.getBalance() != null ? user.getBalance().doubleValue() : 0.0);
        dto.setIsOnline(user.getIsOnline());
        dto.setLastActiveAt(user.getLastActiveAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }
}
