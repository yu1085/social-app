# 测试消息推送问题 - 调试版本
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan
Write-Host "测试快速连续发送消息的JPush推送" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"
$senderId = 23820512  # video_caller
$receiverId = 22491729  # video_receiver

Write-Host "`n发送者: $senderId (video_caller)" -ForegroundColor Yellow
Write-Host "接收者: $receiverId (video_receiver)" -ForegroundColor Yellow

Write-Host "`n=== 测试1: 发送第一条消息 ===" -ForegroundColor Green
$response1 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=测试消息1&messageType=TEXT" -Method POST
Write-Host "✅ 消息1发送成功 - messageId: $($response1.data.id)" -ForegroundColor Green
Start-Sleep -Seconds 2

Write-Host "`n=== 测试2: 发送第二条消息 (2秒后) ===" -ForegroundColor Green
$response2 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=测试消息2&messageType=TEXT" -Method POST
Write-Host "✅ 消息2发送成功 - messageId: $($response2.data.id)" -ForegroundColor Green
Start-Sleep -Seconds 2

Write-Host "`n=== 测试3: 发送第三条消息 (2秒后) ===" -ForegroundColor Green
$response3 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=测试消息3&messageType=TEXT" -Method POST
Write-Host "✅ 消息3发送成功 - messageId: $($response3.data.id)" -ForegroundColor Green
Start-Sleep -Seconds 2

Write-Host "`n=== 测试4: 快速连续发送两条消息 (间隔1秒) ===" -ForegroundColor Yellow
$response4 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=快速消息A&messageType=TEXT" -Method POST
Write-Host "✅ 消息A发送成功 - messageId: $($response4.data.id)" -ForegroundColor Green
Start-Sleep -Seconds 1

$response5 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=快速消息B&messageType=TEXT" -Method POST
Write-Host "✅ 消息B发送成功 - messageId: $($response5.data.id)" -ForegroundColor Green
Start-Sleep -Seconds 1

Write-Host "`n=== 测试5: 极快速连续发送三条消息 (间隔0.5秒) ===" -ForegroundColor Red
$response6 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=极快消息X&messageType=TEXT" -Method POST
Write-Host "✅ 消息X发送成功 - messageId: $($response6.data.id)" -ForegroundColor Green
Start-Sleep -Milliseconds 500

$response7 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=极快消息Y&messageType=TEXT" -Method POST
Write-Host "✅ 消息Y发送成功 - messageId: $($response7.data.id)" -ForegroundColor Green
Start-Sleep -Milliseconds 500

$response8 = Invoke-RestMethod -Uri "$baseUrl/message/send?senderId=$senderId&receiverId=$receiverId&content=极快消息Z&messageType=TEXT" -Method POST
Write-Host "✅ 消息Z发送成功 - messageId: $($response8.data.id)" -ForegroundColor Green

Write-Host "`n=== 测试总结 ===" -ForegroundColor Cyan
Write-Host "已发送8条测试消息" -ForegroundColor Cyan
Write-Host "消息ID: $($response1.data.id), $($response2.data.id), $($response3.data.id), $($response4.data.id), $($response5.data.id), $($response6.data.id), $($response7.data.id), $($response8.data.id)" -ForegroundColor Yellow

Write-Host "`n请检查后端控制台日志，查看JPush推送日志:" -ForegroundColor Yellow
Write-Host "  - 是否所有8条消息都触发了推送?" -ForegroundColor Yellow
Write-Host "  - 是否有推送失败的消息?" -ForegroundColor Yellow
Write-Host "  - 推送失败的具体原因是什么?" -ForegroundColor Yellow

Write-Host "`n请在Android设备上检查:" -ForegroundColor Yellow
Write-Host "  - 是否收到了所有8条JPush通知?" -ForegroundColor Yellow
Write-Host "  - 哪些消息没有收到推送?" -ForegroundColor Yellow

Write-Host "`n═══════════════════════════════════════" -ForegroundColor Cyan
