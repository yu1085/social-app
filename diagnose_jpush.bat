@echo off
chcp 65001
echo ========================================
echo JPush 问题诊断
echo ========================================

echo 1. 检查设备连接状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" devices

echo.
echo 2. 检查JPush初始化状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "JPush.*init\|Registration ID" | Select-Object -Last 5

echo.
echo 3. 检查JPushReceiver调用状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "JPushReceiver" | Select-Object -Last 5

echo.
echo 4. 检查推送接收状态...
& "C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d | findstr "MessageHelper\|NotificationHelper" | Select-Object -Last 5

echo.
echo 5. 检查极光控制台推送设置...
echo 请检查极光控制台：
echo - 推送类型：应该选择"通知"而不是"自定义消息"
echo - 推送内容：确保填写了通知标题和内容
echo - 目标用户：确保选择了正确的Registration ID

echo.
echo ========================================
echo 诊断完成
echo ========================================
pause
