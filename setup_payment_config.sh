#!/bin/bash

# SocialMeet 支付配置安装脚本
# 使用方法: ./setup_payment_config.sh

echo "🚀 SocialMeet 支付平台配置助手"
echo "================================="

# 检查是否为root用户
if [[ $EUID -eq 0 ]]; then
   echo "❌ 请不要使用root用户运行此脚本"
   exit 1
fi

# 创建配置目录
echo "📁 创建配置目录..."
mkdir -p ~/.socialmeet/config
mkdir -p ~/.socialmeet/certs

# 环境配置文件路径
ENV_FILE="$HOME/.socialmeet/config/payment.env"

echo "⚙️  开始配置支付信息..."
echo

# 支付宝配置
echo "💰 支付宝配置"
echo "-------------"
read -p "请输入支付宝 APP ID: " ALIPAY_APP_ID
read -p "请输入支付宝应用私钥 (或私钥文件路径): " ALIPAY_PRIVATE_KEY
read -p "请输入支付宝公钥 (或公钥文件路径): " ALIPAY_PUBLIC_KEY

# 检查是否为文件路径
if [[ -f "$ALIPAY_PRIVATE_KEY" ]]; then
    ALIPAY_PRIVATE_KEY=$(cat "$ALIPAY_PRIVATE_KEY" | tr -d '\n' | sed 's/-----BEGIN PRIVATE KEY-----//g' | sed 's/-----END PRIVATE KEY-----//g' | tr -d ' ')
fi

if [[ -f "$ALIPAY_PUBLIC_KEY" ]]; then
    ALIPAY_PUBLIC_KEY=$(cat "$ALIPAY_PUBLIC_KEY" | tr -d '\n' | sed 's/-----BEGIN PUBLIC KEY-----//g' | sed 's/-----END PUBLIC KEY-----//g' | tr -d ' ')
fi

echo
echo "💬 微信支付配置"
echo "---------------"
read -p "请输入微信 APP ID: " WECHAT_APP_ID
read -p "请输入微信商户号: " WECHAT_MCH_ID
read -p "请输入微信 API v3 密钥: " WECHAT_API_V3_KEY
read -p "请输入微信证书序列号: " WECHAT_CERT_SERIAL_NO
read -p "请输入微信私钥文件路径: " WECHAT_PRIVATE_KEY_PATH

# 复制微信私钥文件
if [[ -f "$WECHAT_PRIVATE_KEY_PATH" ]]; then
    cp "$WECHAT_PRIVATE_KEY_PATH" ~/.socialmeet/certs/wechat_private_key.pem
    chmod 600 ~/.socialmeet/certs/wechat_private_key.pem
    echo "✅ 微信私钥文件已复制到 ~/.socialmeet/certs/wechat_private_key.pem"
else
    echo "⚠️  微信私钥文件不存在: $WECHAT_PRIVATE_KEY_PATH"
fi

echo
echo "🌐 服务器配置"
echo "-------------"
read -p "请输入服务器域名 (如: https://yourdomain.com): " SERVER_BASE_URL

echo
echo "🗄️  数据库配置"
echo "-------------"
read -p "请输入数据库用户名: " DB_USERNAME
read -s -p "请输入数据库密码: " DB_PASSWORD
echo

# 生成环境变量文件
echo "📝 生成环境配置文件..."
cat > "$ENV_FILE" << EOF
# SocialMeet 支付配置环境变量
# 生成时间: $(date)

# 支付宝配置
export ALIPAY_APP_ID="$ALIPAY_APP_ID"
export ALIPAY_PRIVATE_KEY="$ALIPAY_PRIVATE_KEY"
export ALIPAY_PUBLIC_KEY="$ALIPAY_PUBLIC_KEY"

# 微信支付配置
export WECHAT_APP_ID="$WECHAT_APP_ID"
export WECHAT_MCH_ID="$WECHAT_MCH_ID"
export WECHAT_API_V3_KEY="$WECHAT_API_V3_KEY"
export WECHAT_CERT_SERIAL_NO="$WECHAT_CERT_SERIAL_NO"

# 服务器配置
export SERVER_BASE_URL="$SERVER_BASE_URL"

# 数据库配置
export DB_USERNAME="$DB_USERNAME"
export DB_PASSWORD="$DB_PASSWORD"
EOF

# 设置文件权限
chmod 600 "$ENV_FILE"

# 添加到.bashrc
if ! grep -q "source $ENV_FILE" ~/.bashrc; then
    echo "source $ENV_FILE" >> ~/.bashrc
    echo "✅ 环境变量已添加到 ~/.bashrc"
fi

echo
echo "🎉 配置完成！"
echo "============="
echo "配置文件位置: $ENV_FILE"
echo "证书文件目录: ~/.socialmeet/certs/"
echo
echo "📋 下一步操作:"
echo "1. 重新加载环境变量: source ~/.bashrc"
echo "2. 验证配置: echo \$ALIPAY_APP_ID"
echo "3. 启动后端服务: cd SocialMeet && ./gradlew bootRun"
echo "4. 在沙箱环境测试支付功能"
echo
echo "⚠️  安全提醒:"
echo "- 配置文件包含敏感信息，请妥善保管"
echo "- 生产环境请使用HTTPS"
echo "- 定期轮换API密钥"
echo
echo "📚 更多信息请查看: 支付平台账号配置指南.md"
