package com.example.myapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.network.NetworkConfig;
import com.example.myapplication.dto.ApiResponse;

/**
 * 应用程序主类
 * 用于全局初始化
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static final String CHANNEL_ID = "jpush_default_channel";
    private static final String CHANNEL_NAME = "来电通知";

    @Override
    public void onCreate() {
        super.onCreate();

        // 创建通知渠道（Android 8.0+）
        createNotificationChannel();

        // 初始化 JPush
        initJPush();
    }

    /**
     * 创建通知渠道（Android 8.0+ 必需）
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                // 检查通知渠道是否已存在
                NotificationChannel existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
                if (existingChannel == null) {
                    NotificationChannel channel = new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH  // 重要性设置为HIGH，确保通知能显示
                    );
                    channel.setDescription("接收视频/语音来电通知");
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    channel.setShowBadge(true);

                    notificationManager.createNotificationChannel(channel);
                    Log.i(TAG, "✅ 通知渠道已创建: " + CHANNEL_ID);
                } else {
                    Log.d(TAG, "通知渠道已存在: " + CHANNEL_ID);
                }
            }
        }
    }

    /**
     * 初始化极光推送
     */
    private void initJPush() {
        try {
            // 设置调试模式（生产环境请设为false）
            JPushInterface.setDebugMode(true);

            // 添加详细日志
            Log.d(TAG, "═══════════════════════════════════════");
            Log.d(TAG, "开始初始化JPush...");
            Log.d(TAG, "AppKey: ff90a2867fcf541a3f3e8ed4");
            Log.d(TAG, "包名: " + getPackageName());
            Log.d(TAG, "渠道: developer-default");
            Log.d(TAG, "设备信息: " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE);
            Log.d(TAG, "═══════════════════════════════════════");

            // 初始化 JPush
            JPushInterface.init(this);

            // 启用通知（重要！）
            JPushInterface.setChannel(this, "developer-default");

            // 确保通知权限已启用
            JPushInterface.setLatestNotificationNumber(this, 5);

            // 检查推送状态
            boolean isPushStopped = JPushInterface.isPushStopped(this);
            Log.d(TAG, "推送服务状态 - 是否停止: " + isPushStopped);

            // 强制重新注册
            JPushInterface.resumePush(this);
            Log.d(TAG, "强制重新启动推送服务");

            // 立即检查Registration ID
            String immediateRegId = JPushInterface.getRegistrationID(this);
            Log.d(TAG, "立即获取的Registration ID: " + immediateRegId);

            // 多次尝试获取Registration ID
            checkAndUploadRegistrationId(1000);  // 1秒后第一次检查
            checkAndUploadRegistrationId(3000);  // 3秒后第二次检查
            checkAndUploadRegistrationId(5000);  // 5秒后第三次检查
            checkAndUploadRegistrationId(10000); // 10秒后第四次检查

            Log.i(TAG, "✅ JPush 初始化成功");

        } catch (Exception e) {
            Log.e(TAG, "❌ JPush 初始化失败", e);
        }
    }

    /**
     * 检查并上传Registration ID
     */
    private void checkAndUploadRegistrationId(long delayMs) {
        new android.os.Handler().postDelayed(() -> {
            try {
                String regId = JPushInterface.getRegistrationID(this);
                Log.d(TAG, "检查Registration ID (延迟" + delayMs + "ms): " + regId);
                
                if (regId != null && !regId.trim().isEmpty() && !"0".equals(regId)) {
                    Log.i(TAG, "✅ 获取到有效Registration ID: " + regId);
                    uploadRegistrationIdToServer(regId);
                } else {
                    Log.w(TAG, "⚠️ Registration ID无效或为空: " + regId);
                    
                    // 如果Registration ID无效，尝试重新初始化
                    if (delayMs >= 5000) { // 只在延迟5秒以上时尝试重新初始化
                        Log.i(TAG, "尝试重新初始化JPush...");
                        JPushInterface.stopPush(this);
                        new android.os.Handler().postDelayed(() -> {
                            JPushInterface.init(this);
                            JPushInterface.resumePush(this);
                            Log.i(TAG, "JPush重新初始化完成");
                        }, 1000);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "检查Registration ID异常", e);
            }
        }, delayMs);
    }

    /**
     * 自动上传Registration ID到后端
     */
    private void uploadRegistrationIdToServer(String registrationId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "开始自动上传Registration ID: " + registrationId);
                
                // 检查用户是否已登录
                AuthManager authManager = AuthManager.getInstance(this);
                String token = authManager.getToken();
                if (token == null) {
                    Log.w(TAG, "用户未登录，跳过Registration ID上传");
                    return;
                }

                // 调用后端API上传
                retrofit2.Call<ApiResponse<String>> call = 
                    NetworkConfig.getApiService().updateRegistrationId(token, registrationId);

                retrofit2.Response<ApiResponse<String>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Log.i(TAG, "✅ 自动上传Registration ID成功: " + registrationId);
                    } else {
                        Log.e(TAG, "❌ 自动上传Registration ID失败: " + response.body().getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ 自动上传Registration ID请求失败: " + response.code());
                }

            } catch (Exception e) {
                Log.e(TAG, "自动上传Registration ID异常", e);
            }
        }).start();
    }

    /**
     * 设置JPush别名（用户登录后调用）
     * @param userId 用户ID
     */
    public static void setJPushAlias(Application app, Long userId) {
        if (userId == null) {
            Log.w(TAG, "用户ID为空，无法设置JPush别名");
            return;
        }

        try {
            int sequence = 1;
            String alias = String.valueOf(userId);
            JPushInterface.setAlias(app.getApplicationContext(), sequence, alias);
            Log.i(TAG, "JPush 别名设置成功: " + alias);
        } catch (Exception e) {
            Log.e(TAG, "JPush 别名设置失败", e);
        }
    }

    /**
     * 删除JPush别名（用户登出时调用）
     */
    public static void deleteJPushAlias(Application app) {
        try {
            int sequence = 1;
            JPushInterface.deleteAlias(app.getApplicationContext(), sequence);
            Log.i(TAG, "JPush 别名已删除");
        } catch (Exception e) {
            Log.e(TAG, "JPush 别名删除失败", e);
        }
    }
}
