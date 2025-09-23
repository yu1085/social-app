# 真实支付SDK集成指南

## 📋 概述

本指南详细说明如何将支付宝和微信支付的真实SDK集成到SocialMeet应用中。

## 🚀 已完成的工作

### ✅ 后端支付服务架构
- **支付配置类** (`PaymentConfig.java`) - 统一管理支付参数
- **支付宝服务** (`AlipayService.java`) - 支付宝SDK集成和签名验证
- **微信支付服务** (`WechatPayService.java`) - 微信支付API调用和回调处理
- **订单管理** (`RechargeService.java`) - 统一的订单创建和状态管理
- **API接口** (`RechargeController.java`) - RESTful支付接口

### ✅ 前端支付流程
- **充值界面** (`RechargeActivity.kt`) - 用户充值选择界面
- **支付管理** (`RechargeViewModel.kt`) - 与后端API对接
- **支付方式** - 支持支付宝、微信支付

## 🔧 支付宝SDK真实集成

### 1. 后端配置

#### 1.1 获取支付宝应用信息
```yaml
# application-payment.yml
payment:
  alipay:
    app-id: "2021001234567890"  # 支付宝分配的应用ID
    private-key: "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC..."  # 应用私钥
    alipay-public-key: "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."  # 支付宝公钥
    server-url: "https://openapi.alipay.com/gateway.do"  # 正式环境
    # server-url: "https://openapi.alipaydev.com/gateway.do"  # 沙箱环境
```

#### 1.2 完善签名验证
```java
// AlipayService.java
public boolean verifyCallback(Map<String, String> params) {
    try {
        // 使用支付宝SDK验证签名
        return AlipaySignature.rsaCheckV1(
            params, 
            paymentConfig.getAlipay().getAlipayPublicKey(),
            paymentConfig.getAlipay().getCharset(),
            paymentConfig.getAlipay().getSignType()
        );
    } catch (AlipayApiException e) {
        log.error("支付宝签名验证失败", e);
        return false;
    }
}
```

### 2. Android端集成

#### 2.1 添加依赖
```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.alipay.sdk:alipay-sdk-android:15.8.11")
}
```

#### 2.2 添加权限
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

#### 2.3 支付宝支付管理器
```kotlin
// AlipayManager.kt
class AlipayManager(private val context: Context) {
    
    suspend fun pay(activity: Activity, orderInfo: String): PaymentResult = withContext(Dispatchers.IO) {
        try {
            val payTask = PayTask(activity)
            val result = payTask.payV2(orderInfo, true)
            
            val payResult = PayResult(result)
            
            when (payResult.resultStatus) {
                "9000" -> PaymentResult(true, "支付成功", null)
                "8000" -> PaymentResult(false, "正在处理中", null)
                "4000" -> PaymentResult(false, "订单支付失败", null)
                "5000" -> PaymentResult(false, "重复请求", null)
                "6001" -> PaymentResult(false, "用户中途取消", null)
                "6002" -> PaymentResult(false, "网络连接出错", null)
                else -> PaymentResult(false, "支付失败: ${payResult.memo}", null)
            }
        } catch (e: Exception) {
            PaymentResult(false, "支付异常: ${e.message}", null)
        }
    }
}
```

## 🔧 微信支付SDK真实集成

### 1. 后端配置

#### 1.1 获取微信支付信息
```yaml
# application-payment.yml
payment:
  wechat:
    app-id: "wx1234567890abcdef"  # 微信应用ID
    mch-id: "1234567890"  # 商户号
    api-v3-key: "your_api_v3_key_32_characters"  # APIv3密钥
    certificate-serial-number: "1234567890ABCDEF"  # 证书序列号
    private-key-path: "classpath:wechat_private_key.pem"  # 商户私钥文件路径
```

#### 1.2 完善微信支付API调用
```java
// WechatPayService.java
public Map<String, String> createPaymentOrder(RechargeOrder order) {
    try {
        // 构建统一下单请求
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appid", config.getAppId());
        requestBody.put("mchid", config.getMchId());
        requestBody.put("description", "充值" + order.getCoins() + "金币");
        requestBody.put("out_trade_no", order.getOrderId());
        requestBody.put("notify_url", config.getNotifyUrl());
        
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", order.getAmount().multiply(new BigDecimal(100)).intValue());
        amount.put("currency", "CNY");
        requestBody.put("amount", amount);
        
        // 调用微信支付统一下单API
        String response = callWechatPayAPI("/v3/pay/transactions/app", requestBody);
        
        // 解析prepay_id
        JsonNode responseJson = objectMapper.readTree(response);
        String prepayId = responseJson.get("prepay_id").asText();
        
        // 构建客户端支付参数
        return buildClientPayParams(prepayId);
        
    } catch (Exception e) {
        throw new RuntimeException("创建微信支付订单失败: " + e.getMessage());
    }
}
```

### 2. Android端集成

#### 2.1 添加依赖
```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.tencent.mm.opensdk:wechat-sdk-android:6.8.0")
}
```

#### 2.2 注册微信支付
```xml
<!-- AndroidManifest.xml -->
<activity
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTop" />
```

