package com.example.myapplication.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    
    private static AuthManager instance;
    private SharedPreferences prefs;
    
    private AuthManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void saveToken(String token) {
        Log.d(TAG, "保存Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        prefs.edit().putString(KEY_TOKEN, token).apply();
        
        // 设置Token过期时间（假设24小时）
        long expiryTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24);
        prefs.edit().putLong(KEY_TOKEN_EXPIRY, expiryTime).apply();
        Log.d(TAG, "Token过期时间设置为: " + new Date(expiryTime));
    }
    
    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "获取Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        return token;
    }
    
    public void saveRefreshToken(String refreshToken) {
        Log.d(TAG, "保存刷新Token: " + (refreshToken != null ? refreshToken.substring(0, Math.min(20, refreshToken.length())) + "..." : "null"));
        prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply();
    }
    
    public String getRefreshToken() {
        String refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null);
        Log.d(TAG, "获取刷新Token: " + (refreshToken != null ? refreshToken.substring(0, Math.min(20, refreshToken.length())) + "..." : "null"));
        return refreshToken;
    }
    
    public void saveUserId(Long userId) {
        Log.d(TAG, "保存用户ID: " + userId);
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }
    
    public Long getUserId() {
        Long userId = prefs.getLong(KEY_USER_ID, -1);
        Log.d(TAG, "获取用户ID: " + userId);
        return userId;
    }
    
    public void saveUsername(String username) {
        Log.d(TAG, "保存用户名: " + username);
        prefs.edit().putString(KEY_USERNAME, username).apply();
    }
    
    public String getUsername() {
        String username = prefs.getString(KEY_USERNAME, null);
        Log.d(TAG, "获取用户名: " + username);
        return username;
    }
    
    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired() {
        long expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        boolean expired = System.currentTimeMillis() > expiryTime;
        Log.d(TAG, "Token过期检查: " + (expired ? "已过期" : "未过期"));
        Log.d(TAG, "当前时间: " + new Date(System.currentTimeMillis()));
        Log.d(TAG, "过期时间: " + new Date(expiryTime));
        return expired;
    }
    
    /**
     * 检查Token是否有效（存在且未过期）
     */
    public boolean isTokenValid() {
        String token = getToken();
        boolean hasToken = token != null && !token.trim().isEmpty();
        boolean notExpired = !isTokenExpired();
        boolean valid = hasToken && notExpired;
        
        Log.d(TAG, "Token有效性检查:");
        Log.d(TAG, "- 存在Token: " + hasToken);
        Log.d(TAG, "- 未过期: " + notExpired);
        Log.d(TAG, "- 总体有效: " + valid);
        
        return valid;
    }
    
    public boolean isLoggedIn() {
        boolean hasUserId = getUserId() != -1;
        boolean hasValidToken = isTokenValid();
        boolean loggedIn = hasUserId && hasValidToken;
        
        Log.d(TAG, "登录状态检查:");
        Log.d(TAG, "- 有用户ID: " + hasUserId + " (ID: " + getUserId() + ")");
        Log.d(TAG, "- Token有效: " + hasValidToken);
        Log.d(TAG, "- 已登录: " + loggedIn);
        
        return loggedIn;
    }
    
    /**
     * 刷新Token（这里只是示例，实际需要调用API）
     */
    public boolean refreshToken() {
        Log.d(TAG, "尝试刷新Token");
        String refreshToken = getRefreshToken();
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            Log.e(TAG, "没有刷新Token，无法刷新");
            return false;
        }
        
        // TODO: 这里应该调用后端API刷新Token
        // 现在只是模拟刷新成功
        Log.d(TAG, "Token刷新成功（模拟）");
        return true;
    }
    
    /**
     * 验证并刷新Token（如果需要）
     */
    public boolean validateAndRefreshToken() {
        Log.d(TAG, "=== 开始验证和刷新Token ===");
        
        if (isTokenValid()) {
            Log.d(TAG, "Token有效，无需刷新");
            return true;
        }
        
        Log.w(TAG, "Token无效或已过期，尝试刷新");
        
        if (refreshToken()) {
            Log.d(TAG, "Token刷新成功");
            return true;
        } else {
            Log.e(TAG, "Token刷新失败，需要重新登录");
            return false;
        }
    }
    
    public void logout() {
        Log.d(TAG, "用户登出，清除所有认证信息");
        prefs.edit().clear().apply();
    }
    
    public String getAuthHeader() {
        String token = getToken();
        String authHeader = token != null ? "Bearer " + token : null;
        Log.d(TAG, "生成认证头: " + (authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "null"));
        return authHeader;
    }
    
    /**
     * 获取Token的详细信息（用于调试）
     */
    public String getTokenDebugInfo() {
        String token = getToken();
        if (token == null) {
            return "Token: null";
        }
        
        long expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        boolean expired = isTokenExpired();
        
        // 分析Token结构
        String[] tokenParts = token.split("\\.");
        String tokenStructure = tokenParts.length == 3 ? "完整JWT" : "不完整JWT";
        String headerInfo = tokenParts.length > 0 ? tokenParts[0] : "无";
        String payloadInfo = tokenParts.length > 1 ? tokenParts[1] : "无";
        String signatureInfo = tokenParts.length > 2 ? tokenParts[2] : "无";
        
        return String.format(
            "Token: %s...\n长度: %d\n结构: %s (共%d部分)\nHeader: %s\nPayload: %s\nSignature: %s\n过期时间: %s\n是否过期: %s\n用户ID: %d\n用户名: %s",
            token.substring(0, Math.min(20, token.length())),
            token.length(),
            tokenStructure,
            tokenParts.length,
            headerInfo,
            payloadInfo.length() > 20 ? payloadInfo.substring(0, 20) + "..." : payloadInfo,
            signatureInfo.length() > 20 ? signatureInfo.substring(0, 20) + "..." : signatureInfo,
            new Date(expiryTime),
            expired ? "是" : "否",
            getUserId(),
            getUsername()
        );
    }
    
    /**
     * 验证Token格式是否正确
     */
    public boolean isTokenFormatValid() {
        String token = getToken();
        if (token == null || token.trim().isEmpty()) {
            Log.w(TAG, "Token为空");
            return false;
        }
        
        String[] parts = token.split("\\.");
        boolean isValid = parts.length == 3;
        
        Log.d(TAG, "Token格式验证:");
        Log.d(TAG, "- Token长度: " + token.length());
        Log.d(TAG, "- 部分数量: " + parts.length);
        Log.d(TAG, "- 格式有效: " + isValid);
        
        if (parts.length > 0) {
            Log.d(TAG, "- Header长度: " + parts[0].length());
        }
        if (parts.length > 1) {
            Log.d(TAG, "- Payload长度: " + parts[1].length());
        }
        if (parts.length > 2) {
            Log.d(TAG, "- Signature长度: " + parts[2].length());
        }
        
        return isValid;
    }
}
