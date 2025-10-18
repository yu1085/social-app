# 视频通话完整解决方案

## 📱 项目概述

基于现有火山引擎RTC视频通话功能，通过集成极光推送(JPush)实现完整的来电通知系统，打造类似微信的1v1视频通话体验。

## 🎯 技术架构

### 整体流程
```
发起方 (User A)                    后端 (Backend)                     接收方 (User B)
      |                                    |                                    |
      | 1. 点击"视频通话"按钮               |                                    |
      | (UserDetailActivity)                |                                    |
      |------------------------------------>|                                    |
      | POST /api/call/initiate             |                                    |
      |                                    |                                    |
      |                                    | 2. 创建通话会话                    |
      |                                    | CallSession                        |
      |                                    |                                    |
      |                                    | 3. 🔔 JPush 推送 ---------------->| 
      |                                    |    "video_caller 向您发起视频通话" |
      |                                    |                                    |
      | 4. 跳转到等待接听界面 🆕            |                                    | 5. 🆕 显示来电界面
      | OutgoingCallActivity                |                                    | IncomingCallActivity
      | "等待对方接听..."                   |                                    | "video_caller 视频通话"
      | [取消] 按钮                         |                                    | [接听] [拒绝] 按钮
      |                                    |                                    | 🔔 播放铃声 + 振动
      |                                    |                                    |
      |                                    |                     6. 🆕 点击[接听]
      |                                    |<-----------------------------------|
      |                                    | POST /api/call/accept              |
      |                                    |                                    |
      | 7. 🆕 收到"已接听"通知              |                                    |
      |<-----------------------------------|                                    |
      |                                    |                                    |
      | 8. 跳转到视频通话界面 ✅ (原有功能)  |                                    | 8. 跳转到视频通话界面 ✅
      | VideoChatActivity                   |                                    | VideoChatActivity
      |------------------------------------------------------------------->|
      |                        🎥 VolcEngine RTC 视频通话 (已有功能)        |
      |<-------------------------------------------------------------------|
      |                                    |                                    |
```

### 技术栈
- **后端**: Spring Boot + JPush SDK 5.2.9
- **Android**: 极光推送SDK + 火山引擎RTC SDK
- **推送**: 极光推送(JPush)
- **视频通话**: 火山引擎RTC (字节跳动技术)

## 🔧 实现方案

### 1️⃣ 后端部分（已完成）

#### JPush推送服务
```java
// JPushService.java
@Service
public class JPushService {
    private static final String APP_KEY = "ff90a2867fcf541a3f3e8ed4";
    private static final String MASTER_SECRET = "112ee5a04324ae703d2d6b3d";
    
    /**
     * 发送来电通知
     */
    public boolean sendCallNotification(Long receiverId, Long callerId, String callerName,
                                        String callerAvatar, String sessionId, String callType) {
        // 构建推送数据
        Map<String, Object> extras = new HashMap<>();
        extras.put("type", "INCOMING_CALL");
        extras.put("sessionId", sessionId);
        extras.put("callerId", callerId);
        extras.put("callerName", callerName);
        extras.put("callerAvatar", callerAvatar);
        extras.put("callType", callType);
        extras.put("timestamp", System.currentTimeMillis());
        
        // 构建通知
        NotificationMessage notification = new NotificationMessage();
        notification.setAlert("视频通话 - " + callerName + " 向您发起视频通话");
        notification.setTitle("视频通话");
        
        // 发送到指定用户（使用 alias = userId）
        PushSendParam param = new PushSendParam();
        param.setAudience(Audience.alias(receiverId));
        param.setPlatform(Platform.android);
        param.setNotification(notification);
        
        return pushApi.send(param) != null;
    }
    
    /**
     * 发送通话状态更新通知
     */
    public boolean sendCallStatusNotification(Long userId, String sessionId, String status, String message) {
        // 构建状态更新推送
        Map<String, Object> extras = new HashMap<>();
        extras.put("type", "CALL_STATUS");
        extras.put("sessionId", sessionId);
        extras.put("status", status);
        extras.put("timestamp", System.currentTimeMillis());
        
        // 发送状态更新
        // ... 实现细节
    }
}
```

