package com.example.myapplication.network;

import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.PostDTO;
import com.example.myapplication.dto.MessageDTO;
import com.example.myapplication.dto.WalletDTO;
import com.example.myapplication.dto.TransactionDTO;

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
    
    // 健康检查
    @GET("health")
    Call<Object> getHealth();
    
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
    
    // 获取未读消息数量
    @GET("messages/unread-count")
    Call<ApiResponse<Integer>> getUnreadCount(@Header("Authorization") String authHeader);
    
    // 钱包相关
    @GET("wallet/balance")
    Call<ApiResponse<WalletDTO>> getWalletBalance(@Header("Authorization") String authHeader);
    
    @POST("wallet/recharge")
    Call<ApiResponse<WalletDTO>> rechargeWallet(@Header("Authorization") String authHeader, @Body RechargeRequest rechargeRequest);
    
    @GET("wallet/transactions")
    Call<ApiResponse<List<TransactionDTO>>> getWalletTransactions(@Header("Authorization") String authHeader);
    
    // 用户相关
    @GET("users/home-cards")
    Call<ApiResponse<List<com.example.myapplication.model.UserCard>>> getHomeUserCards(@Query("page") int page, @Query("size") int size);
    
    @GET("users/{id}/detail")
    Call<ApiResponse<com.example.myapplication.model.UserCard>> getUserDetail(@Path("id") long userId);
    
    @GET("users/search")
    Call<ApiResponse<List<com.example.myapplication.model.UserCard>>> searchUsers(
        @Query("keyword") String keyword,
        @Query("location") String location,
        @Query("gender") String gender,
        @Query("page") int page,
        @Query("size") int size
    );
    
    // 通话相关
    @POST("call/initiate")
    Call<ApiResponse<Object>> initiateCall(@Header("Authorization") String authHeader, @Body Object callRequest);
    
    @POST("call/accept")
    Call<ApiResponse<Object>> acceptCall(@Header("Authorization") String authHeader, @Body Object acceptRequest);
    
    @POST("call/reject")
    Call<ApiResponse<Object>> rejectCall(@Header("Authorization") String authHeader, @Body Object rejectRequest);
    
    @POST("call/end")
    Call<ApiResponse<Object>> endCall(@Header("Authorization") String authHeader, @Body Object endRequest);
    
    @GET("call/status/{callSessionId}")
    Call<ApiResponse<Object>> getCallStatus(@Header("Authorization") String authHeader, @Path("callSessionId") String callSessionId);
    
    @GET("call/history")
    Call<ApiResponse<Object>> getCallHistory(@Header("Authorization") String authHeader, @Query("page") int page, @Query("size") int size);
    
    // 充值请求类
    class RechargeRequest {
        private java.math.BigDecimal amount;
        private String paymentMethod;
        
        public RechargeRequest(java.math.BigDecimal amount, String paymentMethod) {
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }
        
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    // VIP相关
    @GET("vip/levels")
    Call<ApiResponse<List<Object>>> getVipLevels();
    
    @GET("vip/current")
    Call<ApiResponse<Object>> getCurrentVip(@Header("Authorization") String authHeader);
    
    @POST("vip/subscribe")
    Call<ApiResponse<Object>> subscribeVip(@Header("Authorization") String authHeader, @Body Object vipRequest);
    
    // 财富等级相关
    @GET("wealth/user-level")
    Call<ApiResponse<Object>> getUserWealthLevel(@Header("Authorization") String authHeader);
    
    @GET("wealth/levels")
    Call<ApiResponse<List<Object>>> getWealthLevels();
    
    // 优惠券相关
    @GET("coupons/my")
    Call<ApiResponse<List<Object>>> getMyCoupons(@Header("Authorization") String authHeader);
    
    // 守护相关
    @GET("guard/guardians")
    Call<ApiResponse<List<Object>>> getMyGuardians(@Header("Authorization") String authHeader);
    
    @GET("guard/protected")
    Call<ApiResponse<List<Object>>> getProtectedUsers(@Header("Authorization") String authHeader);
    
    // 访客记录相关
    @GET("stats/views")
    Call<ApiResponse<List<Object>>> getMyVisitors(@Header("Authorization") String authHeader);
    
    // 用户设置相关
    @GET("users/settings")
    Call<ApiResponse<Object>> getUserSettings(@Header("Authorization") String authHeader);
    
    @PUT("users/settings")
    Call<ApiResponse<Object>> updateUserSettings(@Header("Authorization") String authHeader, @Body Object settings);
    
    @GET("users/settings/call")
    Call<ApiResponse<Object>> getCallSettings(@Header("Authorization") String authHeader);
    
    @PUT("users/settings/call")
    Call<ApiResponse<Object>> updateCallSettings(@Header("Authorization") String authHeader, @Body Object settings);
    
    // 邀请好友相关
    @GET("users/invite/code")
    Call<ApiResponse<Object>> getMyInviteCode(@Header("Authorization") String authHeader);
    
    @POST("users/invite/use")
    Call<ApiResponse<String>> useInviteCode(@Header("Authorization") String authHeader, @Query("inviteCode") String inviteCode);
    
    @GET("users/invite/records")
    Call<ApiResponse<List<Object>>> getInviteRecords(@Header("Authorization") String authHeader);
    
    @GET("users/invite/stats")
    Call<ApiResponse<Object>> getInviteStats(@Header("Authorization") String authHeader);
    
    // 用户认证相关
    @GET("users/certification")
    Call<ApiResponse<Object>> getMyCertification(@Header("Authorization") String authHeader);
    
    @POST("users/certification")
    Call<ApiResponse<Object>> submitCertification(@Header("Authorization") String authHeader, @Body Object certification);
    
    @PUT("users/certification")
    Call<ApiResponse<Object>> updateCertification(@Header("Authorization") String authHeader, @Body Object certification);
    
    @GET("users/certification/history")
    Call<ApiResponse<List<Object>>> getCertificationHistory(@Header("Authorization") String authHeader);
    
    // 商城相关
    @GET("shop/items")
    Call<ApiResponse<List<Object>>> getShopItems(@Query("category") String category, @Query("isLimited") Boolean isLimited);
    
    @GET("shop/items/{id}")
    Call<ApiResponse<Object>> getShopItem(@Path("id") Long id);
    
    @GET("shop/categories")
    Call<ApiResponse<List<String>>> getShopCategories();
    
    @GET("shop/search")
    Call<ApiResponse<List<Object>>> searchShopItems(@Query("keyword") String keyword);
}
