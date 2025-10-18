
@echo off
chcp 65001 >nul
echo ========================================
echo 测试JPush配置修复
echo ========================================

echo.
echo 1. 检查当前目录...
dir
echo.

echo 2. 检查gradlew文件...
if exist "gradlew.bat" (
    echo ✅ 找到 gradlew.bat
) else (
    echo ❌ 未找到 gradlew.bat，请确保在项目根目录运行此脚本
    pause
    exit /b 1
)

echo.
echo 3. 重新编译应用...
call gradlew.bat clean assembleDebug
if %errorlevel% neq 0 (
    echo ❌ 编译失败
    pause
    exit /b 1
)
echo ✅ 编译成功

echo.
echo 4. 查找生成的APK文件...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ 找到APK文件: app\build\outputs\apk\debug\app-debug.apk
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
) else (
    echo ❌ 未找到APK文件
    pause
    exit /b 1
)

echo.
echo 5. 检查ADB连接...
adb devices
echo.

echo 6. 安装到设备...
adb install -r "%APK_PATH%"
if %errorlevel% neq 0 (
    echo ❌ 安装失败
    pause
    exit /b 1
)
echo ✅ 安装成功

echo.
echo 7. 启动应用并查看日志...
echo 请手动启动应用，然后查看以下日志：
echo.
echo 查找以下关键日志：
echo ✅ "JPush 初始化成功"
echo ✅ "Registration ID获取成功"
echo ❌ "check config failed" (应该不再出现)
echo.
echo 8. 测试推送功能...
echo 发起一次视频通话，观察接收方是否收到通知
echo.
echo ========================================
echo 测试完成！
echo ========================================
pause