#### 通话会话管理
```java
// CallService.java
@Service
public class CallService {
    
    public CallSession initiateCall(Long callerId, Long receiverId, String callType) {
        // 1. 验证用户存在性
        User caller = userRepository.findById(callerId);
        User receiver = userRepository.findById(receiverId);
        
        // 2. 检查接收者是否在线
        if (!receiver.getIsOnline()) {
            throw new RuntimeException("对方不在线");
        }
        
        // 3. 创建通话会话记录
        CallSession session = new CallSession();
        session.setCallSessionId("CALL_" + UUID.randomUUID());
        session.setCallerId(callerId);
        session.setReceiverId(receiverId);
        session.setCallType(CallType.VIDEO);
        session.setStatus(CallStatus.INITIATED);
        session.setPricePerMinute(300.0); // 视频通话价格
        callSessionRepository.save(session);
        
        // 4. 🔔 发送 JPush 推送通知给接收方
        jPushService.sendCallNotification(
            receiverId,           // 推送目标
            callerId,             // 发起者ID
            caller.getNickname(), // 发起者昵称
            caller.getAvatarUrl(), // 发起者头像
            session.getCallSessionId(), // 会话ID
            "VIDEO"               // 通话类型
        );
        
        // 5. 返回会话信息给发起方
        return session;
    }
    
    public CallSession acceptCall(String sessionId, Long userId) {
        // 1. 更新会话状态
        CallSession session = callSessionRepository.findByCallSessionId(sessionId);
        session.setStatus(CallStatus.ACCEPTED);
        session.setStartTime(LocalDateTime.now());
        callSessionRepository.save(session);
        
        // 2. 🔔 通知发起方
        jPushService.sendCallStatusNotification(
            session.getCallerId(), 
            sessionId, 
            "ACCEPTED", 
            "对方已接听"
        );
        
        return session;
    }
}
```

### 2️⃣ Android端实现

#### A. JPush SDK集成

**依赖配置**
```kotlin
// app/build.gradle.kts
dependencies {
    // JPush 极光推送 SDK (用于来电通知)
    implementation(files("../jpush-android-5.9.0-release/jpush-android-5.9.0-release/libs/jcore-android-5.2.0.jar"))
    implementation(files("../jpush-android-5.9.0-release/jpush-android-5.9.0-release/libs/jpush-android-5.9.0.jar"))
    
    // VolcEngineRTC SDK for video calling (火山引擎RTC SDK)
    implementation(files("libs/rtc/VolcEngineRTC.jar"))
}
```

**AndroidManifest.xml配置**
```xml
<!-- JPush 权限 -->
<uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

<application>
    <!-- JPush AppKey -->
    <meta-data
        android:name="JPUSH_APPKEY"
        android:value="ff90a2867fcf541a3f3e8ed4" />
    
    <!-- JPush Channel -->
    <meta-data
        android:name="JPUSH_CHANNEL"
        android:value="developer-default" />
    
    <!-- JPush 核心服务 -->
    <service
        android:name="cn.jpush.android.service.PushService"
        android:enabled="true"
        android:exported="false"
        android:process=":multiprocess">
        <intent-filter>
            <action android:name="cn.jpush.android.intent.REGISTER" />
            <action android:name="cn.jpush.android.intent.REPORT" />
            <action android:name="cn.jpush.android.intent.PushService" />
            <action android:name="cn.jpush.android.intent.PUSH_TIME" />
        </intent-filter>
    </service>
    
    <!-- JPush 消息接收器 -->
    <receiver
        android:name=".push.JPushReceiver"
        android:enabled="true"
        android:exported="false">
        <intent-filter>
            <action android:name="cn.jpush.android.intent.REGISTRATION" />
            <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" />
            <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED" />
            <action android:name="cn.jpush.android.intent.NOTIFICATION_OPENED" />
            <category android:name="${applicationId}" />
        </intent-filter>
    </receiver>
    
    <!-- 来电界面（JPush通知触发） -->
    <activity
        android:name=".IncomingCallActivity"
        android:exported="false"
        android:excludeFromRecents="true"
        android:launchMode="singleInstance"
        android:showWhenLocked="true"
        android:turnScreenOn="true"
        android:theme="@style/Theme.MyApplication" />
    
    <!-- 等待接听界面 -->
    <activity
        android:name=".OutgoingCallActivity"
        android:exported="false"
        android:launchMode="singleTop"
        android:theme="@style/Theme.MyApplication" />
    
    <!-- 视频通话页面（火山引擎RTC） -->
    <activity
        android:name=".VideoChatActivity"
        android:exported="false"
        android:theme="@style/Theme.MyApplication"
        android:screenOrientation="portrait"
        android:configChanges="orientation|keyboardHidden|screenSize" />
</application>
```

