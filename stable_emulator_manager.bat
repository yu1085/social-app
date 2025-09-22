@echo off
echo ========================================
echo 稳定模拟器管理器
echo ========================================
echo.

:menu
echo 请选择操作：
echo 1. 启动模拟器（稳定模式）
echo 2. 检查模拟器状态
echo 3. 重启模拟器
echo 4. 修复模拟器连接
echo 5. 自动启动应用程序
echo 6. 监控模拟器健康状态
echo 7. 退出
echo.

set /p choice=请输入选择 (1-7): 

if "%choice%"=="1" goto start_stable
if "%choice%"=="2" goto check_status
if "%choice%"=="3" goto restart_emulator
if "%choice%"=="4" goto fix_connection
if "%choice%"=="5" goto auto_start_app
if "%choice%"=="6" goto monitor_health
if "%choice%"=="7" goto exit
echo 无效选择，请重新输入
goto menu

:start_stable
echo 正在启动模拟器（稳定模式）...
echo.

REM 检查Android SDK路径
if exist "%ANDROID_HOME%\emulator\emulator.exe" (
    set EMULATOR_PATH=%ANDROID_HOME%\emulator\emulator.exe
) else if exist "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" (
    set EMULATOR_PATH=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe
) else (
    echo 错误: 未找到Android模拟器
    echo 请确保Android SDK已安装
    pause
    goto menu
)

echo 使用模拟器路径: %EMULATOR_PATH%
echo.

REM 检查是否已有模拟器在运行
adb devices | findstr "emulator" >nul
if %errorlevel% equ 0 (
    echo 模拟器已在运行
    goto check_status
)

echo 启动模拟器（稳定配置）...
echo 配置说明：
echo - 禁用快照加载/保存（避免状态冲突）
echo - 启用硬件加速
echo - 设置稳定的内存配置
echo.

"%EMULATOR_PATH%" -avd Pixel_6a_API_33 ^
    -no-snapshot-load ^
    -no-snapshot-save ^
    -gpu swiftshader_indirect ^
    -memory 4096 ^
    -cores 4 ^
    -no-audio ^
    -no-boot-anim ^
    -no-window ^
    -no-snapshot ^
    -wipe-data

echo 模拟器启动命令已执行
echo 请等待模拟器完全启动...
goto check_status

:check_status
echo 检查模拟器状态...
echo.

echo 1. 检查ADB连接...
adb devices
echo.

echo 2. 检查模拟器进程...
tasklist | findstr emulator
echo.

echo 3. 检查应用程序状态...
adb shell ps | findstr com.example.myapplication
echo.

echo 4. 检查模拟器响应...
adb shell getprop ro.build.version.release
echo.

goto menu

:restart_emulator
echo 重启模拟器...
echo.

echo 1. 停止所有模拟器进程...
taskkill /f /im emulator.exe 2>nul
taskkill /f /im qemu-system-x86_64.exe 2>nul
timeout /t 3 /nobreak >nul

echo 2. 重启ADB服务...
adb kill-server
timeout /t 2 /nobreak >nul
adb start-server

echo 3. 重新启动模拟器...
goto start_stable

:fix_connection
echo 修复模拟器连接...
echo.

echo 1. 重启ADB服务...
adb kill-server
timeout /t 2 /nobreak >nul
adb start-server

echo 2. 重新连接模拟器...
adb connect emulator-5554
timeout /t 3 /nobreak >nul

echo 3. 验证连接...
adb devices
echo.

echo 4. 如果连接成功，重新安装应用程序...
adb devices | findstr "device" >nul
if %errorlevel% equ 0 (
    echo 设备已连接，重新安装应用程序...
    gradle-8.9\bin\gradle.bat installDebug
) else (
    echo 设备未连接，请检查模拟器状态
)

goto menu

:auto_start_app
echo 自动启动应用程序...
echo.

echo 1. 检查设备连接...
adb devices | findstr "device" >nul
if %errorlevel% neq 0 (
    echo 错误: 没有连接的设备
    echo 请先启动模拟器
    goto menu
)

echo 2. 停止现有应用程序...
adb shell am force-stop com.example.myapplication

echo 3. 重新安装应用程序...
gradle-8.9\bin\gradle.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败，请检查错误信息
    goto menu
)

echo 4. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity

echo 5. 验证应用程序状态...
timeout /t 3 /nobreak >nul
adb shell ps | findstr com.example.myapplication

echo 应用程序启动完成！
goto menu

:monitor_health
echo 监控模拟器健康状态...
echo.

:monitor_loop
echo 时间: %date% %time%
echo.

echo 1. 设备连接状态:
adb devices
echo.

echo 2. 模拟器进程状态:
tasklist | findstr emulator
echo.

echo 3. 应用程序状态:
adb shell ps | findstr com.example.myapplication
echo.

echo 4. 模拟器响应测试:
adb shell getprop ro.build.version.release
echo.

echo 按Ctrl+C停止监控，或等待10秒后继续...
timeout /t 10 /nobreak >nul
echo.
goto monitor_loop

:exit
echo 感谢使用稳定模拟器管理器！
exit /b 0
