package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 安全的Toast工具类，避免DeadObjectException
 */
public class SafeToast {
    
    /**
     * 安全显示Toast，检查Activity状态
     */
    public static void show(Activity activity, String message, int duration) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        
        try {
            Toast.makeText(activity, message, duration).show();
        } catch (Exception e) {
            // 忽略Toast显示异常
            android.util.Log.w("SafeToast", "Toast显示失败: " + e.getMessage());
        }
    }
    
    /**
     * 安全显示Toast，使用Application Context
     */
    public static void show(Context context, String message, int duration) {
        if (context == null) {
            return;
        }
        
        try {
            // 使用Application Context避免Activity生命周期问题
            Context appContext = context.getApplicationContext();
            Toast.makeText(appContext, message, duration).show();
        } catch (Exception e) {
            // 忽略Toast显示异常
            android.util.Log.w("SafeToast", "Toast显示失败: " + e.getMessage());
        }
    }
    
    /**
     * 显示短时间Toast
     */
    public static void showShort(Activity activity, String message) {
        show(activity, message, Toast.LENGTH_SHORT);
    }
    
    /**
     * 显示长时间Toast
     */
    public static void showLong(Activity activity, String message) {
        show(activity, message, Toast.LENGTH_LONG);
    }
}