#### B. 应用初始化

```java
// MyApplication.java
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化 JPush
        initJPush();
    }
    
    /**
     * 初始化极光推送
     */
    private void initJPush() {
        try {
            // 设置调试模式（生产环境请设为false）
            JPushInterface.setDebugMode(true);
            
            // 初始化 JPush
            JPushInterface.init(this);
            
            Log.i(TAG, "JPush 初始化成功");
            
        } catch (Exception e) {
            Log.e(TAG, "JPush 初始化失败", e);
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
}
```

#### C. 推送接收器

```java
// JPushReceiver.java
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();
            
            Log.d(TAG, "收到JPush消息 - Action: " + action);
            
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
                // JPush注册成功
                handleRegistration(bundle);
                
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
                // 收到自定义消息（透传消息）
                handleCustomMessage(context, bundle);
                
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
                // 收到通知
                handleNotificationReceived(context, bundle);
                
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
                // 用户点击了通知
                handleNotificationOpened(context, bundle);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "处理JPush消息异常", e);
        }
    }
    
    /**
     * 处理收到通知
     */
    private void handleNotificationReceived(Context context, Bundle bundle) {
        try {
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            
            Log.d(TAG, "收到通知 - 标题: " + title + ", 内容: " + content);
            
            if (extras != null && !extras.isEmpty()) {
                JSONObject extrasJson = new JSONObject(extras);
                String type = extrasJson.optString("type");
                
                if ("INCOMING_CALL".equals(type)) {
                    // 来电通知 - 启动全屏来电界面
                    String sessionId = extrasJson.optString("sessionId");
                    String callerId = extrasJson.optString("callerId");
                    String callerName = extrasJson.optString("callerName");
                    String callerAvatar = extrasJson.optString("callerAvatar");
                    String callType = extrasJson.optString("callType");
                    
                    Log.i(TAG, "收到来电通知 - sessionId: " + sessionId + ", caller: " + callerName + ", type: " + callType);
                    
                    // 启动来电界面
                    Intent callIntent = new Intent(context, IncomingCallActivity.class);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    callIntent.putExtra("sessionId", sessionId);
                    callIntent.putExtra("callerId", callerId);
                    callIntent.putExtra("callerName", callerName);
                    callIntent.putExtra("callerAvatar", callerAvatar);
                    callIntent.putExtra("callType", callType);
                    
                    context.startActivity(callIntent);
                } else if ("CALL_STATUS".equals(type)) {
                    // 通话状态更新 - 发送本地广播
                    String sessionId = extrasJson.optString("sessionId");
                    String status = extrasJson.optString("status");
                    
                    Intent statusIntent = new Intent("com.example.myapplication.CALL_STATUS_UPDATE");
                    statusIntent.putExtra("sessionId", sessionId);
                    statusIntent.putExtra("status", status);
                    context.sendBroadcast(statusIntent);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "处理通知失败", e);
        }
    }
}
```

#### D. 来电界面

