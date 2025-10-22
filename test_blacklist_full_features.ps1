# 测试完整黑名单功能 API
# PowerShell 脚本

$baseUrl = "http://localhost:8080/api"

# 使用 video_receiver 用户的 token (用户ID: 22491729)
$token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIyNDkxNzI5LCJ1c2VybmFtZSI6InZpZGVvX3JlY2VpdmVyIiwic3ViIjoidmlkZW9fcmVjZWl2ZXIiLCJpYXQiOjE3NjExMDM1NjUsImV4cCI6MTc2MTE4OTk2NX0.HlGoarwSfdkRDJFL0q2REtog82xjrmFX7THoQIUVN9g"

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试完整黑名单功能" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. 获取当前黑名单列表
Write-Host "1. 获取当前黑名单列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/blacklist" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   获取成功:" -ForegroundColor Green
    Write-Host "   当前黑名单用户数: $($response.data.Count)" -ForegroundColor White
    foreach ($user in $response.data) {
        Write-Host "   - ID: $($user.id), 用户名: $($user.username), 昵称: $($user.nickname)" -ForegroundColor White
    }
} catch {
    Write-Host "   获取失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 2. 添加用户到黑名单
Write-Host "`n2. 添加用户到黑名单 (用户ID: 23820512, 23820513)" -ForegroundColor Yellow
$usersToBlock = @(23820512, 23820513)
foreach ($userId in $usersToBlock) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/users/$userId/blacklist" `
            -Method POST `
            -Headers $headers `
            -ErrorAction Stop

        Write-Host "   添加用户 $userId 成功: $($response.message)" -ForegroundColor Green
    } catch {
        Write-Host "   添加用户 $userId 失败:" -ForegroundColor Red
        Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 500
}

Start-Sleep -Seconds 1

# 3. 再次获取黑名单列表（应该包含刚添加的用户）
Write-Host "`n3. 再次获取黑名单列表（应该包含刚添加的用户）" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/blacklist" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   获取成功:" -ForegroundColor Green
    Write-Host "   当前黑名单用户数: $($response.data.Count)" -ForegroundColor White
    foreach ($user in $response.data) {
        Write-Host "   - ID: $($user.id), 用户名: $($user.username), 昵称: $($user.nickname)" -ForegroundColor White
    }
} catch {
    Write-Host "   获取失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 4. 测试推荐用户列表（应该过滤黑名单用户）
Write-Host "`n4. 测试推荐用户列表（应该过滤黑名单用户）" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/recommended?size=10" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   获取成功:" -ForegroundColor Green
    Write-Host "   推荐用户数: $($response.data.Count)" -ForegroundColor White
    $blacklistedFound = $false
    foreach ($user in $response.data) {
        if ($usersToBlock -contains $user.id) {
            Write-Host "   ❌ 发现黑名单用户: ID: $($user.id), 用户名: $($user.username)" -ForegroundColor Red
            $blacklistedFound = $true
        }
    }
    if (-not $blacklistedFound) {
        Write-Host "   ✅ 推荐列表中没有黑名单用户" -ForegroundColor Green
    }
} catch {
    Write-Host "   获取失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 5. 测试知友列表（应该过滤黑名单用户）
Write-Host "`n5. 测试知友列表（应该过滤黑名单用户）" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/acquaintances?size=10" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   获取成功:" -ForegroundColor Green
    Write-Host "   知友用户数: $($response.data.Count)" -ForegroundColor White
    $blacklistedFound = $false
    foreach ($user in $response.data) {
        if ($usersToBlock -contains $user.id) {
            Write-Host "   ❌ 发现黑名单用户: ID: $($user.id), 用户名: $($user.username)" -ForegroundColor Red
            $blacklistedFound = $true
        }
    }
    if (-not $blacklistedFound) {
        Write-Host "   ✅ 知友列表中没有黑名单用户" -ForegroundColor Green
    }
} catch {
    Write-Host "   获取失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 6. 批量移出黑名单
Write-Host "`n6. 批量移出黑名单" -ForegroundColor Yellow
try {
    $body = $usersToBlock | ConvertTo-Json
    $response = Invoke-RestMethod -Uri "$baseUrl/users/blacklist/batch" `
        -Method DELETE `
        -Headers $headers `
        -Body $body `
        -ErrorAction Stop

    Write-Host "   移出成功: $($response.message)" -ForegroundColor Green
} catch {
    Write-Host "   移出失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Start-Sleep -Seconds 1

# 7. 最后再次获取黑名单列表（应该为空或不包含刚移出的用户）
Write-Host "`n7. 最后再次获取黑名单列表（应该为空或不包含刚移出的用户）" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/blacklist" `
        -Method GET `
        -Headers $headers `
        -ErrorAction Stop

    Write-Host "   获取成功:" -ForegroundColor Green
    Write-Host "   当前黑名单用户数: $($response.data.Count)" -ForegroundColor White
    if ($response.data.Count -eq 0) {
        Write-Host "   ✅ 黑名单为空" -ForegroundColor Green
    } else {
        foreach ($user in $response.data) {
            Write-Host "   - ID: $($user.id), 用户名: $($user.username), 昵称: $($user.nickname)" -ForegroundColor White
        }
    }
} catch {
    Write-Host "   获取失败:" -ForegroundColor Red
    Write-Host "   $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "测试完成" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
