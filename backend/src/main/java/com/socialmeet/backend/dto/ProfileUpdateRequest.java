package com.socialmeet.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 个人资料更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {

    private String nickname;
    private String gender;
    private String avatarUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    
    private String location;
    private String signature;
    private Integer height;
    private Integer weight;
    private String incomeLevel;
    private String education;
    private String maritalStatus;
}