```java
// IncomingCallActivity.java
public class IncomingCallActivity extends AppCompatActivity {
    private static final String TAG = "IncomingCallActivity";
    private static final int TIMEOUT_SECONDS = 30; // 30秒超时
    
    private String sessionId, callerId, callerName, callerAvatar, callType;
    private MediaPlayer ringtone;
    private Vibrator vibrator;
    private Handler timeoutHandler;
    private Runnable timeoutRunnable;
    
    // UI组件
    private TextView tvCallerName, tvCallType;
    private ImageView ivCallerAvatar;
    private Button btnAccept, btnReject;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 全屏显示，锁屏时显示
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        
        setContentView(R.layout.activity_incoming_call);
        
        // 获取来电信息
        sessionId = getIntent().getStringExtra("sessionId");
        callerId = getIntent().getStringExtra("callerId");
        callerName = getIntent().getStringExtra("callerName");
        callerAvatar = getIntent().getStringExtra("callerAvatar");
        callType = getIntent().getStringExtra("callType");
        
        Log.d(TAG, "来电界面启动 - sessionId: " + sessionId + ", caller: " + callerName);
        
        initViews();
        playRingtone();
        vibrate();
        startTimeout();
    }
    
    private void initViews() {
        tvCallerName = findViewById(R.id.tv_caller_name);
        tvCallType = findViewById(R.id.tv_call_type);
        ivCallerAvatar = findViewById(R.id.iv_caller_avatar);
        btnAccept = findViewById(R.id.btn_accept);
        btnReject = findViewById(R.id.btn_reject);
        
        // 显示来电信息
        tvCallerName.setText(callerName != null ? callerName : "未知用户");
        tvCallType.setText("VIDEO".equals(callType) ? "视频通话" : "语音通话");
        
        // TODO: 加载头像
        // Coil.load(callerAvatar).into(ivCallerAvatar);
        
        // 设置按钮
        btnAccept.setOnClickListener(v -> acceptCall());
        btnReject.setOnClickListener(v -> rejectCall());
    }
    
    /**
     * 播放铃声
     */
    private void playRingtone() {
        try {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = MediaPlayer.create(this, ringtoneUri);
            if (ringtone != null) {
                ringtone.setLooping(true);
                ringtone.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "播放铃声失败", e);
        }
    }
    
    /**
     * 振动
     */
    private void vibrate() {
        try {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                long[] pattern = {0, 500, 1000}; // 停0ms, 振500ms, 停1000ms
                vibrator.vibrate(pattern, 0); // 从第0个元素开始循环
            }
        } catch (Exception e) {
            Log.e(TAG, "振动失败", e);
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
     * 开始超时计时
     */
    private void startTimeout() {
        timeoutHandler = new Handler();
        timeoutRunnable = () -> {
            Log.w(TAG, "来电超时，自动拒绝");
            rejectCall();
        };
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_SECONDS * 1000);
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
        
        // 调用后端API拒绝通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return callRejectApi(sessionId);
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                finish();
            }
        }.execute();
    }
    
    /**
     * 调用后端API接受通话
     */
    private boolean callAcceptApi(String sessionId) {
        try {
            // 实现API调用逻辑
            // POST /api/call/accept/{sessionId}
            return true;
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
            // 实现API调用逻辑
            // POST /api/call/reject/{sessionId}
            return true;
        } catch (Exception e) {
            Log.e(TAG, "调用拒绝通话API失败", e);
            return false;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRinging();
        if (timeoutHandler != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }
    }
}
```

#### E. 状态同步机制

**方案1：轮询（简单快速）**
```java
// OutgoingCallActivity.java
private void startStatusPolling() {
    Handler handler = new Handler();
    Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            // 每1秒检查一次状态
            callService.getCallStatus(sessionId, new Callback<ApiResponse<CallStatus>>() {
                @Override
                public void onResponse(Response<ApiResponse<CallStatus>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String status = response.body().getData().getStatus();
                        if ("ACCEPTED".equals(status)) {
                            onCallAccepted();
                        } else if ("REJECTED".equals(status)) {
                            onCallRejected();
                        } else {
                            // 继续轮询
                            handler.postDelayed(this, 1000);
                        }
                    } else {
                        // 继续轮询
                        handler.postDelayed(this, 1000);
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<CallStatus>> call, Throwable t) {
                    // 继续轮询
                    handler.postDelayed(this, 1000);
                }
            });
        }
    };
    handler.post(pollRunnable);
}
```

