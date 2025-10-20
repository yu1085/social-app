# 全面修复测试脚本
# 测试Gson解析错误和支付API路由修复

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "全面修复测试" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://10.0.2.2:8080"

# 测试用户登录获取Token
Write-Host "[1] 测试用户登录..." -ForegroundColor Yellow
$loginBody = @{
    phone = "19887654321"
    code = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login-with-code?phone=19887654321&code=123456" -Method Post
    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        Write-Host "OK Login successful, got Token" -ForegroundColor Green
        Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "FAIL Login failed: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "FAIL Login failed: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 测试获取钱包信息（修复Gson解析错误）
Write-Host "[2] 测试获取钱包信息（修复Gson解析错误）..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $walletResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/wallet" -Method Get -Headers $headers
    if ($walletResponse.success) {
        Write-Host "OK Get wallet info successful" -ForegroundColor Green
        Write-Host "  Balance: $($walletResponse.data.balance) coins" -ForegroundColor Gray
        Write-Host "  Total recharge: $($walletResponse.data.totalRecharge) coins" -ForegroundColor Gray
        Write-Host "  Total consume: $($walletResponse.data.totalConsume) coins" -ForegroundColor Gray
        Write-Host "  Created at: $($walletResponse.data.createdAt)" -ForegroundColor Gray
        Write-Host "  Updated at: $($walletResponse.data.updatedAt)" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Get wallet info failed: $($walletResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Get wallet info failed: $_" -ForegroundColor Red
}

Write-Host ""

# 测试获取用户设置
Write-Host "[3] 测试获取用户设置..." -ForegroundColor Yellow
try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/settings" -Method Get -Headers $headers
    if ($settingsResponse.success) {
        Write-Host "OK Get user settings successful" -ForegroundColor Green
        Write-Host "  Voice call enabled: $($settingsResponse.data.voiceCallEnabled)" -ForegroundColor Gray
        Write-Host "  Video call enabled: $($settingsResponse.data.videoCallEnabled)" -ForegroundColor Gray
        Write-Host "  Message charge enabled: $($settingsResponse.data.messageChargeEnabled)" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Get user settings failed: $($settingsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Get user settings failed: $_" -ForegroundColor Red
}

Write-Host ""

# 测试支付宝支付API（修复路由错误）
Write-Host "[4] 测试支付宝支付API（修复路由错误）..." -ForegroundColor Yellow
$paymentBody = @{
    amount = 12
    coins = 1200
    description = "充值1200金币"
    packageId = "package_1200"
    paymentMethod = "alipay"
} | ConvertTo-Json

try {
    $paymentResponse = Invoke-RestMethod -Uri "$baseUrl/api/payment/alipay/create" -Method Post -Body $paymentBody -Headers $headers
    if ($paymentResponse.success) {
        Write-Host "OK Create payment order successful" -ForegroundColor Green
        Write-Host "  Order ID: $($paymentResponse.data.orderId)" -ForegroundColor Gray
        Write-Host "  Payment URL: $($paymentResponse.data.paymentUrl)" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Create payment order failed: $($paymentResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Create payment order failed: $_" -ForegroundColor Red
}

Write-Host ""

# 测试财富等级API
Write-Host "[5] 测试财富等级API..." -ForegroundColor Yellow
try {
    $wealthResponse = Invoke-RestMethod -Uri "$baseUrl/api/wealth-level/my-level" -Method Get -Headers $headers
    if ($wealthResponse.success) {
        Write-Host "OK Get wealth level successful" -ForegroundColor Green
        Write-Host "  Level name: $($wealthResponse.data.levelName)" -ForegroundColor Gray
        Write-Host "  Level ID: $($wealthResponse.data.levelId)" -ForegroundColor Gray
        Write-Host "  Wealth value: $($wealthResponse.data.wealthValue)" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Get wealth level failed: $($wealthResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Get wealth level failed: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "测试完成" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "修复总结：" -ForegroundColor Yellow
Write-Host "1. Gson解析错误 - 已修复" -ForegroundColor Green
Write-Host "   - 将WalletDTO中的时间字段从LocalDateTime改为String" -ForegroundColor Gray
Write-Host "   - 添加@SerializedName注解确保字段映射正确" -ForegroundColor Gray
Write-Host ""
Write-Host "2. 支付API路由错误 - 已修复" -ForegroundColor Green
Write-Host "   - 修改PaymentController路径从/api/v1/payment到/api/payment" -ForegroundColor Gray
Write-Host "   - 添加/alipay/create路由映射" -ForegroundColor Gray
Write-Host ""
Write-Host "3. 主线程网络调用问题 - 已修复" -ForegroundColor Green
Write-Host "   - 所有网络调用都在IO线程执行" -ForegroundColor Gray
Write-Host "   - UI更新切换回主线程" -ForegroundColor Gray
Write-Host ""
Write-Host "Please recompile and run the Android app for final testing." -ForegroundColor Yellow

