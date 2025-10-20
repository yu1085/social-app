# 个人资料API测试脚本
# 测试个人资料相关的所有API接口

$baseUrl = "http://localhost:8080"
$testPhone = "13800138000"
$testCode = "123456"

Write-Host "=== 个人资料API测试开始 ===" -ForegroundColor Green

# 1. 发送验证码
Write-Host "`n1. 发送验证码..." -ForegroundColor Yellow
$sendCodeResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/send-code" -Method POST -Body @{phone=$testPhone} -ContentType "application/json"
Write-Host "发送验证码结果: $($sendCodeResponse.message)"

# 2. 登录获取Token
Write-Host "`n2. 登录获取Token..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body @{phone=$testPhone; code=$testCode} -ContentType "application/json"
$token = $loginResponse.data.token
Write-Host "登录成功，Token: $($token.Substring(0, 20))..."

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 3. 获取用户完整资料
Write-Host "`n3. 获取用户完整资料..." -ForegroundColor Yellow
try {
    $profileResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile" -Method GET -Headers $headers
    Write-Host "获取资料成功:"
    Write-Host "  用户ID: $($profileResponse.data.user.id)"
    Write-Host "  用户名: $($profileResponse.data.user.username)"
    Write-Host "  昵称: $($profileResponse.data.user.nickname)"
    Write-Host "  VIP状态: $($profileResponse.data.vipInfo.isVip)"
    Write-Host "  钱包余额: $($profileResponse.data.wallet.balance)"
} catch {
    Write-Host "获取资料失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 更新用户资料
Write-Host "`n4. 更新用户资料..." -ForegroundColor Yellow
$updateData = @{
    nickname = "测试用户" + (Get-Date -Format "HHmmss")
    location = "北京市"
    signature = "这是一个测试用户的签名"
    height = 175
    weight = 70
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile" -Method PUT -Headers $headers -Body $updateData
    Write-Host "更新资料成功:"
    Write-Host "  新昵称: $($updateResponse.data.nickname)"
    Write-Host "  新位置: $($updateResponse.data.location)"
    Write-Host "  新签名: $($updateResponse.data.signature)"
} catch {
    Write-Host "更新资料失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 获取用户设置
Write-Host "`n5. 获取用户设置..." -ForegroundColor Yellow
try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/settings" -Method GET -Headers $headers
    Write-Host "获取设置成功:"
    Write-Host "  语音通话: $($settingsResponse.data.voiceCallEnabled)"
    Write-Host "  视频通话: $($settingsResponse.data.videoCallEnabled)"
    Write-Host "  私信收费: $($settingsResponse.data.messageChargeEnabled)"
} catch {
    Write-Host "获取设置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 更新用户设置
Write-Host "`n6. 更新用户设置..." -ForegroundColor Yellow
$settingsData = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $true
    voiceCallPrice = 1.5
    videoCallPrice = 3.0
    messagePrice = 0.5
} | ConvertTo-Json

try {
    $updateSettingsResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/settings" -Method PUT -Headers $headers -Body $settingsData
    Write-Host "更新设置成功:"
    Write-Host "  语音通话价格: $($updateSettingsResponse.data.voiceCallPrice)"
    Write-Host "  视频通话价格: $($updateSettingsResponse.data.videoCallPrice)"
    Write-Host "  私信价格: $($updateSettingsResponse.data.messagePrice)"
} catch {
    Write-Host "更新设置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. 获取钱包信息
Write-Host "`n7. 获取钱包信息..." -ForegroundColor Yellow
try {
    $walletResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/wallet" -Method GET -Headers $headers
    Write-Host "获取钱包成功:"
    Write-Host "  余额: $($walletResponse.data.balance)"
    Write-Host "  总充值: $($walletResponse.data.totalRecharge)"
    Write-Host "  总消费: $($walletResponse.data.totalConsume)"
    Write-Host "  交易次数: $($walletResponse.data.transactionCount)"
} catch {
    Write-Host "获取钱包失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. 获取VIP信息
Write-Host "`n8. 获取VIP信息..." -ForegroundColor Yellow
try {
    $vipResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/vip" -Method GET -Headers $headers
    Write-Host "获取VIP信息成功:"
    Write-Host "  是否VIP: $($vipResponse.data.isVip)"
    Write-Host "  VIP等级: $($vipResponse.data.vipLevel)"
    Write-Host "  VIP等级名称: $($vipResponse.data.vipLevelName)"
    Write-Host "  剩余天数: $($vipResponse.data.remainingDays)"
    Write-Host "  VIP权益: $($vipResponse.data.vipBenefits)"
} catch {
    Write-Host "获取VIP信息失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 9. 获取用户统计信息
Write-Host "`n9. 获取用户统计信息..." -ForegroundColor Yellow
try {
    $statsResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/stats" -Method GET -Headers $headers
    Write-Host "获取统计信息成功:"
    Write-Host "  注册天数: $($statsResponse.data.registerDays)"
    Write-Host "  是否在线: $($statsResponse.data.isOnline)"
    Write-Host "  是否认证: $($statsResponse.data.isVerified)"
    Write-Host "  财富等级: $($statsResponse.data.wealthLevel)"
} catch {
    Write-Host "获取统计信息失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 个人资料API测试完成 ===" -ForegroundColor Green