**方案2：JPush推送状态（推荐）**
```java
// 在OutgoingCallActivity中注册状态接收器
private void registerCallStatusReceiver() {
    callStatusReceiver = new CallStatusReceiver();
    IntentFilter filter = new IntentFilter("com.example.myapplication.CALL_STATUS_UPDATE");
    
    // Android 13+ (API 33+) 需要指定 RECEIVER_NOT_EXPORTED
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(callStatusReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    } else {
        registerReceiver(callStatusReceiver, filter);
    }
}

private class CallStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedSessionId = intent.getStringExtra("sessionId");
        String status = intent.getStringExtra("status");
        
        if (sessionId.equals(receivedSessionId)) {
            Log.d(TAG, "收到通话状态更新 - status: " + status);
            
            if ("ACCEPTED".equals(status)) {
                onCallAccepted();
            } else if ("REJECTED".equals(status)) {
                onCallRejected();
            }
        }
    }
}
```

## 🎯 核心优势

### 1️⃣ 功能完整性
- ✅ 完整的通话流程：发起→通知→接听→通话→结束
- ✅ 类似微信的用户体验
- ✅ 支持锁屏来电、铃声、振动
- ✅ 实时状态同步

### 2️⃣ 技术可行性
- ✅ 后端基础设施已完备
- ✅ 火山引擎RTC功能已可用
- ✅ 只需要补充Android端推送接收
- ✅ 基于现有代码，风险可控

### 3️⃣ 商业价值
- ✅ 可以真正商业化运营
- ✅ 用户愿意付费使用
- ✅ 有完整的计费系统（300元/分钟）
- ✅ 符合通话应用的预期

### 4️⃣ 用户体验
- ✅ 符合用户对通话应用的期望
- ✅ 有明确的来电提示
- ✅ 支持锁屏状态
- ✅ 操作简单直观

## 📋 实施步骤

### 阶段1：基础推送（1天）
1. 集成JPush SDK
2. 配置AndroidManifest.xml
3. 初始化JPush
4. 设置用户别名

### 阶段2：来电界面（1天）
1. 创建IncomingCallActivity
2. 实现铃声和振动
3. 添加超时处理
4. 测试来电界面

### 阶段3：流程打通（1天）
1. 实现接听/拒绝逻辑
2. 添加状态同步（轮询）
3. 双设备测试
4. 完善异常处理

### 阶段4：优化完善（1-2天）
1. 改用JPush推送状态
2. 添加权限申请
3. 集成厂商推送通道
4. 性能优化

## 🔧 权限申请

### 运行时权限
```xml
<!-- 通知权限 -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- 全屏通知权限 -->
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

<!-- 通话权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- 振动权限 -->
<uses-permission android:name="android.permission.VIBRATE" />

<!-- 唤醒权限 -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

### 权限申请时机
- 通知权限：首次启动应用时
- 全屏通知：首次使用通话功能时
- 摄像头/麦克风：进入VideoChatActivity前

## 🚀 厂商推送通道

### 支持的厂商
- 小米 Push
- 华为 Push
- OPPO Push
- Vivo Push
- 魅族 Push
- 荣耀 Push

### 集成步骤
1. 在各厂商开发者平台注册应用
2. 在JPush控制台配置厂商通道参数
3. 在应用中添加厂商SDK依赖
4. 在AndroidManifest.xml配置厂商参数

## 🔍 异常处理

### 网络异常
- 发起通话时网络断开 → 提示"网络异常，请稍后重试"
- 通话过程中断网 → 自动结束通话，提示用户

### 对方不在线
- 后端检查receiver.getIsOnline()
- 前端显示"对方不在线"

### 对方无应答
- 60秒超时后自动取消
- 调用/api/call/end结束会话

### 权限被拒绝
- 无通知权限 → 无法显示来电，引导用户开启
- 无摄像头权限 → 降级为语音通话
- 无麦克风权限 → 无法通话，提示用户

## 📊 测试方案

### 单元测试
- JPush推送功能测试
- 通话状态同步测试
- 权限申请流程测试

### 集成测试
- 双设备端到端测试
- 网络异常场景测试
- 权限拒绝场景测试

### 性能测试
- 推送延迟测试
- 内存使用测试
- 电池消耗测试

## 🎯 总结

这个方案的核心是：
- **保留**现有的火山引擎RTC视频通话功能
- **添加**JPush推送通知系统
- **实现**完整的来电接听流程
- **达到**类似微信的通话体验

技术风险低，实现周期短，商业价值高！

---

**文档版本**: v1.0  
**创建时间**: 2025-01-27  
**最后更新**: 2025-01-27
