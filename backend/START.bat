@echo off
chcp 65001
echo ========================================
echo  SocialMeet 后端服务启动脚本
echo ========================================
echo.
echo 正在检查环境...
echo.

REM 检查 Java 是否安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java！
    echo 请先安装 Java 21 或更高版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Java 环境检测成功！
echo.

REM 检查 Maven 是否安装
call mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未检测到 Maven！
    echo 正在使用 Maven Wrapper...
    echo.

    REM 下载 Maven Wrapper（如果不存在）
    if not exist mvnw.cmd (
        echo 下载 Maven Wrapper...
        curl -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd
        curl -o mvnw https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw
        curl -o .mvn/wrapper/maven-wrapper.jar https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
        curl -o .mvn/wrapper/maven-wrapper.properties https://raw.githubusercontent.com/takari/maven-wrapper/master/.mvn/wrapper/maven-wrapper.properties
    )
)

echo ========================================
echo  开始启动 SocialMeet 后端服务
echo ========================================
echo.
echo 提示：首次启动会下载依赖，需要等待几分钟
echo 服务地址: http://localhost:8080/api
echo.
echo 按 Ctrl+C 可以停止服务
echo ========================================
echo.

REM 启动 Spring Boot 应用
call mvn spring-boot:run

pause
