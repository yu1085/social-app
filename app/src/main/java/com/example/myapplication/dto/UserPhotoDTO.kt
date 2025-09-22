package com.example.myapplication.dto

import java.time.LocalDateTime

/**
 * 用户照片DTO
 */
data class UserPhotoDTO(
    val id: Long? = null,
    val userId: Long,
    val photoUrl: String,
    val isAvatar: Boolean = false,
    val uploadedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null
)

/**
 * 上传照片响应DTO
 */
data class UploadPhotoResponse(
    val photoId: Long,
    val photoUrl: String,
    val isAvatar: Boolean
)
