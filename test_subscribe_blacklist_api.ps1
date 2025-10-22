# 测试订阅和黑名单 API
# PowerShell 脚本

$baseUrl = "http://localhost:8080/api"

# 使用 video_receiver 用户的 token (用户ID: 22491729)
$token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIyNDkxNzI5LCJ1c2VybmFtZSI6InZpZGVvX3JlY2VpdmVyIiwic3ViIjoidmlkZW9fcmVjZWl2ZXIiLCJpYXQiOjE3NjExMDM1NjUsImV4cCI6MTc2MTE4OTk2NX0.HlGoarwSfdkRDJFL0q2REtog82xjrmFX7THoQIUVN9g"
$targetUserId = 23820512  # video_caller

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试订阅和黑名单 API" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. 测试订阅用户
Write-Host "1. 测试订阅用户 (用户ID: $targetUserId)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/subscribe" `
        -Method POST `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   订阅成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   订阅失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 2. 检查是否已订阅
Write-Host "`n2. 检查是否已订阅" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-subscribed" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   检查成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   检查失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 3. 取消订阅
Write-Host "`n3. 取消订阅" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/subscribe" `
        -Method DELETE `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   取消订阅成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   取消订阅失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 4. 再次检查是否已订阅 (应该为false)
Write-Host "`n4. 再次检查是否已订阅 (应该为false)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-subscribed" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   检查成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   检查失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 5. 测试加入黑名单
Write-Host "`n5. 测试加入黑名单 (用户ID: $targetUserId)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/blacklist" `
        -Method POST `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   加入黑名单成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   加入黑名单失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 6. 检查是否在黑名单
Write-Host "`n6. 检查是否在黑名单" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-blacklisted" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   检查成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   检查失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 7. 移出黑名单
Write-Host "`n7. 移出黑名单" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/blacklist" `
        -Method DELETE `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   移出黑名单成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   移出黑名单失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 8. 再次检查是否在黑名单 (应该为false)
Write-Host "`n8. 再次检查是否在黑名单 (应该为false)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-blacklisted" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   检查成功:" -ForegroundColor Green
    Write-Host "   $($response | ConvertTo-Json -Depth 10)" -ForegroundColor White
} catch {
    Write-Host "   检查失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试完成" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