#### 2.3 微信支付管理器
```kotlin
// WechatPayManager.kt
class WechatPayManager(private val context: Context) {
    
    private val wxApi = WXAPIFactory.createWXAPI(context, "your_wechat_app_id")
    
    init {
        wxApi.registerApp("your_wechat_app_id")
    }
    
    suspend fun pay(activity: Activity, payInfo: Map<String, String>): PaymentResult {
        return try {
            val req = PayReq().apply {
                appId = payInfo["appId"]
                partnerId = payInfo["partnerId"]
                prepayId = payInfo["prepayId"]
                packageValue = payInfo["packageValue"]
                nonceStr = payInfo["nonceStr"]
                timeStamp = payInfo["timeStamp"]
                sign = payInfo["sign"]
            }
            
            val success = wxApi.sendReq(req)
            if (success) {
                PaymentResult(true, "微信支付调起成功", null)
            } else {
                PaymentResult(false, "微信支付调起失败", null)
            }
        } catch (e: Exception) {
            PaymentResult(false, "微信支付异常: ${e.message}", null)
        }
    }
}
```

## 🔐 签名和安全

### 1. 支付宝签名算法
```java
// 支付宝RSA2签名
public String sign(String content, String privateKey) {
    try {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(
            Base64.getDecoder().decode(privateKey)
        );
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        
        Signature signature = Signature.getInstance("SHA256WithRSA");
        signature.initSign(priKey);
        signature.update(content.getBytes("UTF-8"));
        
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);
    } catch (Exception e) {
        throw new RuntimeException("支付宝签名失败", e);
    }
}
```

### 2. 微信支付签名算法
```java
// 微信支付签名
public String buildAuthorization(String method, String url, String body) {
    long timestamp = System.currentTimeMillis() / 1000;
    String nonce = UUID.randomUUID().toString().replace("-", "");
    
    String message = method + "\n" + url + "\n" + timestamp + "\n" + nonce + "\n" + body + "\n";
    
    String signature = sign(message.getBytes("UTF-8"));
    
    return String.format("WECHATPAY2-SHA256-RSA2048 mchid=\"%s\",nonce_str=\"%s\",timestamp=\"%d\",serial_no=\"%s\",signature=\"%s\"",
        mchId, nonce, timestamp, serialNo, signature);
}
```

## 📱 完整支付流程

### 1. 用户发起支付
```kotlin
// RechargeViewModel.kt
private suspend fun processAlipayPayment(rechargePackage: RechargePackage) {
    try {
        // 1. 调用后端创建订单
        val orderResponse = createBackendOrder(rechargePackage, PaymentMethod.ALIPAY)
        val alipayOrderInfo = orderResponse["alipayOrderInfo"] as String
        
        // 2. 调用支付宝SDK
        val payResult = AlipayManager.pay(getApplication() as Activity, alipayOrderInfo)
        
        // 3. 处理支付结果
        if (payResult.success) {
            // 支付成功，等待后端回调确认
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                rechargeSuccess = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = payResult.message
            )
        }
    } catch (e: Exception) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "支付失败: ${e.message}"
        )
    }
}
```

### 2. 后端处理回调
```java
// RechargeController.java
@PostMapping("/callback/alipay")
public String alipayCallback(@RequestParam Map<String, String> params) {
    try {
        boolean success = rechargeService.handleAlipayCallback(params);
        return success ? "success" : "fail";
    } catch (Exception e) {
        log.error("支付宝回调处理失败", e);
        return "fail";
    }
}
```

### 3. 充值到用户钱包
```java
// RechargeService.java
private boolean processPaymentSuccess(RechargeOrder order, String transactionId, BigDecimal amount) {
    // 1. 更新订单状态
    order.setStatus("SUCCESS");
    order.setThirdPartyTransactionId(transactionId);
    order.setPaidAt(LocalDateTime.now());
    rechargeOrderRepository.save(order);
    
    // 2. 充值到用户钱包
    boolean rechargeSuccess = walletService.recharge(
        order.getUserId(),
        new BigDecimal(order.getCoins()),
        "充值" + order.getCoins() + "金币"
    );
    
    return rechargeSuccess;
}
```

## 🧪 测试指南

### 1. 沙箱测试
```yaml
# 使用沙箱环境进行测试
payment:
  alipay:
    server-url: "https://openapi.alipaydev.com/gateway.do"
    app-id: "沙箱应用ID"
    # 使用沙箱密钥
```

### 2. 测试用例
- ✅ 正常支付流程
- ✅ 支付取消
- ✅ 支付失败
- ✅ 网络异常
- ✅ 重复支付
- ✅ 订单过期

## 🚨 注意事项

### 安全要求
1. **私钥保护**: 私钥文件不能提交到代码仓库
2. **HTTPS**: 生产环境必须使用HTTPS
3. **签名验证**: 所有回调必须验证签名
4. **金额校验**: 严格校验支付金额
5. **重复处理**: 防止重复处理同一笔订单

### 合规要求
1. **用户协议**: 充值前显示用户协议
2. **发票功能**: 提供电子发票
3. **退款流程**: 实现退款功能
4. **实名认证**: 大额充值需要实名认证

## 📞 技术支持

- **支付宝开放平台**: https://open.alipay.com/
- **微信支付商户平台**: https://pay.weixin.qq.com/
- **技术文档**: 参考各平台官方文档

---

**状态**: ✅ 架构完成，等待真实SDK密钥配置  
**下一步**: 配置真实的支付平台账号和密钥
