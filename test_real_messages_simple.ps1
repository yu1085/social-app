# 测试真实消息显示功能
Write-Host "=== 测试真实消息API ===" -ForegroundColor Green

# 测试会话API
Write-Host "`n1. 测试获取会话列表API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/message/conversations?userId=22491729" -Method GET
    Write-Host "✓ API调用成功" -ForegroundColor Green
    
    if ($response.success -and $response.data) {
        Write-Host "✓ 成功获取 $($response.data.Count) 个会话" -ForegroundColor Green
        
        foreach ($conversation in $response.data) {
            Write-Host "  - 用户: $($conversation.nickname) (ID: $($conversation.userId))" -ForegroundColor White
            Write-Host "    最后消息: $($conversation.lastMessage)" -ForegroundColor Gray
            Write-Host "    未读数量: $($conversation.unreadCount)" -ForegroundColor Gray
        }
    } else {
        Write-Host "✗ 没有获取到会话数据" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "现在您可以在应用中查看真实的消息列表了！" -ForegroundColor Yellow
