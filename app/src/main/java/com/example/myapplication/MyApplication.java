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
import com.example.myapplication.websocket.MessageWebSocketManager;

/**
 * 应用程序主类
 * 用于全局初始化
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    private static final String CHANNEL_ID = "jpush_default_channel";
    private static final String CHANNEL_NAME = "来电通知";

    // WebSocket管理器（全局单例）
    private static MessageWebSocketManager webSocketManager;

    public MyApplication() {
        super();
        Log.e(TAG, "🔧🔧🔧 MyApplication 构造函数被调用 🔧🔧🔧");
    }

    /**
     * 获取全局WebSocket管理器
     */
    public static MessageWebSocketManager getWebSocketManager() {
        return webSocketManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 添加明显的调试日志
        Log.e(TAG, "🚀🚀🚀 MyApplication.onCreate() 开始执行 🚀🚀🚀");

        // 创建通知渠道（Android 8.0+）
        createNotificationChannel();

        // 初始化 JPush
        initJPush();

        // 初始化 WebSocket
        initWebSocket();
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
                            NotificationManager.IMPORTANCE_NONE  // 设置为NONE，完全禁用通知显示
                    );
                    channel.setDescription("接收视频/语音来电通知");
                    channel.enableVibration(false);
                    channel.enableLights(false);
                    channel.setShowBadge(false);

                    notificationManager.createNotificationChannel(channel);
                    Log.i(TAG, "✅ 通知渠道已创建: " + CHANNEL_ID);
                } else {
                    Log.d(TAG, "通知渠道已存在: " + CHANNEL_ID);
                }
            }
        }
    }

    /**
     * 初始化WebSocket管理器
     */
    private void initWebSocket() {
        try {
            Log.d(TAG, "═══════════════════════════════════════");
            Log.d(TAG, "开始初始化WebSocket管理器...");

            AuthManager authManager = AuthManager.getInstance(this);
            webSocketManager = new MessageWebSocketManager(authManager);

            // 如果用户已登录，立即连接WebSocket
            if (authManager.isLoggedIn()) {
                Long userId = authManager.getUserId();
                Log.i(TAG, "用户已登录 - userId: " + userId + "，自动连接WebSocket");
                webSocketManager.connect();
                Log.i(TAG, "✅ WebSocket管理器初始化成功并已连接");
            } else {
                Log.i(TAG, "✅ WebSocket管理器初始化成功（用户未登录，待登录后连接）");
            }

            Log.d(TAG, "═══════════════════════════════════════");
        } catch (Exception e) {
            Log.e(TAG, "❌ WebSocket管理器初始化失败", e);
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

            // 完全禁用 JPush 系统通知
            JPushInterface.setLatestNotificationNumber(this, 0);
            Log.d(TAG, "已设置 JPush 通知数量为 0");

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
     * 自动上传Registration ID到后端 (多设备支持)
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

                // 生成唯一设备标识
                String uniqueDeviceId = generateUniqueDeviceId(registrationId);
                
                // 获取详细设备信息
                String deviceName = getDeviceName();
                String deviceType = "ANDROID";
                String appVersion = getAppVersion();
                String osVersion = android.os.Build.VERSION.RELEASE;
                
                Log.i(TAG, "设备信息 - 名称: " + deviceName + ", 类型: " + deviceType + 
                      ", 应用版本: " + appVersion + ", 系统版本: " + osVersion);
                Log.i(TAG, "唯一设备ID: " + uniqueDeviceId + " (基于: " + registrationId + ")");
                
                // 优先使用新的多设备API
                try {
                    retrofit2.Call<ApiResponse<String>> call = 
                        NetworkConfig.getApiService().registerDevice(token, registrationId, deviceName, deviceType);

                    retrofit2.Response<ApiResponse<String>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            Log.i(TAG, "✅ 设备注册成功: " + registrationId + " (" + deviceName + ")");
                            return;
                        } else {
                            Log.w(TAG, "⚠️ 设备注册失败，尝试兼容模式: " + response.body().getMessage());
                        }
                    } else {
                        Log.w(TAG, "⚠️ 设备注册请求失败，尝试兼容模式: " + response.code());
                    }
                } catch (Exception e) {
                    Log.w(TAG, "⚠️ 设备注册异常，尝试兼容模式", e);
                }

                // 兼容模式：使用旧的API
                retrofit2.Call<ApiResponse<String>> call = 
                    NetworkConfig.getApiService().updateRegistrationId(token, registrationId);

                retrofit2.Response<ApiResponse<String>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Log.i(TAG, "✅ 兼容模式上传Registration ID成功: " + registrationId);
                    } else {
                        Log.e(TAG, "❌ 兼容模式上传Registration ID失败: " + response.body().getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ 兼容模式上传Registration ID请求失败: " + response.code());
                }

            } catch (Exception e) {
                Log.e(TAG, "上传Registration ID异常", e);
            }
        }).start();
    }

    /**
     * 获取设备名称
     */
    private String getDeviceName() {
        try {
            String manufacturer = android.os.Build.MANUFACTURER;
            String model = android.os.Build.MODEL;
            String version = android.os.Build.VERSION.RELEASE;
            
            if (model.startsWith(manufacturer)) {
                return model + " (" + version + ")";
            } else {
                return manufacturer + " " + model + " (" + version + ")";
            }
        } catch (Exception e) {
            Log.e(TAG, "获取设备名称失败", e);
            return "Android设备 (" + android.os.Build.VERSION.RELEASE + ")";
        }
    }

    /**
     * 获取应用版本
     */
    private String getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            Log.e(TAG, "获取应用版本失败", e);
            return "1.0.0";
        }
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
    
    /**
     * 生成唯一设备标识
     */
    private String generateUniqueDeviceId(String registrationId) {
        // 使用设备ID和注册ID的组合生成唯一标识
        String deviceId = android.provider.Settings.Secure.getString(
            getContentResolver(), 
            android.provider.Settings.Secure.ANDROID_ID
        );
        return deviceId + "_" + registrationId.substring(0, Math.min(8, registrationId.length()));
    }
}
