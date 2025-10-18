# 前端 JPush 集成完整指南

## 已完成
- ✅ JPush SDK 依赖已添加到 build.gradle.kts

## 待完成任务

### 1. AndroidManifest.xml 配置

在 `app/src/main/AndroidManifest.xml` 中添加以下配置：

```xml
<!-- JPush 权限 -->
<uses-permission android:name="android.permission.RECEIVE_USER_PRESENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<application>
    <!-- JPush AppKey -->
    <meta-data
        android:name="JPUSH_APPKEY"
        android:value="ff90a2867fcf541a3f3e8ed4" />

    <!-- JPush Channel (可选) -->
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

    <!-- JPush BroadcastReceiver -->
    <receiver
        android:name="cn.jpush.android.service.PushReceiver"
        android:enabled="true"
        android:exported="false">
        <intent-filter android:priority="1000">
            <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED_PROXY" />
            <action android:name="cn.jpush.android.intent.REGISTRATION" />
            <category android:name="${applicationId}" />
        </intent-filter>
    </receiver>

    <!-- 自定义消息接收器 -->
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

    <!-- 来电界面 (全屏Activity) -->
    <activity
        android:name=".IncomingCallActivity"
        android:excludeFromRecents="true"
        android:launchMode="singleInstance"
        android:showWhenLocked="true"
        android:turnScreenOn="true"
        android:theme="@style/Theme.MyApplication" />

    <!-- 等待接听界面 -->
    <activity
        android:name=".OutgoingCallActivity"
        android:launchMode="singleTop"
        android:theme="@style/Theme.MyApplication" />
</application>
```

### 2. 初始化 JPush (在 Application 类中)

创建或修改 `MyApplication.java`:

```java
package com.example.myapplication;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 JPush
        JPushInterface.setDebugMode(true); // 设置Debug模式（生产环境请设为false）
        JPushInterface.init(this);

        // 设置用户别名（登录后调用）
        // JPushInterface.setAlias(this, sequence, userId.toString());
    }
}
```

记得在 AndroidManifest.xml 中声明：
```xml
<application
    android:name=".MyApplication"
    ...>
```

### 3. 创建 JPush 消息接收器

创建 `app/src/main/java/com/example/myapplication/push/JPushReceiver.java`:

