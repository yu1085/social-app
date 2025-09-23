package com.example.myapplication.dto

import java.time.LocalDateTime

data class EnhancedPostDTO(
    val id: Long? = null,
    val userId: Long? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val userAge: Int? = null,
    val userStatus: String? = null,
    val isOnline: Boolean? = null,
    val distance: Double? = null,
    val content: String? = null,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val location: String? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean? = null,
    val publishTimeText: String? = null,
    val isFreeMinute: Boolean? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val comments: List<CommentDTO> = emptyList()
) {
    data class CommentDTO(
        val id: Long? = null,
        val userId: Long? = null,
        val userName: String? = null,
        val userAvatar: String? = null,
        val content: String? = null,
        val parentId: Long? = null,
        val likeCount: Int = 0,
        val isLiked: Boolean? = null,
        val createdAt: LocalDateTime? = null
    )
}
