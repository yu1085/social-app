@echo off
chcp 65001 >nul
echo ====================================
echo  SocialMeet 数据库初始化
echo ====================================
echo.
echo 正在查找MySQL...

REM 尝试多个常见的MySQL安装路径
set MYSQL_PATH=
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
if exist "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo [错误] 未找到MySQL！
    echo.
    echo 请选择以下方式之一：
    echo.
    echo 方式1：手动执行SQL脚本
    echo   1. 打开MySQL Workbench或命令行
    echo   2. 连接到MySQL服务器 ^(root/root^)
    echo   3. 执行文件: %~dp0init.sql
    echo.
    echo 方式2：使用MySQL Workbench
    echo   1. 打开MySQL Workbench
    echo   2. 连接到本地数据库
    echo   3. File -^> Open SQL Script
    echo   4. 选择: %~dp0init.sql
    echo   5. 点击执行按钮
    echo.
    echo 方式3：添加MySQL到系统PATH
    echo   找到MySQL的bin目录并添加到系统环境变量
    echo.
    pause
    exit /b 1
)

echo 找到MySQL: %MYSQL_PATH%
echo.
echo 正在执行初始化脚本...
echo 数据库: socialmeet
echo 用户: root
echo.

"%MYSQL_PATH%" -u root -proot < "%~dp0init.sql" 2>&1

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
    echo 可能的原因：
    echo 1. MySQL服务未启动
    echo 2. 用户名或密码错误
    echo 3. MySQL版本不兼容
    echo.
    echo 请尝试手动执行SQL脚本
    echo.
)

pause