```java
package com.example.myapplication.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import com.example.myapplication.IncomingCallActivity;
import org.json.JSONObject;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String action = intent.getAction();

            Log.d(TAG, "收到JPush消息 - Action: " + action);

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
                // 注册成功
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "JPush注册ID: " + regId);

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
                // 收到自定义消息
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

                Log.d(TAG, "收到自定义消息: " + message);
                Log.d(TAG, "附加数据: " + extras);

                handleCustomMessage(context, message, extras);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
                // 收到通知
                String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                String content = bundle.getString(JPushInterface.EXTRA_ALERT);
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

                Log.d(TAG, "收到通知 - 标题: " + title + ", 内容: " + content);

                handleNotification(context, title, content, extras);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
                // 用户点击了通知
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Log.d(TAG, "用户点击通知 - 附加数据: " + extras);

                handleNotificationClick(context, extras);
            }

        } catch (Exception e) {
            Log.e(TAG, "处理JPush消息异常", e);
        }
    }

    private void handleCustomMessage(Context context, String message, String extrasJson) {
        try {
            if (extrasJson == null) return;

            JSONObject extras = new JSONObject(extrasJson);
            String type = extras.optString("type");

            if ("CALL_STATUS".equals(type)) {
                // 通话状态更新
                String sessionId = extras.optString("sessionId");
                String status = extras.optString("status");
                Log.d(TAG, "通话状态更新 - sessionId: " + sessionId + ", status: " + status);

                // TODO: 通知正在等待的OutgoingCallActivity更新状态
                Intent statusIntent = new Intent("com.example.myapplication.CALL_STATUS_UPDATE");
                statusIntent.putExtra("sessionId", sessionId);
                statusIntent.putExtra("status", status);
                context.sendBroadcast(statusIntent);
            }

        } catch (Exception e) {
            Log.e(TAG, "处理自定义消息异常", e);
        }
    }

    private void handleNotification(Context context, String title, String content, String extrasJson) {
        try {
            if (extrasJson == null) return;

            JSONObject extras = new JSONObject(extrasJson);
            String type = extras.optString("type");

            if ("INCOMING_CALL".equals(type)) {
                // 来电通知 - 启动全屏来电界面
                String sessionId = extras.optString("sessionId");
                String callerId = extras.optString("callerId");
                String callerName = extras.optString("callerName");
                String callerAvatar = extras.optString("callerAvatar");
                String callType = extras.optString("callType");

                Log.d(TAG, "收到来电通知 - sessionId: " + sessionId + ", callType: " + callType);

                Intent callIntent = new Intent(context, IncomingCallActivity.class);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                callIntent.putExtra("sessionId", sessionId);
                callIntent.putExtra("callerId", callerId);
                callIntent.putExtra("callerName", callerName);
                callIntent.putExtra("callerAvatar", callerAvatar);
                callIntent.putExtra("callType", callType);

                context.startActivity(callIntent);
            }

        } catch (Exception e) {
            Log.e(TAG, "处理通知异常", e);
        }
    }

    private void handleNotificationClick(Context context, String extrasJson) {
        try {
            if (extrasJson == null) return;

            JSONObject extras = new JSONObject(extrasJson);
            String type = extras.optString("type");

            if ("INCOMING_CALL".equals(type)) {
                String sessionId = extras.optString("sessionId");
                // 用户点击了来电通知，跳转到来电界面
                // (通常通知已经自动启动IncomingCallActivity，这里不需要额外处理)
                Log.d(TAG, "用户点击来电通知 - sessionId: " + sessionId);
            }

        } catch (Exception e) {
            Log.e(TAG, "处理通知点击异常", e);
        }
    }
}
```

### 4. 创建来电界面

创建 `IncomingCallActivity.java` - 这是最关键的界面，需要：
- 全屏显示
- 显示来电者信息
- 接听/拒绝按钮
- 播放铃声和震动

### 5. 创建等待接听界面

创建 `OutgoingCallActivity.java` - 发起方等待界面，需要：
- 显示接收方信息
- "等待对方接听..."提示
- 取消按钮
- 监听通话状态更新

### 6. 修改 UserDetailActivity

在视频通话按钮点击后，不要直接跳转到 VideoChatActivity，而是：
1. 先跳转到 OutgoingCallActivity
2. OutgoingCallActivity 显示等待状态
3. 收到 ACCEPTED 状态后再跳转到 VideoChatActivity

### 7. 用户登录后设置别名

在用户登录成功后（AuthManager 或 LoginActivity），调用：

```java
// 设置JPush别名为用户ID
int sequence = 1;
JPushInterface.setAlias(context, sequence, String.valueOf(userId));
```

这样后端就能通过用户ID推送通知。

## 测试流程

1. 安装并运行应用
2. 使用 video_caller 账号登录（ID: 23820512）
3. 进入 video_receiver 用户详情页（ID: 22491729）
4. 点击"视频通话"按钮
5. 后端会发送JPush通知给 video_receiver
6. video_receiver 应该收到全屏来电通知

## 注意事项

1. **Android 13+ 需要请求通知权限**
2. **测试时需要两台设备或使用模拟器**
3. **确保后端服务运行在可访问的地址**
4. **JPush 调试模式下可查看详细日志**
5. **厂商推送渠道配置可提高送达率**

## 下一步

由于实现完整功能需要大量代码和布局文件，建议：

1. 先测试 JPush 基本功能（收发消息）
2. 逐步实现来电界面UI
3. 完善通话状态同步逻辑
4. 添加铃声、震动等交互效果
