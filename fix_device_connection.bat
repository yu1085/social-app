@echo off
echo ========================================
echo 修复设备连接和应用程序问题
echo ========================================
echo.

echo 1. 停止ADB服务...
adb kill-server
timeout /t 2 /nobreak >nul

echo 2. 启动ADB服务...
adb start-server
timeout /t 3 /nobreak >nul

echo 3. 检查设备连接...
adb devices
echo.

echo 4. 如果设备未连接，请检查：
echo    - 模拟器是否正在运行
echo    - 模拟器是否完全启动
echo    - USB调试是否启用
echo.

echo 5. 尝试重新连接模拟器...
adb connect emulator-5554
timeout /t 2 /nobreak >nul

echo 6. 再次检查设备状态...
adb devices
echo.

echo 7. 如果设备已连接，强制停止应用程序...
adb shell am force-stop com.example.myapplication
echo.

echo 8. 重新安装应用程序...
gradle-8.9\bin\gradle.bat installDebug
echo.

echo 9. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 10. 检查应用程序状态...
adb shell ps | findstr com.example.myapplication
echo.

echo 修复完成！
pause
