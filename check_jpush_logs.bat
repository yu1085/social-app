@echo off
echo ========================================
echo 检查JPush日志
echo ========================================

echo.
echo 检查设备1 (emulator-5554) 的JPush日志...
"C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5554 logcat -d -s MyApplication JPushReceiver | findstr /i "JPush\|Registration\|check config"

echo.
echo 检查设备2 (emulator-5556) 的JPush日志...
"C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe" -s emulator-5556 logcat -d -s MyApplication JPushReceiver | findstr /i "JPush\|Registration\|check config"

echo.
echo ========================================
echo 日志检查完成！
echo ========================================
pause
