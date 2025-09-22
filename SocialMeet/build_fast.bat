@echo off
chcp 65001 >nul
echo ================================
echo   SocialMeet 快速构建脚本
echo ================================
echo.

echo [INFO] 设置Gradle环境变量...
set GRADLE_OPTS=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
set GRADLE_USER_HOME=%TEMP%\gradle
mkdir "%TEMP%\gradle" 2>nul

echo [INFO] 清理之前的构建...
call gradlew clean

echo [INFO] 开始快速构建...
call gradlew build -x test --parallel --daemon --build-cache

if %errorlevel%==0 (
    echo.
    echo ================================
    echo   ✅ 构建成功！
    echo ================================
    echo.
    echo 构建产物位置：
    echo   - JAR文件: build\libs\*.jar
    echo   - 构建目录: build\
    echo.
    echo 现在可以部署到服务器了！
) else (
    echo.
    echo ================================
    echo   ❌ 构建失败！
    echo ================================
    echo.
    echo 请检查错误信息并重试
)

pause
