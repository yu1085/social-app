@echo off
echo ========================================
echo Android应用程序管理器
echo ========================================
echo.

:menu
echo 请选择操作：
echo 1. 启动应用程序
echo 2. 停止应用程序
echo 3. 重新安装应用程序
echo 4. 查看应用程序日志
echo 5. 查看清洁日志（过滤系统警告）
echo 6. 检查应用程序状态
echo 7. 清理并重建项目
echo 8. 修复fs-verity警告
echo 9. 退出
echo.

set /p choice=请输入选择 (1-9): 

if "%choice%"=="1" goto start_app
if "%choice%"=="2" goto stop_app
if "%choice%"=="3" goto reinstall_app
if "%choice%"=="4" goto view_logs
if "%choice%"=="5" goto view_clean_logs
if "%choice%"=="6" goto check_status
if "%choice%"=="7" goto clean_rebuild
if "%choice%"=="8" goto fix_verity
if "%choice%"=="9" goto exit
echo 无效选择，请重新输入
goto menu

:start_app
echo 正在启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
if %errorlevel% equ 0 (
    echo 应用程序启动成功！
) else (
    echo 应用程序启动失败
)
echo.
goto menu

:stop_app
echo 正在停止应用程序...
adb shell am force-stop com.example.myapplication
echo 应用程序已停止
echo.
goto menu

:reinstall_app
echo 正在重新安装应用程序...
gradlew installDebug
if %errorlevel% equ 0 (
    echo 应用程序安装成功！
) else (
    echo 应用程序安装失败
)
echo.
goto menu

:view_logs
echo 正在查看应用程序日志（按Ctrl+C停止）...
adb logcat | findstr "com.example.myapplication\|MyApplication\|LoginActivity\|MainActivity"
goto menu

:view_clean_logs
echo 正在查看清洁的应用程序日志（已过滤系统警告）...
echo 按Ctrl+C停止查看日志
adb logcat | findstr /v "VerityUtils\|Failed to measure fs-verity\|system_server.*E\|system_server.*W" | findstr "com.example.myapplication\|MyApplication\|LoginActivity\|MainActivity\|AccountManagementActivity\|ProfileActivity\|SquareActivity\|MessageActivity"
goto menu

:check_status
echo 检查设备连接状态...
adb devices
echo.
echo 检查应用程序安装状态...
adb shell pm list packages | findstr com.example.myapplication
echo.
echo 检查应用程序进程状态...
adb shell ps | findstr com.example.myapplication
echo.
goto menu

:clean_rebuild
echo 正在清理项目...
if exist "app\build" rmdir /s /q "app\build"
if exist "build" rmdir /s /q "build"
if exist ".gradle" rmdir /s /q ".gradle"
echo 项目清理完成
echo.
echo 正在重新构建项目...
gradlew build
if %errorlevel% equ 0 (
    echo 项目构建成功！
    echo 正在安装应用程序...
    gradlew installDebug
) else (
    echo 项目构建失败
)
echo.
goto menu

:fix_verity
echo 正在修复fs-verity警告...
echo 这将重新安装应用程序以解决验证问题
echo.
call fix_verity_warnings.bat
echo.
goto menu

:exit
echo 感谢使用！
exit /b 0
