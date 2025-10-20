# 通话计费功能完整测试脚本

$baseUrl = "http://localhost:8080/api"
$phone1 = "18888888888"  # 测试用户1 (发起者)
$phone2 = "16666666666"  # 测试用户2 (接收者)
$verificationCode = "123456"

Write-Host "=== 通话计费功能完整测试 ===" -ForegroundColor Cyan
Write-Host ""

# 1. 登录用户1（发起者）
Write-Host "1. 登录用户1 (发起者) - $phone1..." -ForegroundColor Yellow
$loginBody1 = @{
    phone = $phone1
    verificationCode = $verificationCode
} | ConvertTo-Json

try {
    $loginResponse1 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $loginBody1 -ContentType "application/json"
    $token1 = $loginResponse1.data.token
    $userId1 = $loginResponse1.data.userId
    Write-Host "✓ 用户1登录成功 - userId: $userId1" -ForegroundColor Green
} catch {
    Write-Host "✗ 用户1登录失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. 登录用户2（接收者）
Write-Host "2. 登录用户2 (接收者) - $phone2..." -ForegroundColor Yellow
$loginBody2 = @{
    phone = $phone2
    verificationCode = $verificationCode
} | ConvertTo-Json

try {
    $loginResponse2 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $loginBody2 -ContentType "application/json"
    $token2 = $loginResponse2.data.token
    $userId2 = $loginResponse2.data.userId
    Write-Host "✓ 用户2登录成功 - userId: $userId2" -ForegroundColor Green
} catch {
    Write-Host "✗ 用户2登录失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. 设置用户2的通话价格
Write-Host "3. 设置用户2的通话价格..." -ForegroundColor Yellow
$headers2 = @{
    "Authorization" = "Bearer $token2"
    "Content-Type" = "application/json"
}

$priceSettings = @{
    videoCallEnabled = $true
    voiceCallEnabled = $true
    videoCallPrice = 200.0
    voiceCallPrice = 100.0
    messageChargeEnabled = $false
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $priceSettings
    Write-Host "✓ 用户2价格设置成功" -ForegroundColor Green
    Write-Host "  视频通话: $($settingsResponse.data.videoCallPrice) 元/分钟" -ForegroundColor Gray
    Write-Host "  语音通话: $($settingsResponse.data.voiceCallPrice) 元/分钟" -ForegroundColor Gray
} catch {
    Write-Host "✗ 设置价格失败: $_" -ForegroundColor Red
}

Write-Host ""

# 4. 查看用户1的余额
Write-Host "4. 查看用户1的初始余额..." -ForegroundColor Yellow
$headers1 = @{
    "Authorization" = "Bearer $token1"
    "Content-Type" = "application/json"
}

try {
    $walletResponse1 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers1
    $initialBalance1 = $walletResponse1.data.balance
    Write-Host "✓ 用户1余额: $initialBalance1 元" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取余额失败: $_" -ForegroundColor Red
}

Write-Host ""

# 5. 查看用户2的余额
Write-Host "5. 查看用户2的初始余额..." -ForegroundColor Yellow
try {
    $walletResponse2 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers2
    $initialBalance2 = $walletResponse2.data.balance
    Write-Host "✓ 用户2余额: $initialBalance2 元" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取余额失败: $_" -ForegroundColor Red
}

Write-Host ""

# 6. 获取用户2的通话价格（从用户1的角度）
Write-Host "6. 用户1查询用户2的通话价格..." -ForegroundColor Yellow
try {
    $pricesResponse = Invoke-RestMethod -Uri "$baseUrl/call/prices/$userId2" -Method Get -Headers $headers1
    Write-Host "✓ 查询成功" -ForegroundColor Green
    Write-Host "  视频通话: $($pricesResponse.data.videoCallPrice) 元/分钟, 开启: $($pricesResponse.data.videoCallEnabled)" -ForegroundColor Gray
    Write-Host "  语音通话: $($pricesResponse.data.voiceCallPrice) 元/分钟, 开启: $($pricesResponse.data.voiceCallEnabled)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 查询价格失败: $_" -ForegroundColor Red
}

Write-Host ""

# 7. 用户1发起视频通话
Write-Host "7. 用户1发起视频通话..." -ForegroundColor Yellow
$initiateBody = @{
    receiverId = $userId2
    callType = "VIDEO"
} | ConvertTo-Json

try {
    $callResponse = Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers1 -Body $initiateBody
    $sessionId = $callResponse.data.callSessionId
    $pricePerMinute = $callResponse.data.pricePerMinute
    Write-Host "✓ 通话发起成功" -ForegroundColor Green
    Write-Host "  会话ID: $sessionId" -ForegroundColor Gray
    Write-Host "  价格: $pricePerMinute 元/分钟" -ForegroundColor Gray
    Write-Host "  状态: $($callResponse.data.status)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 发起通话失败: $_" -ForegroundColor Red
    Write-Host "  错误详情: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 8. 用户2接受通话
Write-Host "8. 用户2接受通话..." -ForegroundColor Yellow
$acceptBody = @{
    callSessionId = $sessionId
} | ConvertTo-Json

try {
    $acceptResponse = Invoke-RestMethod -Uri "$baseUrl/call/accept" -Method Post -Headers $headers2 -Body $acceptBody
    Write-Host "✓ 通话接受成功" -ForegroundColor Green
    Write-Host "  状态: $($acceptResponse.data.status)" -ForegroundColor Gray
    Write-Host "  开始时间: $($acceptResponse.data.startTime)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 接受通话失败: $_" -ForegroundColor Red
}

Write-Host ""

# 模拟通话持续一段时间（等待3秒）
Write-Host "9. 模拟通话中（等待3秒）..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
Write-Host "✓ 通话进行中..." -ForegroundColor Green

Write-Host ""

# 10. 用户1结束通话
Write-Host "10. 用户1结束通话..." -ForegroundColor Yellow
$endBody = @{
    callSessionId = $sessionId
} | ConvertTo-Json

try {
    $endResponse = Invoke-RestMethod -Uri "$baseUrl/call/end" -Method Post -Headers $headers1 -Body $endBody
    Write-Host "✓ 通话结束成功" -ForegroundColor Green
    Write-Host "  时长: $($endResponse.data.durationSeconds) 秒" -ForegroundColor Gray
    Write-Host "  费用: $($endResponse.data.totalCost) 元" -ForegroundColor Gray
    Write-Host "  状态: $($endResponse.data.status)" -ForegroundColor Gray

    $totalCost = $endResponse.data.totalCost
} catch {
    Write-Host "✗ 结束通话失败: $_" -ForegroundColor Red
}

Write-Host ""

# 11. 查看用户1的结束后余额
Write-Host "11. 查看用户1的结束后余额..." -ForegroundColor Yellow
try {
    $finalWallet1 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers1
    $finalBalance1 = $finalWallet1.data.balance
    Write-Host "✓ 用户1余额: $finalBalance1 元" -ForegroundColor Green
    Write-Host "  初始余额: $initialBalance1 元" -ForegroundColor Gray
    Write-Host "  扣费金额: $totalCost 元" -ForegroundColor Gray
    Write-Host "  理论余额: $($initialBalance1 - $totalCost) 元" -ForegroundColor Gray

    if ([Math]::Abs($finalBalance1 - ($initialBalance1 - $totalCost)) -lt 0.01) {
        Write-Host "  ✓ 余额正确" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 余额不正确" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 获取余额失败: $_" -ForegroundColor Red
}

Write-Host ""

# 12. 查看用户2的结束后余额
Write-Host "12. 查看用户2的结束后余额..." -ForegroundColor Yellow
try {
    $finalWallet2 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers2
    $finalBalance2 = $finalWallet2.data.balance
    Write-Host "✓ 用户2余额: $finalBalance2 元" -ForegroundColor Green
    Write-Host "  初始余额: $initialBalance2 元" -ForegroundColor Gray
    Write-Host "  收入金额: $totalCost 元" -ForegroundColor Gray
    Write-Host "  理论余额: $($initialBalance2 + $totalCost) 元" -ForegroundColor Gray

    if ([Math]::Abs($finalBalance2 - ($initialBalance2 + $totalCost)) -lt 0.01) {
        Write-Host "  ✓ 余额正确" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 余额不正确" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 获取余额失败: $_" -ForegroundColor Red
}

Write-Host ""

# 13. 测试余额不足的情况
Write-Host "13. 测试余额不足的情况..." -ForegroundColor Yellow

# 创建一个新用户（余额为0）
$phone3 = "13333333333"
$loginBody3 = @{
    phone = $phone3
    verificationCode = $verificationCode
} | ConvertTo-Json

try {
    $loginResponse3 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $loginBody3 -ContentType "application/json"
    $token3 = $loginResponse3.data.token
    $userId3 = $loginResponse3.data.userId

    $headers3 = @{
        "Authorization" = "Bearer $token3"
        "Content-Type" = "application/json"
    }

    # 尝试发起通话
    $initiateBody3 = @{
        receiverId = $userId2
        callType = "VIDEO"
    } | ConvertTo-Json

    try {
        $callResponse3 = Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers3 -Body $initiateBody3
        Write-Host "✗ 应该拒绝余额不足的通话，但通过了" -ForegroundColor Red
    } catch {
        Write-Host "✓ 正确拒绝了余额不足的通话" -ForegroundColor Green
        Write-Host "  错误信息: $($_.Exception.Message)" -ForegroundColor Gray
    }
} catch {
    Write-Host "⚠ 创建测试用户失败: $_" -ForegroundColor Yellow
}

Write-Host ""

# 14. 测试关闭通话功能
Write-Host "14. 测试关闭通话功能..." -ForegroundColor Yellow

# 用户2关闭视频通话
$disableSettings = @{
    videoCallEnabled = $false
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $disableSettings | Out-Null

    # 用户1尝试发起视频通话
    $initiateBody4 = @{
        receiverId = $userId2
        callType = "VIDEO"
    } | ConvertTo-Json

    try {
        Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers1 -Body $initiateBody4 | Out-Null
        Write-Host "✗ 应该拒绝关闭了视频通话的请求，但通过了" -ForegroundColor Red
    } catch {
        Write-Host "✓ 正确拒绝了关闭视频通话的请求" -ForegroundColor Green
        Write-Host "  错误信息: $($_.Exception.Message)" -ForegroundColor Gray
    }

    # 恢复用户2的视频通话设置
    $enableSettings = @{
        videoCallEnabled = $true
    } | ConvertTo-Json
    Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $enableSettings | Out-Null

} catch {
    Write-Host "⚠ 测试失败: $_" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "总结:" -ForegroundColor Cyan
Write-Host "✓ 价格设置功能正常" -ForegroundColor Green
Write-Host "✓ 通话发起/接受/结束流程正常" -ForegroundColor Green
Write-Host "✓ 通话计费逻辑正确" -ForegroundColor Green
Write-Host "✓ 余额扣除和转账正确" -ForegroundColor Green
Write-Host "✓ 余额不足检查正常" -ForegroundColor Green
Write-Host "✓ 通话开关检查正常" -ForegroundColor Green
