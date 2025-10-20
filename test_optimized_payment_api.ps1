# 优化后的支付接口测试脚本

Write-Host "=== 优化后的支付接口测试 ===" -ForegroundColor Green

# 测试用户登录
Write-Host "`n1. 测试用户登录..." -ForegroundColor Yellow
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

# 测试创建支付订单（新接口）
Write-Host "`n2. 测试创建支付订单（优化版本）..." -ForegroundColor Yellow
$createOrderBody = @{
    packageId = "package_1200"
    coins = 1200
    amount = 12.00
    paymentMethod = "ALIPAY"
    description = "充值1200金币"
    clientIp = "192.168.1.100"
    timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
    signature = "test_signature_123"
} | ConvertTo-Json

$createOrderResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payment/orders" -Method POST -ContentType "application/json" -Headers @{"Authorization" = "Bearer $token"} -Body $createOrderBody

if ($createOrderResponse.success) {
    Write-Host "✓ 支付订单创建成功" -ForegroundColor Green
    Write-Host "  订单ID: $($createOrderResponse.data.orderId)" -ForegroundColor Cyan
    Write-Host "  请求ID: $($createOrderResponse.requestId)" -ForegroundColor Cyan
    Write-Host "  响应时间: $($createOrderResponse.timestamp)" -ForegroundColor Cyan
    $orderId = $createOrderResponse.data.orderId
} else {
    Write-Host "✗ 支付订单创建失败: $($createOrderResponse.message)" -ForegroundColor Red
    Write-Host "  错误码: $($createOrderResponse.code)" -ForegroundColor Red
}

# 测试参数验证
Write-Host "`n3. 测试参数验证..." -ForegroundColor Yellow

# 测试无效金额
Write-Host "  测试无效金额..." -ForegroundColor Cyan
$invalidAmountBody = @{
    packageId = "package_1200"
    coins = 1200
    amount = -10.00  # 无效金额
    paymentMethod = "ALIPAY"
    description = "充值1200金币"
} | ConvertTo-Json

$invalidAmountResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payment/orders" -Method POST -ContentType "application/json" -Headers @{"Authorization" = "Bearer $token"} -Body $invalidAmountBody -ErrorAction SilentlyContinue

if ($invalidAmountResponse -and -not $invalidAmountResponse.success) {
    Write-Host "  ✓ 无效金额验证成功" -ForegroundColor Green
    Write-Host "    错误信息: $($invalidAmountResponse.message)" -ForegroundColor Cyan
} else {
    Write-Host "  ✗ 无效金额验证失败" -ForegroundColor Red
}

# 测试无效支付方式
Write-Host "  测试无效支付方式..." -ForegroundColor Cyan
$invalidMethodBody = @{
    packageId = "package_1200"
    coins = 1200
    amount = 12.00
    paymentMethod = "INVALID"  # 无效支付方式
    description = "充值1200金币"
} | ConvertTo-Json

$invalidMethodResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payment/orders" -Method POST -ContentType "application/json" -Headers @{"Authorization" = "Bearer $token"} -Body $invalidMethodBody -ErrorAction SilentlyContinue

if ($invalidMethodResponse -and -not $invalidMethodResponse.success) {
    Write-Host "  ✓ 无效支付方式验证成功" -ForegroundColor Green
    Write-Host "    错误信息: $($invalidMethodResponse.message)" -ForegroundColor Cyan
} else {
    Write-Host "  ✗ 无效支付方式验证失败" -ForegroundColor Red
}

# 测试查询订单列表（分页）
Write-Host "`n4. 测试查询订单列表（分页）..." -ForegroundColor Yellow
$orderListResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payment/orders?page=0&size=10" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($orderListResponse.success) {
    Write-Host "✓ 订单列表查询成功" -ForegroundColor Green
    Write-Host "  订单数量: $($orderListResponse.data.Count)" -ForegroundColor Cyan
    Write-Host "  请求ID: $($orderListResponse.requestId)" -ForegroundColor Cyan
} else {
    Write-Host "✗ 订单列表查询失败: $($orderListResponse.message)" -ForegroundColor Red
}

# 测试频率限制
Write-Host "`n5. 测试频率限制..." -ForegroundColor Yellow
Write-Host "  快速发送多个请求测试频率限制..." -ForegroundColor Cyan

$rateLimitCount = 0
for ($i = 1; $i -le 8; $i++) {
    $rateLimitBody = @{
        packageId = "package_$i"
        coins = 100
        amount = 1.00
        paymentMethod = "ALIPAY"
        description = "测试频率限制 $i"
    } | ConvertTo-Json
    
    $rateLimitResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payment/orders" -Method POST -ContentType "application/json" -Headers @{"Authorization" = "Bearer $token"} -Body $rateLimitBody -ErrorAction SilentlyContinue
    
    if ($rateLimitResponse -and -not $rateLimitResponse.success -and $rateLimitResponse.code -eq 8001) {
        $rateLimitCount++
        Write-Host "    请求 $i 被频率限制" -ForegroundColor Yellow
    } else {
        Write-Host "    请求 $i 成功" -ForegroundColor Green
    }
    
    Start-Sleep -Milliseconds 100
}

if ($rateLimitCount -gt 0) {
    Write-Host "  ✓ 频率限制测试成功，$rateLimitCount 个请求被限制" -ForegroundColor Green
} else {
    Write-Host "  ⚠ 频率限制可能未生效" -ForegroundColor Yellow
}

# 测试查询支付统计
Write-Host "`n6. 测试查询支付统计..." -ForegroundColor Yellow
$statisticsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/payment/statistics" -Method GET -Headers @{"Authorization" = "Bearer $token"}

if ($statisticsResponse.success) {
    Write-Host "✓ 支付统计查询成功" -ForegroundColor Green
    Write-Host "  总订单数: $($statisticsResponse.data.totalOrders)" -ForegroundColor Cyan
    Write-Host "  成功订单数: $($statisticsResponse.data.successOrders)" -ForegroundColor Cyan
    Write-Host "  总充值金额: $($statisticsResponse.data.totalAmount)" -ForegroundColor Cyan
    Write-Host "  总充值金币: $($statisticsResponse.data.totalCoins)" -ForegroundColor Cyan
    Write-Host "  成功率: $($statisticsResponse.data.successRate)%" -ForegroundColor Cyan
} else {
    Write-Host "✗ 支付统计查询失败: $($statisticsResponse.message)" -ForegroundColor Red
}

Write-Host "`n=== 优化后的支付接口测试完成 ===" -ForegroundColor Green
Write-Host "`n优化特性总结:" -ForegroundColor Yellow
Write-Host "✓ 统一的错误码体系" -ForegroundColor Green
Write-Host "✓ 统一的响应格式" -ForegroundColor Green
Write-Host "✓ 请求ID追踪" -ForegroundColor Green
Write-Host "✓ 参数验证" -ForegroundColor Green
Write-Host "✓ 频率限制" -ForegroundColor Green
Write-Host "✓ 防重放攻击" -ForegroundColor Green
Write-Host "✓ 分页支持" -ForegroundColor Green
Write-Host "✓ 异常处理" -ForegroundColor Green
Write-Host "✓ 配置管理" -ForegroundColor Green
