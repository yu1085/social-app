# 支付宝支付功能测试脚本

Write-Host "开始测试支付宝支付功能..." -ForegroundColor Green

# 测试用户登录
Write-Host "1. 测试用户登录..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{
    "username": "testuser",
    "password": "password123"
}'

if ($loginResponse.success) {
    Write-Host "✓ 用户登录成功" -ForegroundColor Green
    $token = $loginResponse.data.token
} else {
    Write-Host "✗ 用户登录失败: $($loginResponse.message)" -ForegroundColor Red
    exit 1
}

# 测试创建支付宝订单
Write-Host "`n2. 测试创建支付宝订单..." -ForegroundColor Yellow
$createOrderBody = @{
    packageId = "package_1200"
    coins = 1200
    amount = 12.00
    paymentMethod = "alipay"
    description = "充值1200金币"
} | ConvertTo-Json

$createOrderResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/payment/alipay/create" -Method POST -ContentType "application/json" -Headers @{"Authorization" = "Bearer $token"} -Body $createOrderBody

if ($createOrderResponse.success) {
    Write-Host "✓ 支付宝订单创建成功" -ForegroundColor Green
    Write-Host "  订单ID: $($createOrderResponse.data.orderId)" -ForegroundColor Cyan
    Write-Host "  支付宝订单号: $($createOrderResponse.data.alipayOutTradeNo)" -ForegroundColor Cyan
    $orderId = $createOrderResponse.data.orderId
} else {
    Write-Host "✗ 支付宝订单创建失败: $($createOrderResponse.message)" -ForegroundColor Red
    exit 1
}

# 测试查询订单列表
Write-Host "`n3. 测试查询订单列表..." -ForegroundColor Yellow
$orderListResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/payment/orders" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($orderListResponse.success) {
    Write-Host "✓ 订单列表查询成功" -ForegroundColor Green
    Write-Host "  订单数量: $($orderListResponse.data.Count)" -ForegroundColor Cyan
} else {
    Write-Host "✗ 订单列表查询失败: $($orderListResponse.message)" -ForegroundColor Red
}

# 测试查询订单详情
Write-Host "`n4. 测试查询订单详情..." -ForegroundColor Yellow
$orderDetailResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/payment/orders/$orderId" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($orderDetailResponse.success) {
    Write-Host "✓ 订单详情查询成功" -ForegroundColor Green
    Write-Host "  订单状态: $($orderDetailResponse.data.status)" -ForegroundColor Cyan
    Write-Host "  订单金额: $($orderDetailResponse.data.amount)" -ForegroundColor Cyan
} else {
    Write-Host "✗ 订单详情查询失败: $($orderDetailResponse.message)" -ForegroundColor Red
}

# 测试查询支付统计
Write-Host "`n5. 测试查询支付统计..." -ForegroundColor Yellow
$statisticsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/payment/statistics" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($statisticsResponse.success) {
    Write-Host "✓ 支付统计查询成功" -ForegroundColor Green
    Write-Host "  总订单数: $($statisticsResponse.data.totalOrders)" -ForegroundColor Cyan
    Write-Host "  成功订单数: $($statisticsResponse.data.successOrders)" -ForegroundColor Cyan
    Write-Host "  总充值金额: $($statisticsResponse.data.totalAmount)" -ForegroundColor Cyan
    Write-Host "  总充值金币: $($statisticsResponse.data.totalCoins)" -ForegroundColor Cyan
} else {
    Write-Host "✗ 支付统计查询失败: $($statisticsResponse.message)" -ForegroundColor Red
}

# 测试查询钱包余额
Write-Host "`n6. 测试查询钱包余额..." -ForegroundColor Yellow
$walletResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/profile/wallet" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($walletResponse.success) {
    Write-Host "✓ 钱包余额查询成功" -ForegroundColor Green
    Write-Host "  当前余额: $($walletResponse.data.balance)" -ForegroundColor Cyan
    Write-Host "  总充值: $($walletResponse.data.totalRecharge)" -ForegroundColor Cyan
    Write-Host "  总消费: $($walletResponse.data.totalConsume)" -ForegroundColor Cyan
} else {
    Write-Host "✗ 钱包余额查询失败: $($walletResponse.message)" -ForegroundColor Red
}

Write-Host "`n支付宝支付功能测试完成！" -ForegroundColor Green
Write-Host "注意：实际支付需要配置真实的支付宝密钥和回调地址" -ForegroundColor Yellow
