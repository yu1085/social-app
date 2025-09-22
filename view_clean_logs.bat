@echo off
echo ========================================
echo 查看应用程序日志（过滤系统警告）
echo ========================================
echo.

echo 正在查看应用程序相关日志...
echo 已过滤掉fs-verity和其他系统警告
echo 按Ctrl+C停止查看日志
echo.

REM 过滤掉VerityUtils错误和其他系统警告，只显示应用程序相关日志
adb logcat | findstr /v "VerityUtils\|Failed to measure fs-verity\|system_server.*E\|system_server.*W" | findstr "com.example.myapplication\|MyApplication\|LoginActivity\|MainActivity\|AccountManagementActivity\|ProfileActivity\|SquareActivity\|MessageActivity"
