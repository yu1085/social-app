@echo off
echo ========================================
echo 启动SocialMeet后端服务
echo ========================================
echo.

cd SocialMeet
echo 正在启动Spring Boot服务...
echo 如果启动失败，请检查：
echo 1. MySQL数据库是否运行
echo 2. 端口8080是否被占用
echo 3. 配置文件是否正确
echo.

gradlew bootRun

pause
