# 增强礼物道具商城和VIP等级系统 完整API测试脚本

Write-Host "=== 增强礼物道具商城和VIP等级系统 完整API测试 ===" -ForegroundColor Green

# 等待应用启动
Write-Host "等待应用启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 测试健康检查
Write-Host "`n1. 测试健康检查API" -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method GET
    Write-Host "✅ 健康检查API测试成功" -ForegroundColor Green
    Write-Host "应用状态: $($health.status)"
    Write-Host "数据库状态: $($health.database)"
    Write-Host "Redis状态: $($health.redis)"
} catch {
    Write-Host "❌ 健康检查API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 创建测试用户
Write-Host "`n2. 创建测试用户" -ForegroundColor Cyan
$testUser = @{
    username = "testuser"
    password = "123456"
    nickname = "测试用户"
    phone = "13800138000"
    email = "test@example.com"
    gender = "MALE"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $testUser -ContentType "application/json"
    Write-Host "✅ 用户注册成功" -ForegroundColor Green
    Write-Host "用户ID: $($registerResponse.data.id)"
} catch {
    Write-Host "⚠️ 用户可能已存在，尝试登录..." -ForegroundColor Yellow
}

# 用户登录获取token
Write-Host "`n3. 用户登录获取JWT Token" -ForegroundColor Cyan
$loginData = @{
    username = "testuser"
    password = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.data.token
    Write-Host "✅ 登录成功" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 20))..."
} catch {
    Write-Host "❌ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "跳过需要认证的API测试" -ForegroundColor Yellow
    $token = $null
}

# 设置认证头
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 测试财富等级API
Write-Host "`n4. 测试财富等级API" -ForegroundColor Cyan
try {
    $wealthLevels = Invoke-RestMethod -Uri "http://localhost:8080/api/wealth/levels" -Method GET -Headers $headers
    Write-Host "✅ 财富等级API测试成功" -ForegroundColor Green
    Write-Host "财富等级数量: $($wealthLevels.data.Count)"
    $wealthLevels.data | ForEach-Object { Write-Host "  - $($_.name) (等级: $($_.level), 最小贡献: $($_.minContribution))" }
} catch {
    Write-Host "❌ 财富等级API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试道具商城API
Write-Host "`n5. 测试道具商城API" -ForegroundColor Cyan
try {
    $shopItems = Invoke-RestMethod -Uri "http://localhost:8080/api/shop/items" -Method GET -Headers $headers
    Write-Host "✅ 道具商城API测试成功" -ForegroundColor Green
    Write-Host "商品数量: $($shopItems.data.Count)"
    $shopItems.data | Select-Object -First 3 | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price), 分类: $($_.category))" }
} catch {
    Write-Host "❌ 道具商城API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试商品分类API
Write-Host "`n6. 测试商品分类API" -ForegroundColor Cyan
try {
    $categories = Invoke-RestMethod -Uri "http://localhost:8080/api/shop/categories" -Method GET -Headers $headers
    Write-Host "✅ 商品分类API测试成功" -ForegroundColor Green
    Write-Host "分类: $($categories.data -join ', ')"
} catch {
    Write-Host "❌ 商品分类API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试VIP等级API
Write-Host "`n7. 测试VIP等级API" -ForegroundColor Cyan
try {
    $vipLevels = Invoke-RestMethod -Uri "http://localhost:8080/api/vip/levels" -Method GET -Headers $headers
    Write-Host "✅ VIP等级API测试成功" -ForegroundColor Green
    Write-Host "VIP等级数量: $($vipLevels.data.Count)"
    $vipLevels.data | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price), 等级: $($_.level))" }
} catch {
    Write-Host "❌ VIP等级API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试礼物API
Write-Host "`n8. 测试礼物API" -ForegroundColor Cyan
try {
    $gifts = Invoke-RestMethod -Uri "http://localhost:8080/api/gifts" -Method GET -Headers $headers
    Write-Host "✅ 礼物API测试成功" -ForegroundColor Green
    Write-Host "礼物数量: $($gifts.data.Count)"
    $gifts.data | Select-Object -First 3 | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price), 分类: $($_.category))" }
} catch {
    Write-Host "❌ 礼物API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试增强礼物API
Write-Host "`n9. 测试增强礼物API" -ForegroundColor Cyan
try {
    $enhancedGifts = Invoke-RestMethod -Uri "http://localhost:8080/api/enhanced-gifts/categories" -Method GET -Headers $headers
    Write-Host "✅ 增强礼物API测试成功" -ForegroundColor Green
    Write-Host "礼物分类数量: $($enhancedGifts.data.Count)"
    $enhancedGifts.data | ForEach-Object { Write-Host "  - $($_.label) ($($_.value))" }
} catch {
    Write-Host "❌ 增强礼物API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试虚拟货币API
Write-Host "`n10. 测试虚拟货币API" -ForegroundColor Cyan
try {
    $currencyBalance = Invoke-RestMethod -Uri "http://localhost:8080/api/currency/balance" -Method GET -Headers $headers
    Write-Host "✅ 虚拟货币API测试成功" -ForegroundColor Green
    Write-Host "货币余额:"
    $currencyBalance.data.PSObject.Properties | ForEach-Object { Write-Host "  - $($_.Name): $($_.Value)" }
} catch {
    Write-Host "❌ 虚拟货币API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试用户成长值API
Write-Host "`n11. 测试用户成长值API" -ForegroundColor Cyan
try {
    $userGrowth = Invoke-RestMethod -Uri "http://localhost:8080/api/growth/stats" -Method GET -Headers $headers
    Write-Host "✅ 用户成长值API测试成功" -ForegroundColor Green
    Write-Host "总成长值: $($userGrowth.data.totalPoints)"
    Write-Host "当前等级: $($userGrowth.data.currentLevel)"
} catch {
    Write-Host "❌ 用户成长值API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试搜索功能
Write-Host "`n12. 测试搜索功能" -ForegroundColor Cyan
try {
    $searchResults = Invoke-RestMethod -Uri "http://localhost:8080/api/shop/search?keyword=VIP" -Method GET -Headers $headers
    Write-Host "✅ 搜索功能测试成功" -ForegroundColor Green
    Write-Host "搜索结果数量: $($searchResults.data.Count)"
} catch {
    Write-Host "❌ 搜索功能测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 完整API测试完成 ===" -ForegroundColor Green
Write-Host "如果所有测试都通过，说明增强礼物道具商城和VIP等级系统运行正常！" -ForegroundColor Green
