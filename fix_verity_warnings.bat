@echo off
echo ========================================
echo 修复fs-verity警告和优化应用程序安装
echo ========================================
echo.

echo 1. 检查当前应用程序状态...
adb shell pm list packages | findstr com.example.myapplication
echo.

echo 2. 停止应用程序进程...
adb shell am force-stop com.example.myapplication
echo.

echo 3. 卸载当前应用程序...
adb uninstall com.example.myapplication
echo.

echo 4. 清理构建缓存...
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"
if exist ".gradle" rmdir /s /q ".gradle"
echo 缓存清理完成
echo.

echo 5. 重新构建应用程序...
gradlew clean
gradlew assembleDebug
if %errorlevel% neq 0 (
    echo 构建失败，请检查错误信息
    pause
    exit /b 1
)
echo 构建成功
echo.

echo 6. 安装应用程序...
gradlew installDebug
if %errorlevel% neq 0 (
    echo 安装失败，请检查错误信息
    pause
    exit /b 1
)
echo 安装成功
echo.

echo 7. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 8. 查看应用程序日志（按Ctrl+C停止）...
echo 正在显示日志，按Ctrl+C停止...
adb logcat | findstr "com.example.myapplication\|MyApplication\|LoginActivity\|MainActivity\|AccountManagementActivity"
