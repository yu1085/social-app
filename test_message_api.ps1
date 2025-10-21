# 测试消息系统API
$baseUrl = "http://localhost:8080/api"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试消息系统API" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 测试1: 获取会话列表
Write-Host "`n[测试1] 获取用户22491729的会话列表..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/message/conversations?userId=22491729" -Method GET
    Write-Host "✅ 成功" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "❌ 失败: $_" -ForegroundColor Red
}

# 测试2: 获取通话记录
Write-Host "`n[测试2] 获取用户22491729的通话记录..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/message/call-records?userId=22491729" -Method GET
    Write-Host "✅ 成功" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "❌ 失败: $_" -ForegroundColor Red
}

# 测试3: 发送消息
Write-Host "`n[测试3] 发送测试消息..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=22491729&receiverId=23820512&content=测试消息&type=TEXT" -Method POST
    Write-Host "✅ 成功" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "❌ 失败: $_" -ForegroundColor Red
}

# 测试4: 获取聊天记录
Write-Host "`n[测试4] 获取用户22491729和23820512的聊天记录..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/message/chat-history?userId1=22491729&userId2=23820512" -Method GET
    Write-Host "✅ 成功" -ForegroundColor Green
    Write-Host "共 $($response.data.Count) 条消息"
    Write-Host ($response | ConvertTo-Json -Depth 10)
} catch {
    Write-Host "❌ 失败: $_" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

