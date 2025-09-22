@echo off
echo ========================================
echo 用户充值脚本
echo 给用户ID 65899032 和 44479883 各充值10000
echo ========================================
echo.

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装Java
    pause
    exit /b 1
)

echo.
echo 检查MySQL数据库连接...
echo 数据库: localhost:3306/socialmeet
echo 用户名: root
echo.

echo 编译Java脚本...
javac -cp "SocialMeet/lib/*" RechargeScript.java
if %errorlevel% neq 0 (
    echo 错误: 编译失败
    pause
    exit /b 1
)

echo.
echo 执行充值操作...
java -cp ".;SocialMeet/lib/*" RechargeScript

echo.
echo 操作完成！
pause
