package com.example.myapplication.network;

import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.PostDTO;
import com.example.myapplication.dto.MessageDTO;
import com.example.myapplication.dto.WalletDTO;
import com.example.myapplication.dto.TransactionDTO;
import com.example.myapplication.dto.UserPhotoDTO;
import com.example.myapplication.dto.UploadPhotoResponse;
import com.example.myapplication.model.VipLevel;
import com.example.myapplication.model.VipSubscription;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import okhttp3.MultipartBody;

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
    
    // VIP相关 - 已移动到下方新的VIP API部分
    
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
    
    // 相册相关接口
    @GET("users/{id}/photos")
    Call<ApiResponse<List<UserPhotoDTO>>> getUserPhotos(@Path("id") Long userId, @Header("Authorization") String authHeader);
    
    @Multipart
    @POST("users/{id}/photos")
    Call<ApiResponse<UploadPhotoResponse>> uploadPhoto(
            @Path("id") Long userId,
            @Part MultipartBody.Part photo,
            @Query("isAvatar") boolean isAvatar,
            @Header("Authorization") String authHeader
    );
    
    @DELETE("users/{id}/photos/{photoId}")
    Call<ApiResponse<String>> deletePhoto(
            @Path("id") Long userId,
            @Path("photoId") Long photoId,
            @Header("Authorization") String authHeader
    );
    
    @PUT("users/{id}/photos/{photoId}/avatar")
    Call<ApiResponse<String>> setAsAvatar(
            @Path("id") Long userId,
            @Path("photoId") Long photoId,
            @Header("Authorization") String authHeader
    );
    
    // 身份证二要素核验相关接口
    
    @POST("auth/id-card/verify")
    Call<ApiResponse<Object>> verifyIdCard(
            @Header("Authorization") String authHeader,
            @Body java.util.Map<String, String> request
    );
    
    @POST("auth/id-card/submit")
    Call<ApiResponse<Object>> submitIdCardVerification(
            @Header("Authorization") String authHeader,
            @Body java.util.Map<String, String> request
    );
    
    @GET("auth/id-card/status")
    Call<ApiResponse<Object>> getVerificationStatus(
            @Header("Authorization") String authHeader
    );
    
    @GET("auth/id-card/result")
    Call<ApiResponse<Object>> getVerificationResult(
            @Header("Authorization") String authHeader
    );
    
        // 靓号相关API
        @GET("lucky-numbers/items")
        Call<ApiResponse<java.util.List<com.example.myapplication.model.LuckyNumber>>> getLuckyNumbers();
    
    @POST("lucky-numbers/purchase")
    Call<ApiResponse<com.example.myapplication.model.LuckyNumber>> purchaseLuckyNumber(
            @Header("Authorization") String authHeader,
            @Body java.util.Map<String, Object> request
    );
    
    // 财富等级相关API
    @GET("wealth-level/my-level")
    Call<ApiResponse<com.example.myapplication.model.WealthLevelData>> getMyWealthLevel(
            @Header("Authorization") String authHeader
    );
    
    @GET("wealth-level/progress")
    Call<ApiResponse<com.example.myapplication.model.WealthLevelData>> getWealthLevelProgress(
            @Header("Authorization") String authHeader
    );
    
    @GET("wealth-level/privileges")
    Call<ApiResponse<java.util.List<String>>> getUserPrivileges(
            @Header("Authorization") String authHeader
    );
    
    @GET("wealth-level/ranking")
    Call<ApiResponse<java.util.List<com.example.myapplication.model.WealthLevelData>>> getWealthRanking(
            @Query("limit") int limit
    );
    
    @GET("wealth-level/rules")
    Call<ApiResponse<java.util.List<com.example.myapplication.model.WealthLevelRule>>> getWealthLevelRules();
    
    // VIP会员相关API
    @GET("vip/levels")
    Call<ApiResponse<java.util.List<VipLevel>>> getVipLevels();
    
    @GET("vip/levels/{id}")
    Call<ApiResponse<VipLevel>> getVipLevelById(@Path("id") Long id);
    
    @POST("vip/subscribe")
    Call<ApiResponse<VipSubscription>> subscribeVip(
            @Header("Authorization") String authHeader,
            @Query("vipLevelId") Long vipLevelId
    );
    
    @GET("vip/current")
    Call<ApiResponse<VipSubscription>> getCurrentVipSubscription(
            @Header("Authorization") String authHeader
    );
    
    @GET("vip/history")
    Call<ApiResponse<java.util.List<VipSubscription>>> getVipHistory(
            @Header("Authorization") String authHeader
    );
    
    @GET("vip/check")
    Call<ApiResponse<Boolean>> checkVipStatus(
            @Header("Authorization") String authHeader
    );
    
    @GET("vip/level")
    Call<ApiResponse<Integer>> getVipLevel(
            @Header("Authorization") String authHeader
    );
    
    // 支付相关API
    @POST("vip/create-payment-order")
    Call<ApiResponse<com.example.myapplication.service.PaymentOrderData>> createPaymentOrder(
            @Header("Authorization") String authHeader,
            @Body java.util.Map<String, Object> request
    );
    
    @POST("payment/verify")
    Call<ApiResponse<String>> verifyPayment(
            @Header("Authorization") String authHeader,
            @Body java.util.Map<String, String> request
    );
}
