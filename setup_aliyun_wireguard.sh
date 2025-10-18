#!/bin/bash

# 阿里云ECS WireGuard VPN 安装脚本
# 适用于 Ubuntu 20.04/22.04

set -e

echo "🚀 开始为阿里云ECS安装 WireGuard VPN..."

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo "❌ 请使用 root 用户运行此脚本"
    echo "使用命令: sudo bash setup_aliyun_wireguard.sh"
    exit 1
fi

# 检查系统版本
if ! grep -q "Ubuntu" /etc/os-release; then
    echo "❌ 此脚本仅支持 Ubuntu 系统"
    exit 1
fi

echo "📦 更新系统包..."
apt update && apt upgrade -y

# 安装必要软件
echo "🔧 安装必要软件..."
apt install -y wireguard qrencode curl ufw

# 生成服务器密钥
echo "🔑 生成服务器密钥..."
cd /etc/wireguard
wg genkey | tee server_private_key | wg pubkey > server_public_key

# 生成客户端密钥
echo "👤 生成客户端密钥..."
wg genkey | tee client_private_key | wg pubkey > client_public_key

# 获取服务器公网IP
SERVER_IP=$(curl -s ifconfig.me)
echo "🌐 检测到服务器IP: $SERVER_IP"

# 获取网卡名称（阿里云ECS通常是eth0）
INTERFACE=$(ip route | grep default | awk '{print $5}' | head -n1)
echo "🔌 检测到网卡: $INTERFACE"

# 创建服务器配置
echo "⚙️ 创建服务器配置..."
cat > /etc/wireguard/wg0.conf << EOF
[Interface]
PrivateKey = $(cat server_private_key)
Address = 10.0.0.1/24
ListenPort = 51820
PostUp = iptables -A FORWARD -i %i -j ACCEPT; iptables -A FORWARD -o %i -j ACCEPT; iptables -t nat -A POSTROUTING -o $INTERFACE -j MASQUERADE
PostDown = iptables -D FORWARD -i %i -j ACCEPT; iptables -D FORWARD -o %i -j ACCEPT; iptables -t nat -D POSTROUTING -o $INTERFACE -j MASQUERADE

[Peer]
PublicKey = $(cat client_public_key)
AllowedIPs = 10.0.0.2/32
EOF

# 创建客户端配置
echo "📱 创建客户端配置..."
cat > /etc/wireguard/client.conf << EOF
[Interface]
PrivateKey = $(cat client_private_key)
Address = 10.0.0.2/24
DNS = 223.5.5.5, 8.8.8.8

[Peer]
PublicKey = $(cat server_public_key)
Endpoint = $SERVER_IP:51820
AllowedIPs = 0.0.0.0/0
PersistentKeepalive = 25
EOF

# 启用IP转发
echo "🌐 启用IP转发..."
echo 'net.ipv4.ip_forward = 1' >> /etc/sysctl.conf
sysctl -p

# 配置防火墙
echo "🔥 配置防火墙..."
ufw --force reset
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 51820/udp
ufw --force enable

# 启动WireGuard
echo "🚀 启动WireGuard服务..."
systemctl start wg-quick@wg0
systemctl enable wg-quick@wg0

# 等待服务启动
sleep 3

# 检查服务状态
if systemctl is-active --quiet wg-quick@wg0; then
    echo "✅ WireGuard 服务启动成功"
else
    echo "❌ WireGuard 服务启动失败"
    systemctl status wg-quick@wg0
    exit 1
fi

# 生成二维码
echo "📱 生成客户端二维码..."
qrencode -t ansiutf8 < /etc/wireguard/client.conf

echo ""
echo "🎉 阿里云ECS WireGuard VPN 安装完成！"
echo ""
echo "📋 服务器信息:"
echo "   公网IP: $SERVER_IP"
echo "   端口: 51820"
echo "   内网IP: 10.0.0.1"
echo "   网卡: $INTERFACE"
echo ""
echo "📱 客户端配置:"
echo "   内网IP: 10.0.0.2"
echo "   DNS: 223.5.5.5, 8.8.8.8"
echo "   配置文件: /etc/wireguard/client.conf"
echo ""
echo "🔧 管理命令:"
echo "   查看状态: wg show"
echo "   重启服务: systemctl restart wg-quick@wg0"
echo "   停止服务: systemctl stop wg-quick@wg0"
echo "   查看日志: journalctl -u wg-quick@wg0 -f"
echo ""
echo "🛡️ 安全提醒:"
echo "   1. 请确保在阿里云控制台开放51820端口"
echo "   2. 建议定期更换密钥"
echo "   3. 仅用于合法用途"
echo ""
echo "📱 客户端连接:"
echo "   1. 下载 WireGuard 客户端"
echo "   2. 扫描上面的二维码或导入配置文件"
echo "   3. 连接VPN"
echo ""
echo "🎯 测试连接:"
echo "   连接后访问: https://www.whatismyip.com"
echo "   应该显示服务器IP: $SERVER_IP"
echo ""
echo "🎉 享受您的私人VPN服务！"
