package com.socialmeet.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 上传照片响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadPhotoResponse {

    private Long photoId;
    private String photoUrl;
    private Boolean isAvatar;
    private String message;
}
