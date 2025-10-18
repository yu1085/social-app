package com.example.myapplication.network;

import android.content.Context;
import android.util.Log;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.MessageDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    
    private static ApiClient instance;
    private ApiService apiService;
    
    private ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }
    
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }
    
    public ApiService getApiService() {
        return apiService;
    }
    
    /**
     * 发送消息
     */
    public void sendMessage(Context context, Long receiverId, String content, String messageType, 
                           MessageCallback callback) {
        try {
            AuthManager authManager = AuthManager.getInstance(context);
            String token = authManager.getToken();
            if (token == null) {
                callback.onError("用户未登录");
                return;
            }
            
            Long senderId = authManager.getUserId();
            if (senderId == null) {
                callback.onError("无法获取用户ID");
                return;
            }
            
            // 创建MessageDTO
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setSenderId(senderId);
            messageDTO.setReceiverId(receiverId);
            messageDTO.setContent(content);
            messageDTO.setMessageType(messageType);
            
            String authHeader = "Bearer " + token;
            Call<ApiResponse<MessageDTO>> call = apiService.sendMessage(authHeader, messageDTO);
            
            call.enqueue(new Callback<ApiResponse<MessageDTO>>() {
                @Override
                public void onResponse(Call<ApiResponse<MessageDTO>> call, Response<ApiResponse<MessageDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            callback.onSuccess(response.body().getData());
                        } else {
                            callback.onError(response.body().getMessage());
                        }
                    } else {
                        callback.onError("发送消息失败: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<MessageDTO>> call, Throwable t) {
                    Log.e(TAG, "发送消息网络错误", t);
                    callback.onError("网络错误: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "发送消息异常", e);
            callback.onError("发送消息异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取聊天记录
     */
    public void getChatHistory(Context context, Long userId1, Long userId2, ChatHistoryCallback callback) {
        try {
            AuthManager authManager = AuthManager.getInstance(context);
            String token = authManager.getToken();
            if (token == null) {
                callback.onError("用户未登录");
                return;
            }
            
            String authHeader = "Bearer " + token;
            Call<ApiResponse<List<MessageDTO>>> call = apiService.getChatHistory(authHeader, userId1, userId2);
            
            call.enqueue(new Callback<ApiResponse<List<MessageDTO>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<MessageDTO>>> call, Response<ApiResponse<List<MessageDTO>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            callback.onSuccess(response.body().getData());
                        } else {
                            callback.onError(response.body().getMessage());
                        }
                    } else {
                        callback.onError("获取聊天记录失败: " + response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<List<MessageDTO>>> call, Throwable t) {
                    Log.e(TAG, "获取聊天记录网络错误", t);
                    callback.onError("网络错误: " + t.getMessage());
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "获取聊天记录异常", e);
            callback.onError("获取聊天记录异常: " + e.getMessage());
        }
    }
    
    public interface MessageCallback {
        void onSuccess(MessageDTO message);
        void onError(String error);
    }
    
    public interface ChatHistoryCallback {
        void onSuccess(List<MessageDTO> messages);
        void onError(String error);
    }
}
