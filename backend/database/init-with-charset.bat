@echo off
chcp 65001 >nul
echo ====================================
echo  SocialMeet 数据库初始化
echo ====================================
echo.

set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe

if not exist "%MYSQL_PATH%" (
    echo [错误] MySQL 未找到！
    echo 请检查 MySQL 安装路径
    pause
    exit /b 1
)

echo 正在初始化数据库...
echo 数据库: socialmeet
echo 用户: root
echo.

REM 使用正确的字符集参数执行 SQL
"%MYSQL_PATH%" -u root -proot --default-character-set=utf8mb4 < "%~dp0init.sql" 2>&1

if %errorlevel% == 0 (
    echo.
    echo ====================================
    echo  数据库初始化成功！
    echo ====================================
    echo.
    echo 测试账号：
    echo - 19812342076 ^(验证码: 123456^)
    echo - 19887654321 ^(验证码: 123456^)
    echo - 13800138000 ^(验证码: 123456^)
    echo.
) else (
    echo.
    echo ====================================
    echo  数据库初始化失败！
    echo ====================================
    echo.
    echo 错误代码: %errorlevel%
    echo.
)

pause
