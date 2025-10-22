# Test script for user relationship menu features
# 测试用户关系菜单的所有API功能

$baseUrl = "http://localhost:8080/api"
$testUserId = 23820512
$targetUserId = 23820513

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "用户关系菜单API测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# First login to get token
Write-Host "1. 登录获取Token..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body (@{
    phone = "13800138001"
    verificationCode = "123456"
} | ConvertTo-Json) -ContentType "application/json"

$token = "Bearer " + $loginResponse.data.token
Write-Host "✓ Token获取成功" -ForegroundColor Green
Write-Host ""

# Test 1: Subscribe User
Write-Host "2. 测试订阅用户..." -ForegroundColor Yellow
try {
    $subscribeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/subscribe" -Method POST -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 订阅成功: $($subscribeResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 订阅失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 2: Check if Subscribed
Write-Host "3. 检查订阅状态..." -ForegroundColor Yellow
try {
    $isSubscribedResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-subscribed" -Method GET -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 订阅状态: $($isSubscribedResponse.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 检查订阅状态失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 3: Set User Remark
Write-Host "4. 设置用户备注..." -ForegroundColor Yellow
try {
    $remarkResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/remark?remark=我的好朋友" -Method POST -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 备注设置成功: $($remarkResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 设置备注失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Get User Remark
Write-Host "5. 获取用户备注..." -ForegroundColor Yellow
try {
    $getRemarkResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/remark" -Method GET -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 用户备注: $($getRemarkResponse.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取备注失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 5: Query Account Status
Write-Host "6. 查询账号状态..." -ForegroundColor Yellow
try {
    $accountStatusResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/account-status" -Method GET -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 账号状态查询成功:" -ForegroundColor Green
    Write-Host "  - 用户ID: $($accountStatusResponse.data.id)" -ForegroundColor Cyan
    Write-Host "  - 用户名: $($accountStatusResponse.data.username)" -ForegroundColor Cyan
    Write-Host "  - 昵称: $($accountStatusResponse.data.nickname)" -ForegroundColor Cyan
    Write-Host "  - 在线状态: $($accountStatusResponse.data.isOnline)" -ForegroundColor Cyan
    Write-Host "  - VIP状态: $($accountStatusResponse.data.isVip)" -ForegroundColor Cyan
    Write-Host "  - 账号状态: $($accountStatusResponse.data.status)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ 查询账号状态失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 6: Add to Blacklist
Write-Host "7. 测试加入黑名单..." -ForegroundColor Yellow
try {
    $blacklistResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/blacklist" -Method POST -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 加入黑名单成功: $($blacklistResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 加入黑名单失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 7: Check if Blacklisted
Write-Host "8. 检查黑名单状态..." -ForegroundColor Yellow
try {
    $isBlacklistedResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-blacklisted" -Method GET -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 黑名单状态: $($isBlacklistedResponse.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 检查黑名单状态失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 8: Remove from Blacklist
Write-Host "9. 从黑名单移除..." -ForegroundColor Yellow
try {
    $removeBlacklistResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/blacklist" -Method DELETE -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 移除黑名单成功: $($removeBlacklistResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 移除黑名单失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 9: Unsubscribe User
Write-Host "10. 取消订阅..." -ForegroundColor Yellow
try {
    $unsubscribeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/subscribe" -Method DELETE -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 取消订阅成功: $($unsubscribeResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 取消订阅失败: $_" -ForegroundColor Red
}
Write-Host ""

# Test 10: Like/Unlike (existing functionality)
Write-Host "11. 测试喜欢/取消喜欢..." -ForegroundColor Yellow
try {
    # Add like
    $likeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method POST -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 喜欢成功: $($likeResponse.message)" -ForegroundColor Green

    # Check like status
    $isLikedResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 喜欢状态: $($isLikedResponse.data)" -ForegroundColor Green

    # Remove like
    $unlikeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method DELETE -Headers @{
        "Authorization" = $token
    }
    Write-Host "✓ 取消喜欢成功: $($unlikeResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 测试喜欢功能失败: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "所有API测试完成!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
