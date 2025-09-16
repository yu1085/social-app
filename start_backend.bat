@echo off
echo 启动SocialMeet后端服务...
echo.

cd SocialMeet

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装Java 17
    pause
    exit /b 1
)

echo.
echo 启动Spring Boot应用...
echo 服务地址: http://localhost:8080
echo API文档: http://localhost:8080/swagger-ui.html
echo 健康检查: http://localhost:8080/api/health
echo.

gradlew bootRun

pause
