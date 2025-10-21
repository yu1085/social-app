# 测试消息API功能
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试消息系统 - 用户间消息发送功能" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/api"
$user1Id = 23820512
$user2Id = 23820513

Write-Host "📋 测试场景：用户1 (ID: $user1Id) ↔ 用户2 (ID: $user2Id)" -ForegroundColor Yellow
Write-Host ""

# 1. 用户1发送消息给用户2
Write-Host "1️⃣  用户1 发送消息给 用户2" -ForegroundColor Green
$sendUrl1 = "$baseUrl/message/send?senderId=$user1Id&receiverId=$user2Id&content=你好，我是用户1！&messageType=TEXT"
try {
    $response1 = Invoke-RestMethod -Uri $sendUrl1 -Method Post
    if ($response1.success) {
        Write-Host "   ✅ 发送成功！消息ID: $($response1.data.id)" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}
Write-Host ""

# 2. 用户2发送消息给用户1
Write-Host "2️⃣  用户2 发送消息给 用户1" -ForegroundColor Green
$sendUrl2 = "$baseUrl/message/send?senderId=$user2Id&receiverId=$user1Id&content=你好，我是用户2！&messageType=TEXT"
try {
    $response2 = Invoke-RestMethod -Uri $sendUrl2 -Method Post
    if ($response2.success) {
        Write-Host "   ✅ 发送成功！消息ID: $($response2.data.id)" -ForegroundColor Green
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}
Write-Host ""

# 3. 获取聊天记录
Write-Host "3️⃣  获取聊天记录" -ForegroundColor Green
$historyUrl = "$baseUrl/message/history?userId1=$user1Id&userId2=$user2Id"
try {
    $history = Invoke-RestMethod -Uri $historyUrl -Method Get
    if ($history.success) {
        Write-Host "   ✅ 共 $($history.data.Count) 条消息" -ForegroundColor Green
        foreach ($msg in $history.data) {
            $senderLabel = if ($msg.senderId -eq $user1Id) { "用户1" } else { "用户2" }
            Write-Host "   💬 $senderLabel : $($msg.content)" -ForegroundColor White
        }
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}
