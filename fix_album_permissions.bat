@echo off
echo ========================================
echo 修复相册权限问题
echo ========================================
echo.

echo 问题分析：
echo - Android 13+ 使用新的媒体权限系统
echo - 需要 READ_MEDIA_IMAGES 权限而不是 READ_EXTERNAL_STORAGE
echo - 权限请求逻辑需要更新
echo.

echo 解决方案：
echo 1. 更新 AndroidManifest.xml 权限声明
echo 2. 修改权限检查逻辑支持 Android 13+
echo 3. 重新编译和安装应用程序
echo.

echo 开始修复...
echo.

echo 1. 清理项目...
gradle-8.9\bin\gradle.bat clean
echo.

echo 2. 重新编译项目...
gradle-8.9\bin\gradle.bat assembleDebug
if %errorlevel% neq 0 (
    echo 编译失败，请检查错误信息
    pause
    exit /b 1
)
echo 编译成功
echo.

echo 3. 安装应用程序...
gradle-8.9\bin\gradle.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败，请检查设备连接
    pause
    exit /b 1
)
echo 安装成功
echo.

echo 4. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 5. 检查权限状态...
adb shell dumpsys package com.example.myapplication | findstr "permission"
echo.

echo 修复完成！
echo.
echo 测试步骤：
echo 1. 打开应用程序
echo 2. 进入个人中心
echo 3. 点击编辑相册
echo 4. 尝试上传照片
echo 5. 如果仍有问题，请检查系统权限设置
echo.
pause
