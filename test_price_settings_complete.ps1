# 价格设置功能测试脚本

$baseUrl = "http://localhost:8080/api"
$phone = "18888888888"
$verificationCode = "123456"

Write-Host "=== 价格设置功能测试 ===" -ForegroundColor Cyan
Write-Host ""

# 1. 登录获取Token
Write-Host "1. 登录获取Token..." -ForegroundColor Yellow
$loginBody = @{
    phone = $phone
    verificationCode = $verificationCode
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.data.token
    Write-Host "✓ 登录成功" -ForegroundColor Green
    Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
} catch {
    Write-Host "✗ 登录失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. 获取当前设置
Write-Host "2. 获取当前用户设置..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Get -Headers $headers
    Write-Host "✓ 获取设置成功" -ForegroundColor Green
    Write-Host "  当前设置:" -ForegroundColor Gray
    Write-Host "    语音通话开关: $($settingsResponse.data.voiceCallEnabled)" -ForegroundColor Gray
    Write-Host "    视频通话开关: $($settingsResponse.data.videoCallEnabled)" -ForegroundColor Gray
    Write-Host "    语音通话价格: $($settingsResponse.data.voiceCallPrice) 元/分钟" -ForegroundColor Gray
    Write-Host "    视频通话价格: $($settingsResponse.data.videoCallPrice) 元/分钟" -ForegroundColor Gray
} catch {
    Write-Host "✗ 获取设置失败: $_" -ForegroundColor Red
}

Write-Host ""

# 3. 更新设置 - 开启并设置价格
Write-Host "3. 更新设置(开启通话,设置价格)..." -ForegroundColor Yellow
$updateBody = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $false
    voiceCallPrice = 100.0
    videoCallPrice = 200.0
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $updateBody
    Write-Host "✓ 更新设置成功" -ForegroundColor Green
    Write-Host "  更新后设置:" -ForegroundColor Gray
    Write-Host "    语音通话开关: $($updateResponse.data.voiceCallEnabled)" -ForegroundColor Gray
    Write-Host "    视频通话开关: $($updateResponse.data.videoCallEnabled)" -ForegroundColor Gray
    Write-Host "    语音通话价格: $($updateResponse.data.voiceCallPrice) 元/分钟" -ForegroundColor Gray
    Write-Host "    视频通话价格: $($updateResponse.data.videoCallPrice) 元/分钟" -ForegroundColor Gray
} catch {
    Write-Host "✗ 更新设置失败: $_" -ForegroundColor Red
}

Write-Host ""

# 4. 测试关闭视频通话
Write-Host "4. 测试关闭视频通话..." -ForegroundColor Yellow
$updateBody2 = @{
    videoCallEnabled = $false
} | ConvertTo-Json

try {
    $updateResponse2 = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $updateBody2
    Write-Host "✓ 关闭视频通话成功" -ForegroundColor Green
    Write-Host "  更新后设置:" -ForegroundColor Gray
    Write-Host "    视频通话开关: $($updateResponse2.data.videoCallEnabled)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 更新失败: $_" -ForegroundColor Red
}

Write-Host ""

# 5. 测试价格范围验证(超出范围)
Write-Host "5. 测试价格范围验证(设置为1500元,超出范围)..." -ForegroundColor Yellow
$updateBody3 = @{
    voiceCallPrice = 1500.0
} | ConvertTo-Json

try {
    $updateResponse3 = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $updateBody3
    Write-Host "✗ 应该拒绝超范围价格,但通过了" -ForegroundColor Red
} catch {
    Write-Host "✓ 正确拒绝了超范围价格" -ForegroundColor Green
    Write-Host "  错误信息: $($_.Exception.Message)" -ForegroundColor Gray
}

Write-Host ""

# 6. 恢复默认设置
Write-Host "6. 恢复默认设置(免费)..." -ForegroundColor Yellow
$updateBody4 = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    voiceCallPrice = 0.0
    videoCallPrice = 0.0
} | ConvertTo-Json

try {
    $updateResponse4 = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $updateBody4
    Write-Host "✓ 恢复默认设置成功" -ForegroundColor Green
    Write-Host "  最终设置:" -ForegroundColor Gray
    Write-Host "    语音通话: 开启, 价格: $($updateResponse4.data.voiceCallPrice) 元/分钟(免费)" -ForegroundColor Gray
    Write-Host "    视频通话: 开启, 价格: $($updateResponse4.data.videoCallPrice) 元/分钟(免费)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 恢复设置失败: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Cyan
