# 用户设置API测试脚本
# 测试后端ProfileController的用户设置相关接口

Write-Host "=== 用户设置API测试 ===" -ForegroundColor Green

# 设置测试参数
$baseUrl = "http://localhost:8080/api"
$testToken = "your-jwt-token-here"  # 需要替换为真实的JWT token

Write-Host "`n1. 测试获取用户设置 (GET /api/profile/settings)" -ForegroundColor Yellow
$getSettingsUrl = "$baseUrl/profile/settings"
$getHeaders = @{
    "Authorization" = "Bearer $testToken"
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri $getSettingsUrl -Method GET -Headers $getHeaders
    Write-Host "✓ 获取用户设置成功" -ForegroundColor Green
    Write-Host "响应数据: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "✗ 获取用户设置失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. 测试更新用户设置 (PUT /api/profile/settings)" -ForegroundColor Yellow
$updateSettingsUrl = "$baseUrl/profile/settings"
$updateHeaders = @{
    "Authorization" = "Bearer $testToken"
    "Content-Type" = "application/json"
}

# 测试数据
$testSettings = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $false
    voiceCallPrice = 100.0
    videoCallPrice = 150.0
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $updateSettingsUrl -Method PUT -Headers $updateHeaders -Body $testSettings
    Write-Host "✓ 更新用户设置成功" -ForegroundColor Green
    Write-Host "响应数据: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "✗ 更新用户设置失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n3. 测试获取完整用户资料 (GET /api/profile)" -ForegroundColor Yellow
$getProfileUrl = "$baseUrl/profile"
$profileHeaders = @{
    "Authorization" = "Bearer $testToken"
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri $getProfileUrl -Method GET -Headers $profileHeaders
    Write-Host "✓ 获取用户资料成功" -ForegroundColor Green
    Write-Host "响应数据: $($response | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "✗ 获取用户资料失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n4. 测试不同价格设置" -ForegroundColor Yellow

# 测试免费设置
$freeSettings = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $false
    voiceCallPrice = 0.0
    videoCallPrice = 0.0
    messagePrice = 0.0
} | ConvertTo-Json

Write-Host "设置免费通话..."
try {
    $response = Invoke-RestMethod -Uri $updateSettingsUrl -Method PUT -Headers $updateHeaders -Body $freeSettings
    Write-Host "✓ 免费设置更新成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 免费设置更新失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试收费设置
$paidSettings = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $true
    voiceCallPrice = 200.0
    videoCallPrice = 300.0
    messagePrice = 10.0
} | ConvertTo-Json

Write-Host "设置收费通话..."
try {
    $response = Invoke-RestMethod -Uri $updateSettingsUrl -Method PUT -Headers $updateHeaders -Body $paidSettings
    Write-Host "✓ 收费设置更新成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 收费设置更新失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n5. 测试关闭所有功能" -ForegroundColor Yellow
$disabledSettings = @{
    voiceCallEnabled = $false
    videoCallEnabled = $false
    messageChargeEnabled = $false
    voiceCallPrice = 0.0
    videoCallPrice = 0.0
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $updateSettingsUrl -Method PUT -Headers $updateHeaders -Body $disabledSettings
    Write-Host "✓ 关闭所有功能成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 关闭所有功能失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== API测试完成 ===" -ForegroundColor Green
Write-Host "`n注意事项:"
Write-Host "1. 确保后端服务正在运行 (http://localhost:8080)"
Write-Host "2. 替换testToken为有效的JWT token"
Write-Host "3. 确保用户已登录并具有有效的认证信息"
Write-Host "4. 检查数据库中的user_settings表是否正确更新"










