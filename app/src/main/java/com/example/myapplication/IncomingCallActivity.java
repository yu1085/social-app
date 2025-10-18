package com.example.myapplication;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.network.NetworkConfig;
import com.example.myapplication.websocket.WebSocketClient;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 来电界面Activity
 * 类似微信的全屏来电提示
 */
public class IncomingCallActivity extends AppCompatActivity {

    private static final String TAG = "IncomingCallActivity";

    private String sessionId;
    private String callerId;
    private String callerName;
    private String callerAvatar;
    private String callType;

    private TextView tvCallerName;
    private TextView tvCallType;
    private ImageView ivCallerAvatar;
    private View btnAccept;
    private View btnReject;

    private Ringtone ringtone;
    private Vibrator vibrator;
    private AuthManager authManager;
    
    // WebSocket信令
    private WebSocketClient webSocketClient;
    private boolean isWebSocketConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置为全屏显示，锁屏时也显示
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        setContentView(R.layout.activity_incoming_call);

        // 获取Intent数据
        sessionId = getIntent().getStringExtra("sessionId");
        callerId = getIntent().getStringExtra("callerId");
        callerName = getIntent().getStringExtra("callerName");
        callerAvatar = getIntent().getStringExtra("callerAvatar");
        callType = getIntent().getStringExtra("callType");

        Log.d(TAG, "来电界面启动 - sessionId: " + sessionId + ", caller: " + callerName + ", type: " + callType);

        authManager = AuthManager.getInstance(this);

        initViews();
        startRinging();
        initWebSocket();
    }

    private void initViews() {
        tvCallerName = findViewById(R.id.tv_caller_name);
        tvCallType = findViewById(R.id.tv_call_type);
        ivCallerAvatar = findViewById(R.id.iv_caller_avatar);
        btnAccept = findViewById(R.id.btn_accept);
        btnReject = findViewById(R.id.btn_reject);

        // 显示来电者信息
        if (callerName != null && !callerName.isEmpty()) {
            tvCallerName.setText(callerName);
        } else {
            tvCallerName.setText("未知用户");
        }

        // 显示通话类型
        if ("VIDEO".equals(callType)) {
            tvCallType.setText("视频通话");
        } else {
            tvCallType.setText("语音通话");
        }

        // TODO: 加载头像（使用Coil或Glide）
        // Coil.load(callerAvatar).into(ivCallerAvatar);

        // 设置按钮点击事件
        btnAccept.setOnClickListener(v -> acceptCall());
        btnReject.setOnClickListener(v -> rejectCall());
    }

    /**
     * 开始铃声和震动
     */
    private void startRinging() {
        try {
            // 播放铃声
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            if (ringtone != null) {
                ringtone.play();
            }

            // 开始震动
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 500, 1000, 500}; // 震动模式
                vibrator.vibrate(pattern, 0); // 重复震动
            }

        } catch (Exception e) {
            Log.e(TAG, "播放铃声/震动失败", e);
        }
    }

    /**
     * 停止铃声和震动
     */
    private void stopRinging() {
        try {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }
            if (vibrator != null) {
                vibrator.cancel();
            }
        } catch (Exception e) {
            Log.e(TAG, "停止铃声/震动失败", e);
        }
    }

    /**
     * 接听通话
     */
    private void acceptCall() {
        Log.d(TAG, "用户接听通话 - sessionId: " + sessionId);
        stopRinging();

        // 禁用按钮防止重复点击
        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);

        // 发送接听信令
        sendAcceptSignaling();
        
        // 调用后端API接受通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return callAcceptApi(sessionId);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // 接受成功，跳转到视频通话界面
                    Intent intent = new Intent(IncomingCallActivity.this, VideoChatActivity.class);
                    intent.putExtra("CALL_ID", sessionId);
                    intent.putExtra("ROOM_ID", sessionId);
                    intent.putExtra("REMOTE_USER_ID", callerId);
                    intent.putExtra("IS_CALLER", false); // 接收方
                    intent.putExtra("CALL_TYPE", callType);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(IncomingCallActivity.this, "接听失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute();
    }

    /**
     * 拒绝通话
     */
    private void rejectCall() {
        Log.d(TAG, "用户拒绝通话 - sessionId: " + sessionId);
        stopRinging();

        // 禁用按钮防止重复点击
        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);

        // 发送拒绝信令
        sendRejectSignaling();
        
        // 调用后端API拒绝通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return callRejectApi(sessionId);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(IncomingCallActivity.this, "已拒绝通话", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IncomingCallActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        }.execute();
    }

    /**
     * 调用后端API接受通话
     */
    private boolean callAcceptApi(String sessionId) {
        try {
            String token = authManager.getToken();
            if (token == null) {
                Log.e(TAG, "用户未登录");
                return false;
            }

            URL url = new URL("http://10.0.2.2:8080/api/call/accept");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("callSessionId", sessionId);

            // 发送请求
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "接受通话API响应码: " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "调用接受通话API失败", e);
            return false;
        }
    }

    /**
     * 调用后端API拒绝通话
     */
    private boolean callRejectApi(String sessionId) {
        try {
            String token = authManager.getToken();
            if (token == null) {
                Log.e(TAG, "用户未登录");
                return false;
            }

            URL url = new URL("http://10.0.2.2:8080/api/call/reject");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("callSessionId", sessionId);

            // 发送请求
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "拒绝通话API响应码: " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "调用拒绝通话API失败", e);
            return false;
        }
    }
    
    /**
     * 初始化WebSocket连接
     */
    private void initWebSocket() {
        webSocketClient = new WebSocketClient(authManager, new WebSocketClient.WebSocketListener() {
            @Override
            public void onMessage(String message) {
                // 来电界面通常不需要处理WebSocket消息
                Log.d(TAG, "收到WebSocket消息: " + message);
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
     * 发送接听信令
     */
    private void sendAcceptSignaling() {
        if (webSocketClient != null && isWebSocketConnected) {
            try {
                JSONObject message = new JSONObject();
                message.put("type", "CALL_ACCEPT");
                message.put("sessionId", sessionId);
                message.put("callerId", Long.valueOf(callerId));
                message.put("receiverId", authManager.getCurrentUserId());
                
                webSocketClient.sendMessage(message);
                Log.i(TAG, "✅ 接听信令已发送");
            } catch (Exception e) {
                Log.e(TAG, "发送接听信令失败", e);
            }
        } else {
            Log.w(TAG, "WebSocket未连接，无法发送接听信令");
        }
    }
    
    /**
     * 发送拒绝信令
     */
    private void sendRejectSignaling() {
        if (webSocketClient != null && isWebSocketConnected) {
            try {
                JSONObject message = new JSONObject();
                message.put("type", "CALL_REJECT");
                message.put("sessionId", sessionId);
                message.put("callerId", Long.valueOf(callerId));
                message.put("receiverId", authManager.getCurrentUserId());
                
                webSocketClient.sendMessage(message);
                Log.i(TAG, "✅ 拒绝信令已发送");
            } catch (Exception e) {
                Log.e(TAG, "发送拒绝信令失败", e);
            }
        } else {
            Log.w(TAG, "WebSocket未连接，无法发送拒绝信令");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // 断开WebSocket连接
        if (webSocketClient != null) {
            webSocketClient.disconnect();
            Log.i(TAG, "WebSocket连接已断开");
        }
        
        stopRinging();
    }

    @Override
    public void onBackPressed() {
        // 禁用返回键，防止用户误操作
        // 用户必须明确选择接听或拒绝
    }
}
