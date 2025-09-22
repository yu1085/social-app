@echo off
echo ========================================
echo 查看应用程序日志
echo ========================================
echo.

REM 检查ADB是否可用
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: ADB未找到，请确保Android SDK已安装并添加到PATH
    pause
    exit /b 1
)

echo 正在查看 com.example.myapplication 的日志...
echo 按Ctrl+C停止查看日志
echo.

REM 过滤应用程序相关的日志
adb logcat | findstr "com.example.myapplication\|MyApplication\|MyApplicationActivity\|LoginActivity\|MainActivity\|AccountManagementActivity"
