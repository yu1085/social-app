@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM SocialMeet Windows 快速部署脚本
REM 一键部署到云服务器

echo ================================
echo   SocialMeet 快速部署 (Windows)
echo ================================
echo.

REM 服务器信息
set SERVER_IP=119.45.174.10
set SERVER_USER=ubuntu
set SERVER_PASSWORD=Q4!zVBTL5^*p)tkb

REM 检查本地环境
echo [INFO] 检查本地环境...
where ssh >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] SSH客户端未安装，请安装OpenSSH或PuTTY
    pause
    exit /b 1
)

where scp >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] SCP客户端未安装，请安装OpenSSH或WinSCP
    pause
    exit /b 1
)

echo [INFO] 本地环境检查通过

REM 检查项目文件
if not exist "..\SocialMeet" (
    echo [ERROR] SocialMeet项目目录不存在
    pause
    exit /b 1
)

if not exist "docker-compose.yml" (
    echo [ERROR] 部署配置文件不存在
    pause
    exit /b 1
)

echo [INFO] 项目文件检查通过

REM 检查sshpass是否可用
where sshpass >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] sshpass未安装，将使用交互式SSH
    set USE_SSHPASS=0
) else (
    set USE_SSHPASS=1
)

REM 检查服务器连接
echo [INFO] 检查服务器连接...
if %USE_SSHPASS%==1 (
    sshpass -p "%SERVER_PASSWORD%" ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 %SERVER_USER%@%SERVER_IP% "echo '连接成功'" >nul 2>&1
) else (
    ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 %SERVER_USER%@%SERVER_IP% "echo '连接成功'" >nul 2>&1
)

if %errorlevel% neq 0 (
    echo [ERROR] 无法连接到服务器 %SERVER_IP%
    echo 请检查：
    echo 1. 服务器IP地址是否正确
    echo 2. 服务器是否已启动
    echo 3. 网络连接是否正常
    echo 4. SSH服务是否正常运行
    pause
    exit /b 1
)

echo [INFO] 服务器连接正常

REM 创建临时目录
echo [INFO] 准备上传文件...
set TEMP_DIR=%TEMP%\socialmeet_deploy_%RANDOM%
mkdir "%TEMP_DIR%"

REM 复制项目文件
xcopy "..\SocialMeet" "%TEMP_DIR%\SocialMeet\" /E /I /Q >nul
xcopy "." "%TEMP_DIR%\deploy\" /E /I /Q >nul

echo [INFO] 正在上传文件到服务器，请稍候...

REM 上传文件到服务器
if %USE_SSHPASS%==1 (
    sshpass -p "%SERVER_PASSWORD%" scp -r "%TEMP_DIR%\*" %SERVER_USER%@%SERVER_IP%:~/
) else (
    echo 请手动上传文件到服务器：
    echo 1. 使用WinSCP或其他SFTP工具
    echo 2. 将 %TEMP_DIR% 目录下的所有文件上传到服务器的 ~/ 目录
    echo 3. 上传完成后按任意键继续...
    pause
)

REM 清理临时目录
rmdir /s /q "%TEMP_DIR%"

echo [INFO] 文件上传完成

