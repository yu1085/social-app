@echo off
echo 正在将ADB添加到系统PATH中...
echo.

REM 获取当前用户的ADB路径
set ADB_PATH=%USERPROFILE%\AppData\Local\Android\Sdk\platform-tools

REM 检查ADB是否存在
if not exist "%ADB_PATH%\adb.exe" (
    echo 错误：在 %ADB_PATH% 中找不到adb.exe
    echo 请确保Android SDK已正确安装
    pause
    exit /b 1
)

REM 添加到用户PATH（不需要管理员权限）
for /f "tokens=2*" %%A in ('reg query "HKCU\Environment" /v PATH 2^>nul') do set USER_PATH=%%B
if not defined USER_PATH set USER_PATH=

REM 检查是否已经存在
echo %USER_PATH% | findstr /C:"%ADB_PATH%" >nul
if %errorlevel% equ 0 (
    echo ADB路径已经存在于用户PATH中
) else (
    REM 添加ADB路径到用户PATH
    if defined USER_PATH (
        reg add "HKCU\Environment" /v PATH /t REG_EXPAND_SZ /d "%USER_PATH%;%ADB_PATH%" /f
    ) else (
        reg add "HKCU\Environment" /v PATH /t REG_EXPAND_SZ /d "%ADB_PATH%" /f
    )
    echo ADB路径已成功添加到用户PATH中
)

echo.
echo 注意：您需要重新启动命令提示符或PowerShell才能生效
echo 或者运行以下命令刷新环境变量：
echo refreshenv
echo.
pause
