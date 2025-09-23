# 简化的API测试脚本

$baseUrl = "http://localhost:8080"

Write-Host "测试API连接..." -ForegroundColor Green

try {
    # 测试健康检查
    $response = Invoke-RestMethod -Uri "$baseUrl/api/health" -Method GET
    Write-Host "✅ 后端服务运行正常" -ForegroundColor Green
    Write-Host "响应: $response"
} catch {
    Write-Host "❌ 后端服务连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n测试完成！" -ForegroundColor Green
