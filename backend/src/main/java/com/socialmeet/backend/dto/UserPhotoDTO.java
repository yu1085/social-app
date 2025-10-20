package com.socialmeet.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户照片DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPhotoDTO {

    private Long id;
    private Long userId;
    private String photoUrl;
    private Boolean isAvatar;
    private LocalDateTime uploadTime;
}
