@echo off
echo ========================================
echo 自动启动稳定模拟器
echo ========================================
echo.

REM 设置模拟器稳定配置
set EMULATOR_NAME=Pixel_6a_API_33
set EMULATOR_PORT=5554

REM 查找模拟器路径
if exist "%ANDROID_HOME%\emulator\emulator.exe" (
    set EMULATOR_PATH=%ANDROID_HOME%\emulator\emulator.exe
) else if exist "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" (
    set EMULATOR_PATH=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe
) else (
    echo 错误: 未找到Android模拟器
    echo 请确保Android SDK已安装并设置ANDROID_HOME环境变量
    pause
    exit /b 1
)

echo 使用模拟器: %EMULATOR_PATH%
echo 目标AVD: %EMULATOR_NAME%
echo.

REM 检查是否已有模拟器在运行
echo 检查现有模拟器...
adb devices | findstr "emulator" >nul
if %errorlevel% equ 0 (
    echo 发现运行中的模拟器
    adb devices
    echo.
    echo 是否要重启模拟器？(Y/N)
    set /p restart_choice=
    if /i "%restart_choice%"=="Y" (
        echo 停止现有模拟器...
        taskkill /f /im emulator.exe 2>nul
        taskkill /f /im qemu-system-x86_64.exe 2>nul
        timeout /t 3 /nobreak >nul
    ) else (
        echo 使用现有模拟器
        goto check_app
    )
)

REM 启动模拟器（稳定配置）
echo 启动模拟器（稳定模式）...
echo 配置参数：
echo - 内存: 4GB
echo - CPU核心: 4
echo - GPU: 软件渲染
echo - 禁用音频和动画（提高稳定性）
echo - 禁用快照（避免状态冲突）
echo.

"%EMULATOR_PATH%" -avd %EMULATOR_NAME% ^
    -no-snapshot-load ^
    -no-snapshot-save ^
    -gpu swiftshader_indirect ^
    -memory 4096 ^
    -cores 4 ^
    -no-audio ^
    -no-boot-anim ^
    -no-window ^
    -no-snapshot ^
    -wipe-data ^
    -port %EMULATOR_PORT%

echo 模拟器启动命令已执行
echo 请等待模拟器完全启动...
echo.

REM 等待模拟器启动
echo 等待模拟器启动（最多等待60秒）...
set /a counter=0
:wait_loop
timeout /t 5 /nobreak >nul
set /a counter+=5
adb devices | findstr "emulator" >nul
if %errorlevel% equ 0 (
    echo 模拟器已启动！
    goto check_app
)
if %counter% geq 60 (
    echo 超时: 模拟器启动时间过长
    echo 请检查模拟器配置或手动启动
    pause
    exit /b 1
)
echo 等待中... (%counter%/60秒)
goto wait_loop

:check_app
echo 检查设备连接...
adb devices
echo.

echo 检查应用程序状态...
adb shell ps | findstr com.example.myapplication
if %errorlevel% equ 0 (
    echo 应用程序正在运行
) else (
    echo 应用程序未运行，正在启动...
    goto start_app
)

echo 模拟器运行正常！
goto end

:start_app
echo 启动应用程序...
echo.

echo 1. 重新安装应用程序...
gradle-8.9\bin\gradle.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败，请检查错误信息
    pause
    exit /b 1
)

echo 2. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity

echo 3. 验证启动...
timeout /t 3 /nobreak >nul
adb shell ps | findstr com.example.myapplication
if %errorlevel% equ 0 (
    echo 应用程序启动成功！
) else (
    echo 应用程序启动可能有问题
)

:end
echo.
echo ========================================
echo 模拟器管理完成
echo ========================================
echo.
echo 常用命令：
echo - 查看设备: adb devices
echo - 查看应用: adb shell ps ^| findstr com.example.myapplication
echo - 重启应用: adb shell am start -n com.example.myapplication/.LoginActivity
echo - 停止应用: adb shell am force-stop com.example.myapplication
echo.
echo 如需更多功能，请运行 stable_emulator_manager.bat
echo.
pause
