@echo off
echo ========================================
echo 启动Android模拟器
echo ========================================
echo.

echo 1. 检查Android SDK路径...
if exist "%ANDROID_HOME%\emulator\emulator.exe" (
    set EMULATOR_PATH=%ANDROID_HOME%\emulator\emulator.exe
    echo 找到模拟器: %EMULATOR_PATH%
) else if exist "%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe" (
    set EMULATOR_PATH=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe
    echo 找到模拟器: %EMULATOR_PATH%
) else (
    echo 错误: 未找到Android模拟器
    echo 请确保Android SDK已安装
    pause
    exit /b 1
)

echo.
echo 2. 列出可用的AVD...
"%EMULATOR_PATH%" -list-avds
echo.

echo 3. 启动Pixel 6a模拟器...
echo 正在启动模拟器，请等待...
"%EMULATOR_PATH%" -avd Pixel_6a_API_33 -no-snapshot-load -no-snapshot-save
echo.

echo 4. 等待模拟器完全启动...
timeout /t 30 /nobreak >nul

echo 5. 检查设备连接...
adb devices
echo.

echo 6. 如果设备已连接，启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 模拟器启动完成！
pause
