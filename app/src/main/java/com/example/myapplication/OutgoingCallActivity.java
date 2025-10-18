package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.websocket.WebSocketClient;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 等待接听界面Activity
 * 发起方等待对方接听的界面
 */
public class OutgoingCallActivity extends AppCompatActivity {

    private static final String TAG = "OutgoingCallActivity";
    private static final int TIMEOUT_SECONDS = 60; // 60秒超时

    private String sessionId;
    private String receiverId;
    private String receiverName;
    private String receiverAvatar;
    private String callType;

    private TextView tvReceiverName;
    private TextView tvCallStatus;
    private ImageView ivReceiverAvatar;
    private View btnCancel;

    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    private CallStatusReceiver callStatusReceiver;
    private AuthManager authManager;
    
    // 轮询机制
    private Handler pollingHandler;
    private Runnable pollingRunnable;
    private boolean isPolling = false;
    
    // WebSocket信令
    private WebSocketClient webSocketClient;
    private boolean isWebSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);

        Log.d(TAG, "=== OutgoingCallActivity onCreate 开始 ===");

        // 获取Intent数据
        sessionId = getIntent().getStringExtra("sessionId");
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverAvatar = getIntent().getStringExtra("receiverAvatar");
        callType = getIntent().getStringExtra("callType");

        Log.d(TAG, "等待接听界面启动 - sessionId: " + sessionId + ", receiver: " + receiverName);

        authManager = AuthManager.getInstance(this);

        Log.d(TAG, "开始初始化视图...");
        initViews();
        
        Log.d(TAG, "开始注册广播接收器...");
        registerCallStatusReceiver();
        
        Log.d(TAG, "开始启动超时计时...");
        startTimeout();
        
        Log.d(TAG, "开始启动轮询机制...");
        startPolling();
        
        Log.d(TAG, "开始初始化WebSocket...");
        initWebSocket();
        
        Log.d(TAG, "=== OutgoingCallActivity onCreate 完成 ===");
    }

    private void initViews() {
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvCallStatus = findViewById(R.id.tv_call_status);
        ivReceiverAvatar = findViewById(R.id.iv_receiver_avatar);
        btnCancel = findViewById(R.id.btn_cancel);

        // 显示接收方信息
        if (receiverName != null && !receiverName.isEmpty()) {
            tvReceiverName.setText(receiverName);
        } else {
            tvReceiverName.setText("用户");
        }

        // 显示状态
        if ("VIDEO".equals(callType)) {
            tvCallStatus.setText("等待对方接听视频通话...");
        } else {
            tvCallStatus.setText("等待对方接听语音通话...");
        }

        // TODO: 加载头像
        // Coil.load(receiverAvatar).into(ivReceiverAvatar);

        // 设置取消按钮
        btnCancel.setOnClickListener(v -> cancelCall());
    }

    /**
     * 注册通话状态广播接收器
     */
    private void registerCallStatusReceiver() {
        try {
            callStatusReceiver = new CallStatusReceiver();
            IntentFilter filter = new IntentFilter("com.example.myapplication.CALL_STATUS_UPDATE");

            // Android 13+ (API 33+) 需要指定 RECEIVER_NOT_EXPORTED
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(callStatusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
                Log.i(TAG, "通话状态广播接收器已注册 (API 33+) - sessionId: " + sessionId);
            } else {
                registerReceiver(callStatusReceiver, filter);
                Log.i(TAG, "通话状态广播接收器已注册 (API < 33) - sessionId: " + sessionId);
            }
            
            Log.i(TAG, "✅ 广播接收器注册成功 - 监听: com.example.myapplication.CALL_STATUS_UPDATE");
        } catch (Exception e) {
            Log.e(TAG, "❌ 广播接收器注册失败", e);
        }
    }

    /**
     * 开始超时计时
     */
    private void startTimeout() {
        timeoutHandler = new Handler();
        timeoutRunnable = () -> {
            Log.w(TAG, "等待超时，自动取消通话");
            Toast.makeText(OutgoingCallActivity.this, "对方无应答", Toast.LENGTH_SHORT).show();
            cancelCall();
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_SECONDS * 1000);
    }
    
    /**
     * 开始轮询通话状态（备用方案）
     */
    private void startPolling() {
        pollingHandler = new Handler();
        isPolling = true;
        
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (isPolling) {
                    Log.d(TAG, "轮询检查通话状态 - sessionId: " + sessionId);
                    checkCallStatus();
                    
                    // 每2秒轮询一次
                    pollingHandler.postDelayed(this, 2000);
                }
            }
        };
        
        pollingHandler.post(pollingRunnable);
        Log.i(TAG, "✅ 轮询机制已启动 - 每2秒检查一次");
    }
    
    /**
     * 停止轮询
     */
    private void stopPolling() {
        isPolling = false;
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
        Log.i(TAG, "轮询机制已停止");
    }
    
    /**
     * 检查通话状态
     */
    private void checkCallStatus() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return getCallStatusFromServer(sessionId);
            }
            
            @Override
            protected void onPostExecute(String status) {
                if (status != null) {
                    Log.d(TAG, "轮询获取到状态: " + status);
                    
                    if ("ACCEPTED".equals(status)) {
                        Log.i(TAG, "轮询检测到对方已接听");
                        stopPolling();
                        onCallAccepted();
                    } else if ("REJECTED".equals(status)) {
                        Log.i(TAG, "轮询检测到对方已拒绝");
                        stopPolling();
                        onCallRejected();
                    }
                }
            }
        }.execute();
    }
    
    /**
     * 从服务器获取通话状态
     */
    private String getCallStatusFromServer(String sessionId) {
        try {
            String token = authManager.getToken();
            if (token == null) {
                Log.e(TAG, "用户未登录");
                return null;
            }

            URL url = new URL("http://10.0.2.2:8080/api/call/status/" + sessionId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "获取通话状态API响应码: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 解析响应
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getBoolean("success")) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    return data.getString("status");
                }
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "获取通话状态失败", e);
            return null;
        }
    }
    
    /**
     * 初始化WebSocket连接
     */
    private void initWebSocket() {
        webSocketClient = new WebSocketClient(authManager, new WebSocketClient.WebSocketListener() {
            @Override
            public void onMessage(String message) {
                handleWebSocketMessage(message);
            }
            
            @Override
            public void onOpen() {
                isWebSocketConnected = true;
                Log.i(TAG, "✅ WebSocket连接已建立");
            }
            
            @Override
            public void onClose() {
                isWebSocketConnected = false;
                Log.i(TAG, "WebSocket连接已关闭");
            }
            
            @Override
            public void onError(Throwable error) {
                isWebSocketConnected = false;
                Log.e(TAG, "❌ WebSocket连接错误", error);
            }
        });
        
        webSocketClient.connect();
    }
    
    /**
     * 处理WebSocket消息
     */
    private void handleWebSocketMessage(String message) {
        try {
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.optString("type");
            String sessionId = jsonMessage.optString("sessionId");
            String status = jsonMessage.optString("status");
            
            Log.i(TAG, "收到WebSocket信令 - type: " + type + ", sessionId: " + sessionId + ", status: " + status);
            
            if (this.sessionId != null && this.sessionId.equals(sessionId)) {
                if ("CALL_ACCEPT".equals(type) && "ACCEPTED".equals(status)) {
                    Log.i(TAG, "✅ WebSocket检测到对方已接听");
                    onCallAccepted();
                } else if ("CALL_REJECT".equals(type) && "REJECTED".equals(status)) {
                    Log.i(TAG, "✅ WebSocket检测到对方已拒绝");
                    onCallRejected();
                }
            } else {
                Log.w(TAG, "WebSocket消息会话ID不匹配 - received: " + sessionId + ", current: " + this.sessionId);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "处理WebSocket消息失败", e);
        }
    }

    /**
     * 取消通话
     */
    private void cancelCall() {
        Log.d(TAG, "取消通话 - sessionId: " + sessionId);

        // 禁用按钮
        btnCancel.setEnabled(false);

        // 调用后端API拒绝/结束通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return callEndApi(sessionId);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(OutgoingCallActivity.this, "已取消通话", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        }.execute();
    }

    /**
     * 调用后端API结束通话
     */
    private boolean callEndApi(String sessionId) {
        try {
            String token = authManager.getToken();
            if (token == null) {
                Log.e(TAG, "用户未登录");
                return false;
            }

            URL url = new URL("http://10.0.2.2:8080/api/call/end");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("callSessionId", sessionId);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "结束通话API响应码: " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "调用结束通话API失败", e);
            return false;
        }
    }

    /**
     * 通话状态广播接收器
     */
    private class CallStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedSessionId = intent.getStringExtra("sessionId");
            String status = intent.getStringExtra("status");
            String message = intent.getStringExtra("message");

            Log.i(TAG, "=== 收到通话状态广播 ===");
            Log.i(TAG, "receivedSessionId: " + receivedSessionId);
            Log.i(TAG, "currentSessionId: " + sessionId);
            Log.i(TAG, "status: " + status);
            Log.i(TAG, "message: " + message);
            Log.i(TAG, "================================");

            if (sessionId != null && receivedSessionId != null && sessionId.equals(receivedSessionId)) {
                Log.i(TAG, "✅ 会话ID匹配，处理状态更新 - status: " + status);

                if ("ACCEPTED".equals(status)) {
                    Log.i(TAG, "对方已接听，准备跳转到视频通话界面");
                    // 对方已接听，跳转到视频通话界面
                    onCallAccepted();
                } else if ("REJECTED".equals(status)) {
                    Log.i(TAG, "对方已拒绝通话");
                    // 对方已拒绝
                    onCallRejected();
                } else {
                    Log.w(TAG, "未知状态: " + status);
                }
            } else {
                Log.w(TAG, "❌ 会话ID不匹配或为空");
                Log.w(TAG, "receivedSessionId: " + receivedSessionId);
                Log.w(TAG, "currentSessionId: " + sessionId);
                Log.w(TAG, "sessionId.equals(receivedSessionId): " + (sessionId != null && receivedSessionId != null ? sessionId.equals(receivedSessionId) : "null"));
            }
        }
    }

    /**
     * 对方接听通话
     */
    private void onCallAccepted() {
        Log.i(TAG, "对方已接听");

        // 停止轮询
        stopPolling();

        runOnUiThread(() -> {
            Toast.makeText(this, "对方已接听", Toast.LENGTH_SHORT).show();

            // 跳转到视频通话界面
            Intent intent = new Intent(OutgoingCallActivity.this, VideoChatActivity.class);
            intent.putExtra("CALL_ID", sessionId);
            intent.putExtra("ROOM_ID", sessionId);
            intent.putExtra("REMOTE_USER_ID", receiverId);
            intent.putExtra("IS_CALLER", true); // 发起方
            intent.putExtra("CALL_TYPE", callType);
            startActivity(intent);
            finish();
        });
    }

    /**
     * 对方拒绝通话
     */
    private void onCallRejected() {
        Log.i(TAG, "对方已拒绝");

        // 停止轮询
        stopPolling();

        runOnUiThread(() -> {
            Toast.makeText(this, "对方已拒绝", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 断开WebSocket连接
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            Log.i(TAG, "WebSocket连接已断开");
        }

        // 停止轮询
        stopPolling();

        // 取消超时计时
        if (timeoutHandler != null && timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        // 注销广播接收器
        if (callStatusReceiver != null) {
            try {
                unregisterReceiver(callStatusReceiver);
                Log.i(TAG, "广播接收器已注销");
            } catch (Exception e) {
                Log.e(TAG, "注销广播接收器失败", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // 点击返回键取消通话
        cancelCall();
    }
}
