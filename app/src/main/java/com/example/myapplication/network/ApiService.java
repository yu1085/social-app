package com.example.myapplication.network;

import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.PostDTO;
import com.example.myapplication.dto.MessageDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // 发送验证码
    @POST("auth/send-code")
    Call<ApiResponse<String>> sendVerificationCode(@Query("phone") String phone);
    
    // 验证码登录/注册
    @POST("auth/login-with-code")
    Call<ApiResponse<LoginResponse>> loginWithVerificationCode(@Query("phone") String phone, @Query("code") String code);
    
    // 用户登录
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    // 获取用户信息
    @GET("users/profile")
    Call<ApiResponse<UserDTO>> getProfile(@Header("Authorization") String authHeader);
    
    // 更新用户信息
    @PUT("users/profile")
    Call<ApiResponse<UserDTO>> updateProfile(@Header("Authorization") String authHeader, @Body UserDTO user);
    
    // 搜索用户
    @GET("users/search")
    Call<ApiResponse<List<UserDTO>>> searchUsers(
            @Query("keyword") String keyword,
            @Query("gender") String gender,
            @Query("location") String location,
            @Query("minAge") Integer minAge,
            @Query("maxAge") Integer maxAge,
            @Query("page") int page,
            @Query("size") int size
    );
    
    // 获取用户详情
    @GET("users/{id}")
    Call<ApiResponse<UserDTO>> getUserById(@Path("id") Long id);
    
    // 关注用户
    @POST("users/follow/{userId}")
    Call<ApiResponse<String>> followUser(@Header("Authorization") String authHeader, @Path("userId") Long userId);
    
    // 取消关注
    @DELETE("users/follow/{userId}")
    Call<ApiResponse<String>> unfollowUser(@Header("Authorization") String authHeader, @Path("userId") Long userId);
    
    // 获取动态列表
    @GET("posts")
    Call<ApiResponse<List<PostDTO>>> getPosts(
            @Query("page") int page,
            @Query("size") int size
    );
    
    // 点赞动态
    @POST("posts/{postId}/like")
    Call<ApiResponse<PostDTO>> likePost(@Header("Authorization") String authHeader, @Path("postId") Long postId);
    
    // 取消点赞动态
    @POST("posts/{postId}/unlike")
    Call<ApiResponse<PostDTO>> unlikePost(@Header("Authorization") String authHeader, @Path("postId") Long postId);
    
    // 获取消息列表
    @GET("messages")
    Call<ApiResponse<List<MessageDTO>>> getMessages(@Header("Authorization") String authHeader);
    
    // 发送消息
    @POST("messages")
    Call<ApiResponse<MessageDTO>> sendMessage(@Header("Authorization") String authHeader, @Body MessageDTO message);
}
