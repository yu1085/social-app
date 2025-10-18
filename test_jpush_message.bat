@echo off
chcp 65001
echo ========================================
echo 测试JPush消息发送
echo ========================================

echo 1. 检查设备连接状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices

echo.
echo 2. 检查当前Registration ID...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "Registration ID" | Select-Object -Last 1

echo.
echo 3. 检查JPush初始化状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "JPush.*init" | Select-Object -Last 3

echo.
echo 4. 检查JPushReceiver调用...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "JPushReceiver" | Select-Object -Last 5

echo.
echo 5. 检查推送接收状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "MessageHelper\|NotificationHelper" | Select-Object -Last 5

echo.
echo ========================================
echo 测试完成
echo ========================================
pause

