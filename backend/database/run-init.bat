@echo off
chcp 65001
echo ========================================
echo  SocialMeet 数据库初始化脚本
echo ========================================
echo.
echo 正在连接MySQL并执行初始化...
echo 用户名: root
echo 密码: root
echo.

mysql -u root -proot < init.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo  数据库初始化成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo  数据库初始化失败！
    echo  请检查MySQL服务是否已启动
    echo  以及用户名密码是否正确
    echo ========================================
)

echo.
pause
