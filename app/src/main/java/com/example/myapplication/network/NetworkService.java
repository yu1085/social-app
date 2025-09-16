package com.example.myapplication.network;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.PostDTO;
import com.example.myapplication.dto.MessageDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkService {
    private static final String TAG = "NetworkService";
    private static NetworkService instance;
    private ApiService apiService;
    private AuthManager authManager;
    
    private NetworkService(Context context) {
        apiService = NetworkConfig.getApiService();
        authManager = AuthManager.getInstance(context);
    }
    
    public static synchronized NetworkService getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkService(context.getApplicationContext());
        }
        return instance;
    }
    
    
    // 发送验证码
    public void sendVerificationCode(String phone, NetworkCallback<String> callback) {
        apiService.sendVerificationCode(phone).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("发送验证码失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Log.e(TAG, "发送验证码请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 验证码登录/注册
    public void loginWithVerificationCode(String phone, String code, NetworkCallback<LoginResponse> callback) {
        apiService.loginWithVerificationCode(phone, code).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginResponse = apiResponse.getData();
                        // 保存认证信息
                        authManager.saveToken(loginResponse.getToken());
                        authManager.saveUserId(loginResponse.getUser().getId());
                        authManager.saveUsername(loginResponse.getUser().getUsername());
                        callback.onSuccess(loginResponse);
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("登录失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "验证码登录请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 用户登录
    public void login(LoginRequest request, NetworkCallback<LoginResponse> callback) {
        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        LoginResponse loginResponse = apiResponse.getData();
                        // 保存认证信息
                        authManager.saveToken(loginResponse.getToken());
                        authManager.saveUserId(loginResponse.getUser().getId());
                        authManager.saveUsername(loginResponse.getUser().getUsername());
                        callback.onSuccess(loginResponse);
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("登录失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "登录请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 获取用户信息
    public void getProfile(NetworkCallback<UserDTO> callback) {
        String authHeader = authManager.getAuthHeader();
        if (authHeader == null) {
            callback.onError("未登录");
            return;
        }
        
        apiService.getProfile(authHeader).enqueue(new Callback<ApiResponse<UserDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserDTO>> call, Response<ApiResponse<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserDTO> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("获取用户信息失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<UserDTO>> call, Throwable t) {
                Log.e(TAG, "获取用户信息请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 搜索用户
    public void searchUsers(String keyword, String gender, String location, 
                          Integer minAge, Integer maxAge, int page, int size, 
                          NetworkCallback<List<UserDTO>> callback) {
        apiService.searchUsers(keyword, gender, location, minAge, maxAge, page, size)
                .enqueue(new Callback<ApiResponse<List<UserDTO>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<UserDTO>>> call, Response<ApiResponse<List<UserDTO>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<UserDTO>> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                callback.onSuccess(apiResponse.getData());
                            } else {
                                callback.onError(apiResponse.getMessage());
                            }
                        } else {
                            callback.onError("搜索用户失败: " + response.message());
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<UserDTO>>> call, Throwable t) {
                        Log.e(TAG, "搜索用户请求失败", t);
                        callback.onError("网络错误: " + t.getMessage());
                    }
                });
    }
    
    // 获取动态列表
    public void getPosts(int page, int size, NetworkCallback<List<PostDTO>> callback) {
        apiService.getPosts(page, size).enqueue(new Callback<ApiResponse<List<PostDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PostDTO>>> call, Response<ApiResponse<List<PostDTO>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<PostDTO>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("获取动态列表失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<PostDTO>>> call, Throwable t) {
                Log.e(TAG, "获取动态列表请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 点赞动态
    public void likePost(Long postId, NetworkCallback<PostDTO> callback) {
        String authHeader = authManager.getAuthHeader();
        if (authHeader == null) {
            callback.onError("未登录");
            return;
        }
        
        apiService.likePost(authHeader, postId).enqueue(new Callback<ApiResponse<PostDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostDTO>> call, Response<ApiResponse<PostDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostDTO> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(apiResponse.getMessage());
                    }
                } else {
                    callback.onError("点赞失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<PostDTO>> call, Throwable t) {
                Log.e(TAG, "点赞请求失败", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 网络回调接口
    public interface NetworkCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
