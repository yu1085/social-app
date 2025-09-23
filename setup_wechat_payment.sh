#!/bin/bash

# 微信支付配置脚本
# 使用方法: ./setup_wechat_payment.sh

echo "🚀 开始配置微信支付..."

# 检查必要的工具
check_tools() {
    echo "📋 检查必要工具..."
    
    if ! command -v java &> /dev/null; then
        echo "❌ Java 未安装，请先安装 Java 8 或更高版本"
        exit 1
    fi
    
    if ! command -v gradle &> /dev/null; then
        echo "❌ Gradle 未安装，请先安装 Gradle"
        exit 1
    fi
    
    echo "✅ 工具检查完成"
}

# 创建环境变量文件
create_env_file() {
    echo "📝 创建环境变量文件..."
    
    cat > .env << EOF
# 微信支付配置
WECHAT_APP_ID=your_wechat_app_id
WECHAT_MCH_ID=your_wechat_mch_id
WECHAT_API_V3_KEY=your_wechat_api_v3_key
WECHAT_CERTIFICATE_SERIAL_NUMBER=your_certificate_serial_number
WECHAT_PRIVATE_KEY_PATH=classpath:wechat_private_key.pem

# 服务器配置
SERVER_BASE_URL=http://localhost:8080
EOF
    
    echo "✅ 环境变量文件已创建: .env"
    echo "⚠️  请编辑 .env 文件，填入您的真实配置信息"
}

# 创建证书目录
create_cert_directories() {
    echo "📁 创建证书目录..."
    
    mkdir -p SocialMeet/src/main/resources/certs
    mkdir -p SocialMeet/src/main/resources/keys
    
    echo "✅ 证书目录已创建"
}

