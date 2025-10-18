package com.example.myapplication.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log

/**
 * 应用状态工具类
 * 用于检测应用是否在前台运行
 */
object AppStateUtils {
    
    private const val TAG = "AppStateUtils"
    
    /**
     * 检查应用是否在前台运行
     * 使用多种方法确保检测准确性，特别是针对模拟器
     * 
     * @param context 上下文
     * @return true-前台，false-后台
     */
    fun isAppInForeground(context: Context): Boolean {
        try {
            // 方法1: 检查当前Activity
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if (activityManager != null) {
                val runningProcesses = activityManager.runningAppProcesses
                if (runningProcesses != null) {
                    for (processInfo in runningProcesses) {
                        if (processInfo.processName == context.packageName) {
                            val isForeground = processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            Log.i(TAG, "=== 应用前台状态检测 ===")
                            Log.i(TAG, "进程名: ${processInfo.processName}")
                            Log.i(TAG, "重要性级别: ${processInfo.importance}")
                            Log.i(TAG, "前台级别: ${ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND}")
                            Log.i(TAG, "是否前台: $isForeground")
                            
                            // 方法2: 检查是否有可见Activity（针对模拟器优化）
                            if (isForeground) {
                                Log.i(TAG, "方法1检测为前台")
                                return true
                            }
                            
                            // 方法3: 检查应用是否在顶层（模拟器备用方案）
                            try {
                                val runningTasks = activityManager.getRunningTasks(1)
                                if (runningTasks != null && runningTasks.isNotEmpty()) {
                                    val topTask = runningTasks[0]
                                    val topActivity = topTask.topActivity
                                    val isTopApp = topActivity != null && 
                                                 topActivity.packageName == context.packageName
                                    Log.i(TAG, "方法3检测 - isTopApp: $isTopApp")
                                    if (isTopApp) {
                                        Log.i(TAG, "方法3检测为前台")
                                        return true
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "方法3检测失败", e)
                            }
                            
                            Log.i(TAG, "========================")
                            return false
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "检查应用前台状态失败", e)
        }
        
        // 如果检测失败，默认返回false（后台）
        Log.w(TAG, "无法检测应用状态，默认认为在后台")
        return false
    }
    
    /**
     * 检查应用是否在后台运行
     * 
     * @param context 上下文
     * @return true-后台，false-前台
     */
    fun isAppInBackground(context: Context): Boolean {
        return !isAppInForeground(context)
    }
}
