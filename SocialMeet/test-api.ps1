# 增强礼物道具商城和VIP等级系统 API 测试脚本

Write-Host "=== 增强礼物道具商城和VIP等级系统 API 测试 ===" -ForegroundColor Green

# 等待应用启动
Write-Host "等待应用启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 测试财富等级API
Write-Host "`n1. 测试财富等级API" -ForegroundColor Cyan
try {
    $wealthLevels = Invoke-RestMethod -Uri "http://localhost:8080/api/wealth/levels" -Method GET
    Write-Host "✅ 财富等级API测试成功" -ForegroundColor Green
    Write-Host "财富等级数量: $($wealthLevels.data.Count)"
    $wealthLevels.data | ForEach-Object { Write-Host "  - $($_.name) (等级: $($_.level))" }
} catch {
    Write-Host "❌ 财富等级API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试道具商城API
Write-Host "`n2. 测试道具商城API" -ForegroundColor Cyan
try {
    $shopItems = Invoke-RestMethod -Uri "http://localhost:8080/api/shop/items" -Method GET
    Write-Host "✅ 道具商城API测试成功" -ForegroundColor Green
    Write-Host "商品数量: $($shopItems.data.Count)"
    $shopItems.data | Select-Object -First 3 | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price))" }
} catch {
    Write-Host "❌ 道具商城API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试商品分类API
Write-Host "`n3. 测试商品分类API" -ForegroundColor Cyan
try {
    $categories = Invoke-RestMethod -Uri "http://localhost:8080/api/shop/categories" -Method GET
    Write-Host "✅ 商品分类API测试成功" -ForegroundColor Green
    Write-Host "分类: $($categories.data -join ', ')"
} catch {
    Write-Host "❌ 商品分类API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试VIP等级API
Write-Host "`n4. 测试VIP等级API" -ForegroundColor Cyan
try {
    $vipLevels = Invoke-RestMethod -Uri "http://localhost:8080/api/vip/levels" -Method GET
    Write-Host "✅ VIP等级API测试成功" -ForegroundColor Green
    Write-Host "VIP等级数量: $($vipLevels.data.Count)"
    $vipLevels.data | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price))" }
} catch {
    Write-Host "❌ VIP等级API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试礼物API
Write-Host "`n5. 测试礼物API" -ForegroundColor Cyan
try {
    $gifts = Invoke-RestMethod -Uri "http://localhost:8080/api/gifts" -Method GET
    Write-Host "✅ 礼物API测试成功" -ForegroundColor Green
    Write-Host "礼物数量: $($gifts.data.Count)"
    $gifts.data | Select-Object -First 3 | ForEach-Object { Write-Host "  - $($_.name) (价格: $($_.price))" }
} catch {
    Write-Host "❌ 礼物API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试增强礼物API
Write-Host "`n6. 测试增强礼物API" -ForegroundColor Cyan
try {
    $enhancedGifts = Invoke-RestMethod -Uri "http://localhost:8080/api/enhanced-gifts/categories" -Method GET
    Write-Host "✅ 增强礼物API测试成功" -ForegroundColor Green
    Write-Host "礼物分类数量: $($enhancedGifts.data.Count)"
    $enhancedGifts.data | ForEach-Object { Write-Host "  - $($_.label) ($($_.value))" }
} catch {
    Write-Host "❌ 增强礼物API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试虚拟货币API
Write-Host "`n7. 测试虚拟货币API" -ForegroundColor Cyan
try {
    $currencyBalance = Invoke-RestMethod -Uri "http://localhost:8080/api/currency/balance" -Method GET -Headers @{"Authorization" = "Bearer test-token"}
    Write-Host "✅ 虚拟货币API测试成功" -ForegroundColor Green
    Write-Host "货币余额: $($currencyBalance.data | ConvertTo-Json -Compress)"
} catch {
    Write-Host "❌ 虚拟货币API测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== API测试完成 ===" -ForegroundColor Green
