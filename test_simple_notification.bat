@echo off
echo ========================================
echo 测试简单通知功能
echo ========================================

echo.
echo 1. 检查应用通知权限...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 shell dumpsys notification | findstr "com.example.myapplication"

echo.
echo 2. 检查JPush日志...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "JPushReceiver\|handleCustomMessage" | Select-Object -Last 5

echo.
echo 3. 建议：
echo - 推送确实到达了设备（日志显示收到580字节数据）
echo - 但JPushReceiver可能没有正确处理
echo - 建议使用真机测试或检查通知权限
echo.
pause
