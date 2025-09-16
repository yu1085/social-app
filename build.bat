@echo off
echo 正在清理项目...
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"
if exist ".gradle" rmdir /s /q ".gradle"

echo 正在同步项目...
echo 请在 Android Studio 中执行以下操作：
echo 1. 打开项目
echo 2. 等待 Gradle 同步完成
echo 3. 如果出现错误，请检查 SDK 版本和 Compose 版本兼容性

echo.
echo 项目配置已更新为兼容版本：
echo - Kotlin: 1.9.10
echo - Compose Compiler: 1.5.3
echo - Compose BOM: 2023.10.01
echo - Gradle: 8.4

pause
