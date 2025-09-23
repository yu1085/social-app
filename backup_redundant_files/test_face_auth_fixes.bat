@echo off
echo ========================================
echo 测试实人认证修复效果
echo ========================================

echo.
echo 1. 检查相机权限...
adb shell dumpsys package com.example.myapplication | findstr "android.permission.CAMERA"

echo.
echo 2. 启动应用...
adb shell am start -n com.example.myapplication/.RealPersonAuthActivity

echo.
echo 3. 等待应用启动...
timeout /t 3 /nobreak > nul

echo.
echo 4. 开始监控日志...
echo 请观察以下关键日志：
echo - 相机启动成功
echo - 人脸检测状态
echo - 实人认证流程
echo - 错误信息（如果有）
echo.
echo 按 Ctrl+C 停止监控
echo.

adb logcat -s "AliyunFaceAuthService:D" "CameraX:D" "FaceCameraPreview:D" "RealPersonAuthActivity:D"
