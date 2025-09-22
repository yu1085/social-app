@echo off
echo ========================================
echo Android应用程序调试和启动脚本
echo ========================================
echo.

REM 检查ADB是否可用
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: ADB未找到，请确保Android SDK已安装并添加到PATH
    pause
    exit /b 1
)

echo 1. 检查设备连接状态...
adb devices
echo.

echo 2. 检查应用程序是否已安装...
adb shell pm list packages | findstr com.example.myapplication
if %errorlevel% neq 0 (
    echo 应用程序未安装，正在安装...
    gradlew installDebug
    if %errorlevel% neq 0 (
        echo 安装失败，请检查构建错误
        pause
        exit /b 1
    )
) else (
    echo 应用程序已安装
)

echo.
echo 3. 检查应用程序进程状态...
adb shell ps | findstr com.example.myapplication
if %errorlevel% neq 0 (
    echo 应用程序未运行
) else (
    echo 应用程序正在运行
)

echo.
echo 4. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
if %errorlevel% equ 0 (
    echo 应用程序启动成功！
) else (
    echo 应用程序启动失败
)

echo.
echo 5. 查看应用程序日志（按Ctrl+C停止）...
echo 正在显示日志，按Ctrl+C停止...
adb logcat -s "MyApplication"