# 更新支付配置文件
update_payment_config() {
    echo "⚙️  更新支付配置文件..."
    
    # 备份原文件
    cp SocialMeet/src/main/resources/application-payment.yml SocialMeet/src/main/resources/application-payment.yml.bak
    
    # 更新微信支付配置
    cat > SocialMeet/src/main/resources/application-payment.yml << 'EOF'
# 支付配置文件
payment:
  # 支付宝配置
  alipay:
    app-id: ${ALIPAY_APP_ID:2021005195696348}
    private-key: ${ALIPAY_PRIVATE_KEY:your_alipay_private_key}
    public-key: ${ALIPAY_PUBLIC_KEY:your_alipay_public_key}
    alipay-public-key: ${ALIPAY_ALIPAY_PUBLIC_KEY:your_alipay_alipay_public_key}
    server-url: https://openapi.alipay.com/gateway.do
    format: json
    charset: UTF-8
    sign-type: RSA2
    notify-url: ${SERVER_BASE_URL:http://localhost:8080}/api/recharge/callback/alipay
    return-url: ${SERVER_BASE_URL:http://localhost:8080}/api/recharge/return/alipay
  
  # 微信支付配置
  wechat:
    app-id: ${WECHAT_APP_ID:your_wechat_app_id}
    mch-id: ${WECHAT_MCH_ID:your_wechat_mch_id}
    api-v3-key: ${WECHAT_API_V3_KEY:your_wechat_api_v3_key}
    private-key-path: ${WECHAT_PRIVATE_KEY_PATH:classpath:wechat_private_key.pem}
    certificate-serial-number: ${WECHAT_CERTIFICATE_SERIAL_NUMBER:your_certificate_serial_number}
    server-url: https://api.mch.weixin.qq.com
    notify-url: ${SERVER_BASE_URL:https://socialchatai.cloud}/api/recharge/callback/wechat
    return-url: ${SERVER_BASE_URL:https://socialchatai.cloud}/api/recharge/return/wechat

---
# 开发环境配置
spring:
  config:
    activate:
      on-profile: dev
      
payment:
  alipay:
    server-url: https://openapi.alipaydev.com/gateway.do  # 沙箱环境
    notify-url: http://localhost:8080/api/recharge/callback/alipay
    return-url: http://localhost:8080/api/recharge/return/alipay
  wechat:
    notify-url: http://localhost:8080/api/recharge/callback/wechat
    return-url: http://localhost:8080/api/recharge/return/wechat

---
# 生产环境配置
spring:
  config:
    activate:
      on-profile: prod
      
payment:
  alipay:
    server-url: https://openapi.alipay.com/gateway.do  # 正式环境
  wechat:
    server-url: https://api.mch.weixin.qq.com  # 正式环境
EOF
    
    echo "✅ 支付配置文件已更新"
}

# 创建微信支付回调Activity
create_wechat_callback_activity() {
    echo "📱 创建微信支付回调Activity..."
    
    mkdir -p app/src/main/java/com/example/myapplication/payment
    
    cat > app/src/main/java/com/example/myapplication/payment/WechatPayCallbackActivity.kt << 'EOF'
package com.example.myapplication.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class WechatPayCallbackActivity : Activity(), IWXAPIEventHandler {
    
    companion object {
        private const val TAG = "WechatPayCallback"
        private const val WECHAT_APP_ID = "your_wechat_app_id"
    }
    
    private lateinit var api: com.tencent.mm.opensdk.openapi.IWXAPI
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID, false)
        api.handleIntent(intent, this)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }
    
    override fun onReq(req: BaseReq) {
        Log.d(TAG, "onReq: ${req.type}")
    }
    
    override fun onResp(resp: BaseResp) {
        Log.d(TAG, "onResp: ${resp.type}")
        
        when (resp.type) {
            ConstantsAPI.COMMAND_PAY_BY_WX -> {
                when (resp.errCode) {
                    0 -> {
                        // 支付成功
                        Log.d(TAG, "微信支付成功")
                        // TODO: 处理支付成功逻辑
                        finish()
                    }
                    -1 -> {
                        // 支付失败
                        Log.e(TAG, "微信支付失败: ${resp.errStr}")
                        // TODO: 处理支付失败逻辑
                        finish()
                    }
                    -2 -> {
                        // 用户取消
                        Log.d(TAG, "用户取消微信支付")
                        // TODO: 处理用户取消逻辑
                        finish()
                    }
                }
            }
        }
    }
}
EOF
    
    echo "✅ 微信支付回调Activity已创建"
}

# 更新AndroidManifest.xml
update_android_manifest() {
    echo "📱 更新AndroidManifest.xml..."
    
    # 检查是否已经添加了微信支付回调Activity
    if ! grep -q "WechatPayCallbackActivity" app/src/main/AndroidManifest.xml; then
        echo "添加微信支付回调Activity到AndroidManifest.xml..."
        
        # 在</application>标签前添加微信支付回调Activity
        sed -i '/<\/application>/i\
        <activity\
            android:name=".payment.WechatPayCallbackActivity"\
            android:exported="true"\
            android:launchMode="singleTop">\
            <intent-filter>\
                <action android:name="android.intent.action.VIEW" />\
                <category android:name="android.intent.category.DEFAULT" />\
                <data android:scheme="your_wechat_app_id" />\
            </intent-filter>\
        </activity>' app/src/main/AndroidManifest.xml
        
        echo "✅ AndroidManifest.xml已更新"
    else
        echo "✅ AndroidManifest.xml已包含微信支付回调Activity"
    fi
}

# 创建测试脚本
create_test_script() {
    echo "🧪 创建测试脚本..."
    
    cat > test_wechat_payment.sh << 'EOF'
#!/bin/bash

echo "🧪 测试微信支付配置..."

# 启动后端服务
echo "🚀 启动后端服务..."
cd SocialMeet
./gradlew bootRun --args="--spring.profiles.active=wechat-real" &
BACKEND_PID=$!

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 测试健康检查
echo "🔍 测试健康检查..."
curl -X GET http://localhost:8080/api/auth/health

# 测试创建订单
echo "📝 测试创建订单..."
curl -X POST http://localhost:8080/api/recharge/create-order \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test_token" \
  -d '{
    "packageId": "package_1",
    "amount": 6.00,
    "coins": 60,
    "paymentMethod": "WECHAT"
  }'

# 停止后端服务
echo "🛑 停止后端服务..."
kill $BACKEND_PID

echo "✅ 测试完成"
EOF
    
    chmod +x test_wechat_payment.sh
    echo "✅ 测试脚本已创建: test_wechat_payment.sh"
}

# 主函数
main() {
    echo "🎯 微信支付配置脚本"
    echo "===================="
    
    check_tools
    create_env_file
    create_cert_directories
    update_payment_config
    create_wechat_callback_activity
    update_android_manifest
    create_test_script
    
    echo ""
    echo "🎉 微信支付配置完成！"
    echo ""
    echo "📋 下一步操作："
    echo "1. 编辑 .env 文件，填入您的微信支付配置信息"
    echo "2. 将微信支付证书文件放置到 SocialMeet/src/main/resources/ 目录"
    echo "3. 更新 WechatPayCallbackActivity.kt 中的 WECHAT_APP_ID"
    echo "4. 更新 AndroidManifest.xml 中的微信支付回调scheme"
    echo "5. 运行测试脚本: ./test_wechat_payment.sh"
    echo ""
    echo "📚 详细配置指南请查看: 微信支付配置指南.md"
}

# 运行主函数
main
