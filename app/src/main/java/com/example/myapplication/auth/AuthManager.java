package com.example.myapplication.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    
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
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public void saveUserId(Long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }
    
    public Long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }
    
    public void saveUsername(String username) {
        prefs.edit().putString(KEY_USERNAME, username).apply();
    }
    
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }
    
    public boolean isLoggedIn() {
        return getToken() != null && getUserId() != -1;
    }
    
    public void logout() {
        prefs.edit().clear().apply();
    }
    
    public String getAuthHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
}
