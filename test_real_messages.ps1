# 测试真实消息显示功能
Write-Host "=== 测试真实消息API ===" -ForegroundColor Green

# 测试会话API
Write-Host "`n1. 测试获取会话列表API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/message/conversations?userId=22491729" -Method GET
    Write-Host "✓ API调用成功" -ForegroundColor Green
    Write-Host "返回数据: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Cyan
    
    if ($response.success -and $response.data) {
        Write-Host "✓ 成功获取 $($response.data.Count) 个会话" -ForegroundColor Green
        
        foreach ($conversation in $response.data) {
            Write-Host "  - 用户: $($conversation.nickname) (ID: $($conversation.userId))" -ForegroundColor White
            Write-Host "    最后消息: $($conversation.lastMessage)" -ForegroundColor Gray
            Write-Host "    未读数量: $($conversation.unreadCount)" -ForegroundColor Gray
            Write-Host "    时间: $($conversation.lastMessageTime)" -ForegroundColor Gray
        }
    } else {
        Write-Host "✗ 没有获取到会话数据" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试聊天记录API
Write-Host "`n2. 测试获取聊天记录API..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/message/history?userId1=22491729&userId2=23820512" -Method GET
    Write-Host "✓ 聊天记录API调用成功" -ForegroundColor Green
    Write-Host "返回消息数量: $($response.data.Count)" -ForegroundColor Cyan
    
    if ($response.data.Count -gt 0) {
        Write-Host "前5条消息:" -ForegroundColor White
        for ($i = 0; $i -lt [Math]::Min(5, $response.data.Count); $i++) {
            $msg = $response.data[$i]
            Write-Host "  $($i+1). [$($msg.messageType)] $($msg.content)" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "✗ 聊天记录API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 检查数据库中的消息数量
Write-Host "`n3. 检查数据库消息数量..." -ForegroundColor Yellow
try {
    $result = & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -proot socialmeet -e "SELECT COUNT(*) as message_count FROM messages WHERE (sender_id = 22491729 AND receiver_id = 23820512) OR (sender_id = 23820512 AND receiver_id = 22491729);" 2>$null
    if ($result) {
        $count = ($result | Select-String "message_count" -A 1).Line.Trim()
        Write-Host "✓ 数据库中两个用户之间的消息数量: $count" -ForegroundColor Green
    }
} catch {
    Write-Host "✗ 数据库查询失败" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "现在您可以在应用中查看真实的消息列表了！" -ForegroundColor Yellow
Write-Host "用户22491729 (video_receiver) 应该能看到与用户23820512 (video_caller) 的聊天记录" -ForegroundColor Yellow
