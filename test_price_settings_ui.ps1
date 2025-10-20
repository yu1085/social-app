# 价格设置UI测试脚本
# 测试获取和更新用户设置

$baseUrl = "http://localhost:8080/api"

Write-Host "=== 价格设置UI测试 ===" -ForegroundColor Cyan
Write-Host ""

# 1. 登录获取token
Write-Host "1. 用户登录..." -ForegroundColor Yellow
$loginBody = @{
    phone = "18888888888"
    verificationCode = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.data.token
    $userId = $loginResponse.data.userId
    Write-Host "✓ 登录成功 - userId: $userId" -ForegroundColor Green
} catch {
    Write-Host "✗ 登录失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 2. 获取当前设置
Write-Host "2. 获取当前价格设置..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Get -Headers $headers
    Write-Host "✓ 获取设置成功" -ForegroundColor Green
    Write-Host "  语音通话: 开关=$($settingsResponse.data.voiceCallEnabled), 价格=$($settingsResponse.data.voiceCallPrice)" -ForegroundColor Gray
    Write-Host "  视频通话: 开关=$($settingsResponse.data.videoCallEnabled), 价格=$($settingsResponse.data.videoCallPrice)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 获取设置失败: $_" -ForegroundColor Red
}

Write-Host ""

# 3. 测试更新设置 - 打开视频通话并设置价格
Write-Host "3. 更新设置 - 打开视频通话，设置价格200元/分钟..." -ForegroundColor Yellow
$updateBody = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    voiceCallPrice = 100.0
    videoCallPrice = 200.0
    messageChargeEnabled = $false
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $updateBody
    Write-Host "✓ 更新设置成功" -ForegroundColor Green
    Write-Host "  语音通话: 开关=$($updateResponse.data.voiceCallEnabled), 价格=$($updateResponse.data.voiceCallPrice)" -ForegroundColor Gray
    Write-Host "  视频通话: 开关=$($updateResponse.data.videoCallEnabled), 价格=$($updateResponse.data.videoCallPrice)" -ForegroundColor Gray
} catch {
    Write-Host "✗ 更新设置失败: $_" -ForegroundColor Red
    Write-Host "  错误详情: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# 4. 再次获取设置验证更新
Write-Host "4. 验证设置是否已更新..." -ForegroundColor Yellow
try {
    $verifyResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Get -Headers $headers
    Write-Host "✓ 验证成功" -ForegroundColor Green
    Write-Host "  语音通话: 开关=$($verifyResponse.data.voiceCallEnabled), 价格=$($verifyResponse.data.voiceCallPrice)" -ForegroundColor Gray
    Write-Host "  视频通话: 开关=$($verifyResponse.data.videoCallEnabled), 价格=$($verifyResponse.data.videoCallPrice)" -ForegroundColor Gray

    if ($verifyResponse.data.videoCallPrice -eq 200.0) {
        Write-Host "  ✓ 价格更新正确" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 价格更新不正确" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 验证失败: $_" -ForegroundColor Red
}

Write-Host ""

# 5. 测试关闭视频通话
Write-Host "5. 测试关闭视频通话..." -ForegroundColor Yellow
$closeBody = @{
    voiceCallEnabled = $true
    videoCallEnabled = $false
    voiceCallPrice = 100.0
    videoCallPrice = 200.0
} | ConvertTo-Json

try {
    $closeResponse = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers -Body $closeBody
    Write-Host "✓ 关闭视频通话成功" -ForegroundColor Green
    Write-Host "  视频通话开关: $($closeResponse.data.videoCallEnabled)" -ForegroundColor Gray

    if ($closeResponse.data.videoCallEnabled -eq $false) {
        Write-Host "  ✓ 开关状态正确" -ForegroundColor Green
    } else {
        Write-Host "  ✗ 开关状态不正确" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 关闭失败: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== 测试完成 ===" -ForegroundColor Cyan
