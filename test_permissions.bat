@echo off
echo ========================================
echo 权限测试和修复脚本
echo ========================================
echo.

echo 1. 检查设备连接...
adb devices
echo.

echo 2. 检查当前权限状态...
adb shell dumpsys package com.example.myapplication | findstr "permission"
echo.

echo 3. 重新安装应用程序（包含新权限）...
gradle-8.9\bin\gradle.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败
    pause
    exit /b 1
)
echo 安装成功
echo.

echo 4. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 5. 等待应用程序启动...
timeout /t 5 /nobreak >nul

echo 6. 检查应用程序进程...
adb shell ps | findstr com.example.myapplication
echo.

echo 7. 查看权限相关日志...
echo 正在查看权限日志（按Ctrl+C停止）...
adb logcat | findstr "EditAlbumActivity\|permission\|Permission"
