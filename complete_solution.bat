@echo off
echo ========================================
echo 完整解决方案 - 设备连接和应用程序问题
echo ========================================
echo.

echo 问题诊断：
echo - 错误: "Device is offline" 或 "No connected devices"
echo - 原因: Android模拟器未运行或ADB连接断开
echo.

echo 解决方案：
echo.
echo 1. 在Android Studio中：
echo    - 打开 AVD Manager (Tools > AVD Manager)
echo    - 启动 Pixel 6a API 33 模拟器
echo    - 等待模拟器完全启动（显示主屏幕）
echo.
echo 2. 或者使用命令行启动模拟器：
echo    - 运行 start_emulator.bat
echo.
echo 3. 模拟器启动后，运行以下命令：
echo    - adb devices (应该显示设备)
echo    - gradle-8.9\bin\gradle.bat installDebug
echo    - adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 4. 如果模拟器启动失败，请检查：
echo    - Android SDK是否正确安装
echo    - 系统环境变量 ANDROID_HOME 是否设置
echo    - 模拟器镜像是否下载完整
echo.

echo 5. 临时解决方案（如果模拟器有问题）：
echo    - 使用真机调试
echo    - 在真机上启用USB调试
echo    - 连接USB线到电脑
echo.

echo 当前状态检查：
echo.
adb devices
echo.

if %errorlevel% equ 0 (
    echo 如果上面显示了设备，请运行：
    echo gradle-8.9\bin\gradle.bat installDebug
) else (
    echo 没有检测到设备，请先启动模拟器或连接真机
)

echo.
echo 需要帮助？请告诉我：
echo 1. 您是否能看到Android Studio中的AVD Manager？
echo 2. 您是否有Android真机可以用于调试？
echo 3. 您是否愿意重新安装Android SDK？
echo.
pause
