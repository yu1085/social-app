@echo off
chcp 65001 >nul
echo ====================================
echo  SocialMeet 后端服务 - 自动设置和启动
echo ====================================
echo.

cd /d "%~dp0"

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java！
    echo 请安装 Java 21
    pause
    exit /b 1
)

echo Java 环境检测成功
echo.

REM 检查 Maven Wrapper
if not exist "mvnw.cmd" (
    echo Maven Wrapper 不存在，正在下载...
    echo.

    REM 创建 .mvn/wrapper 目录
    if not exist ".mvn\wrapper" mkdir ".mvn\wrapper"

    REM 下载 Maven Wrapper 文件
    echo 下载 mvnw.cmd...
    curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd

    echo 下载 mvnw...
    curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw

    echo 下载 maven-wrapper.jar...
    curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

    echo 下载 maven-wrapper.properties...
    curl -o .mvn/wrapper/maven-wrapper.properties https://raw.githubusercontent.com/takari/maven-wrapper/master/.mvn/wrapper/maven-wrapper.properties

    echo.
    echo Maven Wrapper 下载完成
    echo.
)

echo ====================================
echo  启动 SocialMeet 后端服务
echo ====================================
echo.
echo 服务地址: http://localhost:8080/api
echo 按 Ctrl+C 停止服务
echo.

REM 启动应用
call mvnw.cmd spring-boot:run

pause
