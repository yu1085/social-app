@echo off
echo ========================================
echo 快速修复应用程序问题
echo ========================================
echo.

echo 步骤1: 重启ADB服务
adb kill-server
timeout /t 2 /nobreak >nul
adb start-server
echo ADB服务已重启
echo.

echo 步骤2: 检查设备连接
adb devices
echo.

echo 步骤3: 如果看到设备，强制停止应用程序
adb shell am force-stop com.example.myapplication
echo 应用程序已停止
echo.

echo 步骤4: 重新安装应用程序
gradle-8.9\bin\gradle.bat installDebug
echo.

echo 步骤5: 启动应用程序
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 步骤6: 检查应用程序状态
adb shell ps | findstr com.example.myapplication
echo.

echo 修复完成！
echo 如果仍有问题，请：
echo 1. 确保模拟器正在运行
echo 2. 在Android Studio中重新启动模拟器
echo 3. 运行 start_emulator.bat 启动模拟器
echo.
pause
