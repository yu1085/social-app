@echo off
echo Creating socialmeet database...

REM 尝试查找MySQL安装路径
set MYSQL_PATH=

if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
) else if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" (
    set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
) else if exist "C:\MySQL\bin\mysql.exe" (
    set MYSQL_PATH=C:\MySQL\bin\mysql.exe
)

if "%MYSQL_PATH%"=="" (
    echo Error: MySQL not found in common locations
    echo Please run this SQL command manually in MySQL:
    echo.
    type init_database.sql
    pause
    exit /b 1
)

echo Found MySQL at: %MYSQL_PATH%
"%MYSQL_PATH%" -uroot -proot < init_database.sql

if %errorlevel% == 0 (
    echo Database created successfully!
) else (
    echo Failed to create database. Please check MySQL credentials.
    echo Username: root
    echo Password: root
)

pause
