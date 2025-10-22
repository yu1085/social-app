# 简单测试亲密度系统API - 使用数据库中已有的用户
$baseUrl = "http://localhost:8080/api"
$userId1 = 22491729  # video_receiver
$userId2 = 23820512  # video_caller

Write-Host "=================================="
Write-Host "  亲密度系统API简单测试"
Write-Host "=================================="
Write-Host ""

# 1. 查询所有等级配置（不需要认证）
Write-Host "1. 查询所有亲密度等级配置..." -ForegroundColor Yellow
try {
    $levelsResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/levels" -Method GET

    if ($levelsResponse.success) {
        Write-Host "✅ 查询成功 - 共 $($levelsResponse.data.Count) 个等级" -ForegroundColor Green
        $levelsResponse.data | ForEach-Object {
            Write-Host "  Lv.$($_.level) - 需要温度: $($_.requiredTemperature)°C"
        }
    } else {
        Write-Host "❌ 查询失败: $($levelsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=================================="
Write-Host "  测试完成"
Write-Host "  说明: 其他API需要JWT token认证"
Write-Host "  建议: 从Android应用或Postman测试完整流程"
Write-Host "=================================="
Write-Host ""
Write-Host "API列表："
Write-Host "  GET  /api/intimacy/levels - 查询等级配置（无需认证）"
Write-Host "  POST /api/intimacy/action - 记录亲密度行为（需认证）"
Write-Host "  GET  /api/intimacy/list - 查询亲密度列表（需认证）"
Write-Host "  GET  /api/intimacy/{targetUserId} - 查询亲密度详情（需认证）"
Write-Host "  GET  /api/intimacy/rewards/unclaimed - 查询未领取奖励（需认证）"
Write-Host "  POST /api/intimacy/rewards/{rewardId}/claim - 领取奖励（需认证）"
Write-Host ""
