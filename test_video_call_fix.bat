@echo off
echo 测试视频通话修复...
echo.

echo 1. 清理项目...
call gradlew clean

echo.
echo 2. 构建项目...
call gradlew assembleDebug

if %ERRORLEVEL% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo 3. 安装APK到设备...
adb install -r app\build\outputs\apk\debug\app-debug.apk

if %ERRORLEVEL% neq 0 (
    echo 安装失败！请确保设备已连接且USB调试已开启
    pause
    exit /b 1
)

echo.
echo 4. 启动应用...
adb shell am start -n com.example.myapplication/.LoginActivity

echo.
echo 5. 监控日志...
echo 请观察日志输出，查看是否还有ContextCompat相关错误
echo 按Ctrl+C停止监控
adb logcat -s VideoChatActivity:RTCManager:ByteRTC

pause