REM 生成远程执行脚本
echo [INFO] 生成远程执行脚本...
(
echo #!/bin/bash
echo set -e
echo.
echo echo "=== 开始部署 SocialMeet ==="
echo.
echo # 更新系统
echo echo "[INFO] 更新系统包..."
echo apt update ^&^& apt upgrade -y
echo.
echo # 安装必要工具
echo echo "[INFO] 安装必要工具..."
echo apt install -y curl wget git vim unzip htop tree net-tools ufw fail2ban
echo.
echo # 安装Docker
echo echo "[INFO] 安装Docker..."
echo if ! command -v docker ^&^> /dev/null; then
echo     curl -fsSL https://get.docker.com -o get-docker.sh
echo     sh get-docker.sh
echo     rm get-docker.sh
echo fi
echo.
echo # 安装Docker Compose
echo echo "[INFO] 安装Docker Compose..."
echo if ! command -v docker-compose ^&^> /dev/null; then
echo     COMPOSE_VERSION=^$(curl -s https://api.github.com/repos/docker/compose/releases/latest ^| grep 'tag_name' ^| cut -d\" -f4^)
echo     curl -L "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-^$(uname -s^)-^$(uname -m^)" -o /usr/local/bin/docker-compose
echo     chmod +x /usr/local/bin/docker-compose
echo fi
echo.
echo # 进入部署目录
echo cd ~/deploy
echo.
echo # 配置环境变量
echo echo "[INFO] 配置环境变量..."
echo if [ ! -f ".env" ]; then
echo     cp env.example .env
echo     # 生成JWT密钥
echo     JWT_SECRET=^$(openssl rand -base64 32^)
echo     sed -i "s/your-super-secret-jwt-key-change-this-in-production-.*/your-super-secret-jwt-key-change-this-in-production-${JWT_SECRET}/" .env
echo fi
echo.
echo # 生成SSL证书
echo echo "[INFO] 生成SSL证书..."
echo chmod +x deploy.sh
echo ./deploy.sh ssl
echo.
echo # 配置防火墙
echo echo "[INFO] 配置防火墙..."
echo ufw allow 22
echo ufw allow 80
echo ufw allow 443
echo ufw allow 8080
echo ufw --force enable
echo.
echo # 部署应用
echo echo "[INFO] 部署应用..."
echo ./deploy.sh deploy prod
echo.
echo # 等待服务启动
echo echo "[INFO] 等待服务启动..."
echo sleep 30
echo.
echo # 检查服务状态
echo echo "[INFO] 检查服务状态..."
echo ./deploy.sh status
echo.
echo # 测试API
echo echo "[INFO] 测试API接口..."
echo if curl -f http://localhost/api/health ^> /dev/null 2^>^&1; then
echo     echo "✅ API接口测试成功"
echo else
echo     echo "⚠️ API接口测试失败"
echo fi
echo.
echo if curl -f https://localhost/api/health ^> /dev/null 2^>^&1; then
echo     echo "✅ HTTPS API接口测试成功"
echo else
echo     echo "⚠️ HTTPS API接口测试失败"
echo fi
echo.
echo echo "🎉 部署完成！"
echo echo "API地址: https://%SERVER_IP%/api/"
echo echo "健康检查: https://%SERVER_IP%/api/health"
) > remote_deploy.sh

REM 上传并执行远程脚本
echo [INFO] 执行远程部署脚本...
if %USE_SSHPASS%==1 (
    sshpass -p "%SERVER_PASSWORD%" scp remote_deploy.sh %SERVER_USER%@%SERVER_IP%:~/
    sshpass -p "%SERVER_PASSWORD%" ssh %SERVER_USER%@%SERVER_IP% "chmod +x remote_deploy.sh && ./remote_deploy.sh"
) else (
    echo 请手动执行以下命令：
    echo 1. 将 remote_deploy.sh 上传到服务器
    echo 2. 在服务器上执行: chmod +x remote_deploy.sh ^&^& ./remote_deploy.sh
    echo 3. 执行完成后按任意键继续...
    pause
)

REM 清理临时文件
del remote_deploy.sh >nul 2>&1

echo.
echo ================================
echo   🎉 部署完成！
echo ================================
echo.
echo 服务访问地址：
echo   - API接口: https://%SERVER_IP%/api/
echo   - HTTPS API: https://%SERVER_IP%/api/
echo   - 健康检查: https://%SERVER_IP%/api/health
echo.
echo 管理命令：
echo   - 连接服务器: ssh %SERVER_USER%@%SERVER_IP%
echo   - 查看状态: cd ~/deploy ^&^& ./deploy.sh status
echo   - 查看日志: cd ~/deploy ^&^& ./deploy.sh logs
echo   - 重启服务: cd ~/deploy ^&^& ./deploy.sh restart
echo.
echo 重要提醒：
echo 1. 请立即修改服务器登录密码
echo 2. 配置SSH密钥认证
echo 3. 更新Android应用中的API地址
echo 4. 定期备份数据
echo.
echo Android应用配置：
echo 将API地址更新为: https://%SERVER_IP%/api
echo.
pause
