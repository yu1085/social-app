# 测试WebSocket混合推送功能
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan
Write-Host "测试WebSocket混合推送系统" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"
$senderId = 23820512  # video_caller
$receiverId = 22491729  # video_receiver

Write-Host "`n发送者: $senderId (video_caller)" -ForegroundColor Yellow
Write-Host "接收者: $receiverId (video_receiver)" -ForegroundColor Yellow

Write-Host "`n=== 测试场景说明 ===" -ForegroundColor Green
Write-Host "1. 如果接收者的Android APP已连接WebSocket → 消息通过WebSocket推送（实时）" -ForegroundColor White
Write-Host "2. 如果接收者的Android APP未连接WebSocket → 消息通过JPush推送（离线）" -ForegroundColor White
Write-Host ""

Write-Host "=== 测试开始 ===" -ForegroundColor Green
Write-Host "请确保:" -ForegroundColor Yellow
Write-Host "  1. 后端服务已启动 (localhost:8080)" -ForegroundColor Yellow
Write-Host "  2. 接收者的Android APP已打开（测试WebSocket在线推送）" -ForegroundColor Yellow
Write-Host "     或者 APP已关闭（测试JPush离线推送）" -ForegroundColor Yellow
Write-Host ""

# 发送测试消息
Write-Host "`n=== 发送测试消息1 ===" -ForegroundColor Cyan
$response1 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=WebSocket测试消息1&messageType=TEXT" -Method POST
if ($response1.success) {
    Write-Host "✅ 消息1发送成功 - messageId: $($response1.data.id)" -ForegroundColor Green
} else {
    Write-Host "❌ 消息1发送失败: $($response1.message)" -ForegroundColor Red
}

Start-Sleep -Seconds 2

Write-Host "`n=== 发送测试消息2 ===" -ForegroundColor Cyan
$response2 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=WebSocket测试消息2&messageType=TEXT" -Method POST
if ($response2.success) {
    Write-Host "✅ 消息2发送成功 - messageId: $($response2.data.id)" -ForegroundColor Green
} else {
    Write-Host "❌ 消息2发送失败: $($response2.message)" -ForegroundColor Red
}

Start-Sleep -Seconds 2

Write-Host "`n=== 发送测试消息3 ===" -ForegroundColor Cyan
$response3 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=WebSocket测试消息3&messageType=TEXT" -Method POST
if ($response3.success) {
    Write-Host "✅ 消息3发送成功 - messageId: $($response3.data.id)" -ForegroundColor Green
} else {
    Write-Host "❌ 消息3发送失败: $($response3.message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host ""
Write-Host "请检查:" -ForegroundColor Yellow
Write-Host ""
Write-Host "【后端控制台日志】" -ForegroundColor Cyan
Write-Host "  查找以下日志判断推送方式:" -ForegroundColor White
Write-Host "  - '✅ 消息通过WebSocket发送成功' → WebSocket在线推送" -ForegroundColor Green
Write-Host "  - '⚠️ 用户离线，降级到JPush推送' → JPush离线推送" -ForegroundColor Yellow
Write-Host ""
Write-Host "【Android APP】" -ForegroundColor Cyan
Write-Host "  - 如果APP在线且连接WebSocket → 应该实时收到消息（无JPush通知）" -ForegroundColor Green
Write-Host "  - 如果APP离线 → 应该收到JPush推送通知" -ForegroundColor Yellow
Write-Host ""
Write-Host "【预期效果】" -ForegroundColor Cyan
Write-Host "  - 用户在线时：3条消息全部通过WebSocket推送，无JPush调用" -ForegroundColor Green
Write-Host "  - 用户离线时：3条消息降级到JPush推送" -ForegroundColor Yellow
Write-Host "  - 混合场景：部分WebSocket，部分JPush" -ForegroundColor Yellow
Write-Host ""
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan
