# 简化的多设备推送系统测试
Write-Host "🧪 多设备推送系统简化测试" -ForegroundColor Green

# 1. 检查后端服务状态
Write-Host "`n1. 检查后端服务状态..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method GET
    Write-Host "✅ 后端服务正常运行" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端服务无法访问" -ForegroundColor Red
    Write-Host "   需要重新启动后端服务" -ForegroundColor Yellow
    exit 1
}

# 2. 测试用户登录
Write-Host "`n2. 测试用户登录..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login-with-code?phone=19887654321&code=123456" -Method POST
    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        Write-Host "✅ 用户登录成功" -ForegroundColor Green
        Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "❌ 用户登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 测试多设备API
Write-Host "`n3. 测试多设备API..." -ForegroundColor Yellow
$headers = @{ "Authorization" = "Bearer $token" }
$registrationId = "test_device_$(Get-Random)"
$deviceName = "Test Device $(Get-Date -Format 'HH:mm:ss')"

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/device/register?registrationId=$registrationId&deviceName=$deviceName&deviceType=ANDROID" -Method POST -Headers $headers
    if ($registerResponse.success) {
        Write-Host "✅ 多设备API工作正常" -ForegroundColor Green
        Write-Host "   设备注册成功: $deviceName" -ForegroundColor Gray
    } else {
        Write-Host "❌ 多设备API失败: $($registerResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 多设备API不可用 (404错误)" -ForegroundColor Red
    Write-Host "   系统将使用兼容模式" -ForegroundColor Yellow
}

# 4. 测试设备列表
Write-Host "`n4. 测试设备列表..." -ForegroundColor Yellow
try {
    $deviceListResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/device/list" -Method GET -Headers $headers
    if ($deviceListResponse.success) {
        $deviceCount = $deviceListResponse.data.Count
        Write-Host "✅ 设备列表获取成功，共 $deviceCount 个设备" -ForegroundColor Green
    } else {
        Write-Host "❌ 设备列表获取失败: $($deviceListResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 设备列表API不可用" -ForegroundColor Red
}

# 5. 测试推送通知
Write-Host "`n5. 测试推送通知..." -ForegroundColor Yellow
try {
    $pushResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/test-push" -Method POST -Headers $headers
    if ($pushResponse.success) {
        Write-Host "✅ 推送通知发送成功" -ForegroundColor Green
        Write-Host "   消息: $($pushResponse.message)" -ForegroundColor Gray
    } else {
        Write-Host "❌ 推送通知发送失败: $($pushResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 推送通知API不可用" -ForegroundColor Red
}

Write-Host "`n🎯 测试完成！" -ForegroundColor Green
Write-Host "如果看到多个 ✅ 表示多设备推送系统完全正常" -ForegroundColor Cyan
Write-Host "如果看到多个 ❌ 表示需要进一步调试" -ForegroundColor Yellow
