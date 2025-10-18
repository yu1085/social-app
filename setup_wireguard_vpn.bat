@echo off
echo ================================================================
echo WireGuard VPN 一键安装脚本 (Windows)
echo ================================================================

echo.
echo 此脚本将帮助您在Windows上安装和配置WireGuard客户端
echo.

echo 安装步骤:
echo 1. 下载 WireGuard 客户端
echo 2. 安装 WireGuard
echo 3. 导入配置文件
echo 4. 连接VPN
echo.

echo 下载地址:
echo https://www.wireguard.com/install/
echo.

echo 或者使用包管理器安装:
echo winget install WireGuard.WireGuard
echo.

echo 配置文件示例:
echo [Interface]
echo PrivateKey = 您的客户端私钥
echo Address = 10.0.0.2/24
echo DNS = 8.8.8.8
echo.
echo [Peer]
echo PublicKey = 服务器公钥
echo Endpoint = 您的服务器IP:51820
echo AllowedIPs = 0.0.0.0/0
echo PersistentKeepalive = 25
echo.

echo 注意事项:
echo - 确保服务器已正确配置WireGuard
echo - 防火墙需要开放51820端口
echo - 客户端需要管理员权限运行
echo.

echo ================================================================
echo 请按照上述步骤完成WireGuard客户端安装
echo ================================================================
pause
