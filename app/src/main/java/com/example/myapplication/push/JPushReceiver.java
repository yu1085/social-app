package com.example.myapplication.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.JPushMessageReceiver;
import com.example.myapplication.IncomingCallActivity;
import org.json.JSONObject;

/**
 * JPush 消息接收器
 * 处理来电通知和通话状态更新
 */
public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "JPushReceiver";

    public JPushReceiver() {
        super();
        Log.d(TAG, "JPushReceiver 构造函数被调用");
    }

    @Override
    public void onMessage(Context context, cn.jpush.android.api.CustomMessage customMessage) {
        super.onMessage(context, customMessage);
        try {
            Log.i(TAG, "=== JPushReceiver.onMessage 被调用 ===");
            Log.i(TAG, "收到自定义消息: " + customMessage.message);
            Log.i(TAG, "消息额外数据: " + customMessage.extra);
            handleCustomMessage(context, customMessage);
        } catch (Exception e) {
            Log.e(TAG, "处理自定义消息异常", e);
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, cn.jpush.android.api.NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        try {
            Log.i(TAG, "=== 收到JPush通知消息 ===");
            Log.i(TAG, "通知内容: " + notificationMessage.notificationContent);
            Log.i(TAG, "通知额外数据: " + notificationMessage.notificationExtras);
            Log.i(TAG, "通知标题: " + notificationMessage.notificationTitle);
            Log.i(TAG, "通知类型: " + notificationMessage.notificationType);
            
            // 检查应用是否在前台运行
            boolean isAppInForeground = isAppInForeground(context);
            Log.i(TAG, "应用状态 - 前台: " + isAppInForeground);
            
            if (isAppInForeground) {
                // 应用在前台 - 直接处理，不显示通知栏
                Log.i(TAG, "应用在前台，直接处理通知，不显示通知栏");
                handleNotificationReceived(context, notificationMessage);
            } else {
                // 应用在后台 - 显示通知栏 + 处理
                Log.i(TAG, "应用在后台，显示通知栏并处理");
                createCustomNotification(context, notificationMessage.notificationContent, notificationMessage.notificationExtras);
                handleNotificationReceived(context, notificationMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "处理通知消息异常", e);
        }
    }

    @Override
    public void onNotifyMessageOpened(Context context, cn.jpush.android.api.NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        try {
            Log.d(TAG, "用户点击了通知: " + notificationMessage.notificationContent);
            handleNotificationOpened(context, notificationMessage);
        } catch (Exception e) {
            Log.e(TAG, "处理通知点击异常", e);
        }
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        super.onRegister(context, registrationId);
        try {
            Log.d(TAG, "JPush注册成功: " + registrationId);
            handleRegistration(context, registrationId);
        } catch (Exception e) {
            Log.e(TAG, "处理注册成功异常", e);
        }
    }

    /**
     * 处理JPush注册成功
     */
    private void handleRegistration(Context context, String registrationId) {
        try {
            Log.i(TAG, "JPush注册成功 - registrationId: " + registrationId);
        } catch (Exception e) {
            Log.e(TAG, "处理注册成功失败", e);
        }
    }

    /**
     * 处理自定义消息（透传消息）
     */
    private void handleCustomMessage(Context context, cn.jpush.android.api.CustomMessage customMessage) {
        try {
            String message = customMessage.message;
            String extras = customMessage.extra;

            Log.i(TAG, "处理自定义消息 - message: " + message);
            Log.i(TAG, "处理自定义消息 - extras: " + extras);

            // 解析消息内容
            if (extras != null && !extras.isEmpty()) {
                JSONObject extrasJson = new JSONObject(extras);
                String messageType = extrasJson.optString("type");

                if ("CALL_STATUS".equals(messageType)) {
                    String sessionId = extrasJson.optString("sessionId");
                    String status = extrasJson.optString("status");
                    String statusMessage = extrasJson.optString("message", "");

                    Log.i(TAG, "处理通话状态更新(自定义消息) - sessionId: " + sessionId + ", status: " + status + ", message: " + statusMessage);

                    // 广播状态更新
                    Intent statusIntent = new Intent("com.example.myapplication.CALL_STATUS_UPDATE");
                    statusIntent.putExtra("sessionId", sessionId);
                    statusIntent.putExtra("status", status);
                    statusIntent.putExtra("message", statusMessage);
                    context.sendBroadcast(statusIntent);

                    Log.i(TAG, "已发送通话状态广播(自定义消息) - sessionId: " + sessionId + ", status: " + status);

                    // 显示状态通知
                    showCallStatusNotification(context, status, statusMessage, sessionId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "处理自定义消息失败", e);
        }
    }

    /**
     * 处理通知消息
     */
    private void handleNotificationReceived(Context context, cn.jpush.android.api.NotificationMessage notificationMessage) {
        try {
            String notificationContent = notificationMessage.notificationContent;
            String extras = notificationMessage.notificationExtras;

            Log.i(TAG, "处理通知消息 - content: " + notificationContent);
            Log.i(TAG, "处理通知消息 - extras: " + extras);

            // 解析额外数据，处理状态更新
            if (extras != null && !extras.isEmpty()) {
                JSONObject extrasJson = new JSONObject(extras);
                String type = extrasJson.optString("type");
                
                Log.i(TAG, "通知消息类型: " + type);
                Log.i(TAG, "通知消息extras: " + extras);
                
                if ("INCOMING_CALL".equals(type)) {
                    // 处理来电通知
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
                    String sessionId = extrasJson.optString("sessionId");
                    String status = extrasJson.optString("status");
                    String statusMessage = extrasJson.optString("message", "");

                    Log.i(TAG, "处理通话状态更新 - sessionId: " + sessionId + ", status: " + status + ", message: " + statusMessage);

                    // 广播状态更新
                    Intent statusIntent = new Intent("com.example.myapplication.CALL_STATUS_UPDATE");
                    statusIntent.putExtra("sessionId", sessionId);
                    statusIntent.putExtra("status", status);
                    statusIntent.putExtra("message", statusMessage);
                    context.sendBroadcast(statusIntent);

                    Log.i(TAG, "已发送通话状态广播 - sessionId: " + sessionId + ", status: " + status);

                    // 显示状态通知
                    showCallStatusNotification(context, status, statusMessage, sessionId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "处理通知消息失败", e);
        }
    }

    /**
     * 处理通知点击
     */
    private void handleNotificationOpened(Context context, cn.jpush.android.api.NotificationMessage notificationMessage) {
        try {
            String notificationContent = notificationMessage.notificationContent;
            String extras = notificationMessage.notificationExtras;

            Log.i(TAG, "处理通知点击 - content: " + notificationContent);
            Log.i(TAG, "处理通知点击 - extras: " + extras);

            // 解析额外数据
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
                    
                    Log.i(TAG, "收到来电通知点击 - sessionId: " + sessionId + ", caller: " + callerName + ", type: " + callType);
                    
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
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "处理通知点击失败", e);
        }
    }

    /**
     * 备用通知机制 - 当JPush配置失败时使用
     * 通过系统通知栏显示来电信息
     */
    private void showFallbackNotification(Context context, String callerName, String callType, String sessionId) {
        try {
            Log.i(TAG, "使用备用通知机制 - caller: " + callerName + ", type: " + callType);
            
            // 创建通知渠道（Android 8.0+）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
                if (notificationManager != null) {
                    android.app.NotificationChannel channel = new android.app.NotificationChannel(
                            "fallback_call_channel",
                            "备用通话通知",
                            android.app.NotificationManager.IMPORTANCE_HIGH
                    );
                    channel.setDescription("当JPush配置失败时的备用通知");
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // 创建通知
            android.content.Intent callIntent = new android.content.Intent(context, IncomingCallActivity.class);
            callIntent.putExtra("sessionId", sessionId);
            callIntent.putExtra("callerName", callerName);
            callIntent.putExtra("callType", callType);
            
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                    context, 
                    0, 
                    callIntent, 
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );

            String title = "VIDEO".equals(callType) ? "视频通话" : "语音通话";
            String content = callerName + " 向您发起" + ("VIDEO".equals(callType) ? "视频" : "语音") + "通话";

            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(context, "fallback_call_channel")
                    .setSmallIcon(android.R.drawable.ic_menu_call)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setCategory(androidx.core.app.NotificationCompat.CATEGORY_CALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(1001, builder.build());
                Log.i(TAG, "备用通知已发送");
            }

        } catch (Exception e) {
            Log.e(TAG, "发送备用通知失败", e);
        }
    }


    /**
     * 显示通话状态通知
     */
    private void showCallStatusNotification(Context context, String status, String message, String sessionId) {
        try {
            Log.i(TAG, "显示通话状态通知 - status: " + status + ", message: " + message);
            
            // 创建通知渠道（Android 8.0+）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
                if (notificationManager != null) {
                    android.app.NotificationChannel channel = new android.app.NotificationChannel(
                            "call_status_channel",
                            "通话状态通知",
                            android.app.NotificationManager.IMPORTANCE_DEFAULT
                    );
                    channel.setDescription("通话状态更新通知");
                    channel.enableVibration(false);
                    channel.enableLights(false);
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // 创建通知
            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(context, "call_status_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("通话状态")
                    .setContentText(message)
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                // 使用不同的通知ID避免覆盖
                int notificationId = 2000 + (sessionId != null ? sessionId.hashCode() % 1000 : 0);
                notificationManager.notify(notificationId, builder.build());
                Log.i(TAG, "通话状态通知已显示 - ID: " + notificationId);
            }

        } catch (Exception e) {
            Log.e(TAG, "显示通话状态通知失败", e);
        }
    }

    /**
     * 检查应用是否在前台运行
     */
    private boolean isAppInForeground(Context context) {
        try {
            android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                java.util.List<android.app.ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
                if (runningProcesses != null) {
                    for (android.app.ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                        if (processInfo.processName.equals(context.getPackageName())) {
                            return processInfo.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "检查应用前台状态失败", e);
        }
        return false;
    }

    /**
     * 手动创建通知，绕过JPush的图标问题
     */
    private void createCustomNotification(Context context, String content, String extras) {
        try {
            Log.i(TAG, "手动创建通知 - content: " + content);
            
            // 创建通知渠道（Android 8.0+）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
                if (notificationManager != null) {
                    android.app.NotificationChannel channel = new android.app.NotificationChannel(
                            "jpush_custom_channel",
                            "JPush自定义通知",
                            android.app.NotificationManager.IMPORTANCE_DEFAULT
                    );
                    channel.setDescription("JPush自定义通知渠道");
                    channel.enableVibration(true);
                    channel.enableLights(true);
                    notificationManager.createNotificationChannel(channel);
                }
            }

            // 创建通知
            androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(context, "jpush_custom_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("SocialMeet")
                    .setContentText(content != null ? content : "收到新消息")
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // 添加额外数据
            if (extras != null && !extras.isEmpty()) {
                builder.setStyle(new androidx.core.app.NotificationCompat.BigTextStyle()
                        .bigText("内容: " + content + "\n额外数据: " + extras));
            }

            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(3000, builder.build());
                Log.i(TAG, "自定义通知已创建并显示");
            }

        } catch (Exception e) {
            Log.e(TAG, "创建自定义通知失败", e);
        }
    }
}
