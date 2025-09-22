@echo off
chcp 65001 >nul
echo ================================
echo   安装 sshpass 工具
echo ================================
echo.

echo [INFO] 检查是否已安装 sshpass...
where sshpass >nul 2>&1
if %errorlevel%==0 (
    echo [INFO] sshpass 已安装
    pause
    exit /b 0
)

echo [INFO] sshpass 未安装，开始安装...
echo.

echo 请选择安装方式：
echo 1. 使用 Chocolatey 安装 (推荐)
echo 2. 使用 Scoop 安装
echo 3. 手动下载安装
echo 4. 跳过安装，使用交互式SSH
echo.

set /p choice=请输入选择 (1-4): 

if "%choice%"=="1" goto install_chocolatey
if "%choice%"=="2" goto install_scoop
if "%choice%"=="3" goto manual_install
if "%choice%"=="4" goto skip_install
goto invalid_choice

:install_chocolatey
echo [INFO] 使用 Chocolatey 安装 sshpass...
where choco >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Chocolatey 未安装，请先安装 Chocolatey
    echo 安装命令: Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    pause
    exit /b 1
)

choco install sshpass -y
if %errorlevel%==0 (
    echo [INFO] sshpass 安装成功
) else (
    echo [ERROR] sshpass 安装失败
)
goto end

:install_scoop
echo [INFO] 使用 Scoop 安装 sshpass...
where scoop >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Scoop 未安装，请先安装 Scoop
    echo 安装命令: Set-ExecutionPolicy RemoteSigned -Scope CurrentUser; irm get.scoop.sh | iex
    pause
    exit /b 1
)

scoop install sshpass
if %errorlevel%==0 (
    echo [INFO] sshpass 安装成功
) else (
    echo [ERROR] sshpass 安装失败
)
goto end

:manual_install
echo [INFO] 手动安装 sshpass...
echo.
echo 请按以下步骤手动安装 sshpass：
echo.
echo 1. 下载 sshpass 二进制文件：
echo    https://sourceforge.net/projects/sshpass/files/sshpass/1.10/sshpass-1.10.tar.gz/download
echo.
echo 2. 解压并编译：
echo    tar -xzf sshpass-1.10.tar.gz
echo    cd sshpass-1.10
echo    ./configure
echo    make
echo    make install
echo.
echo 3. 或者下载预编译的 Windows 版本：
echo    https://github.com/keimpx/sshpass-windows/releases
echo.
echo 4. 将 sshpass.exe 放到 PATH 环境变量中的目录
echo.
echo 安装完成后重新运行部署脚本
pause
goto end

:skip_install
echo [INFO] 跳过 sshpass 安装
echo [WARNING] 将使用交互式SSH，需要手动输入密码
goto end

:invalid_choice
echo [ERROR] 无效选择，请重新运行脚本
pause
exit /b 1

:end
echo.
echo 安装完成！
pause
