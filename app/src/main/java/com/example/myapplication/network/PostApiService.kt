package com.example.myapplication.network

import com.example.myapplication.dto.EnhancedPostDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * 动态相关API服务
 */
interface PostApiService {
    
    /**
     * 获取增强的动态列表
     */
    @GET("api/posts/enhanced")
    suspend fun getEnhancedPosts(
        @Header("Authorization") token: String,
        @Query("filter") filter: String = "nearby",
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PageResponse<EnhancedPostDTO>>>
    
    /**
     * 点赞/取消点赞动态
     */
    @POST("api/posts/{id}/toggle-like")
    suspend fun toggleLikePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Long
    ): Response<ApiResponse<EnhancedPostDTO>>
    
    /**
     * 添加评论
     */
    @POST("api/posts/{id}/comments")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") postId: Long,
        @Query("content") content: String,
        @Query("parentId") parentId: Long? = null
    ): Response<ApiResponse<EnhancedPostDTO>>
    
    /**
     * 获取动态评论
     */
    @GET("api/posts/{id}/comments")
    suspend fun getPostComments(
        @Header("Authorization") token: String,
        @Path("id") postId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<List<EnhancedPostDTO.CommentDTO>>>
    
    /**
     * 删除评论
     */
    @DELETE("api/posts/comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: Long
    ): Response<ApiResponse<String>>
}

/**
 * API响应包装类
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

/**
 * 分页响应类
 */
data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int
)

/**
 * 增强的动态DTO
 */
data class EnhancedPostDTO(
    val id: Long,
    val userId: Long,
    val content: String,
    val imageUrl: String?,
    val videoUrl: String?,
    val location: String?,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val userName: String?,
    val userAvatar: String?,
    val userAge: Int?,
    val distance: Double?,
    val userStatus: String?,
    val isOnline: Boolean?,
    val publishTimeText: String?,
    val isFreeMinute: Boolean?,
    val comments: List<CommentDTO>? = null
) {
    data class CommentDTO(
        val id: Long,
        val userId: Long,
        val userName: String?,
        val userAvatar: String?,
        val content: String,
        val parentId: Long?,
        val likeCount: Int,
        val isLiked: Boolean,
        val createdAt: String,
        val replies: List<CommentDTO>? = null
    )
}
