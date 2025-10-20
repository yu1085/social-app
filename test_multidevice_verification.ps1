# 多设备推送通知系统验证脚本
Write-Host "🧪 开始验证多设备推送通知系统..." -ForegroundColor Green

# 1. 测试后端服务健康状态
Write-Host "`n1. 测试后端服务健康状态..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/health" -Method GET
    Write-Host "✅ 后端服务正常运行" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端服务无法访问: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 测试用户登录
Write-Host "`n2. 测试用户登录..." -ForegroundColor Yellow
$phone = "19887654321"
$code = "123456"

try {
    # 发送验证码
    $sendCodeResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/send-code?phone=$phone" -Method POST
    Write-Host "✅ 验证码发送成功: $($sendCodeResponse.message)" -ForegroundColor Green
    
    # 登录
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login-with-code?phone=$phone&code=$code" -Method POST
    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        Write-Host "✅ 用户登录成功" -ForegroundColor Green
        Write-Host "   Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "❌ 用户登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 登录过程失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 测试设备注册API (新多设备API)
Write-Host "`n3. 测试设备注册API..." -ForegroundColor Yellow
$registrationId = "test_device_$(Get-Random)"
$deviceName = "Test Device $(Get-Date -Format 'HH:mm:ss')"
$deviceType = "ANDROID"

try {
    $headers = @{ "Authorization" = "Bearer $token" }
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/device/register?registrationId=$registrationId&deviceName=$deviceName&deviceType=$deviceType" -Method POST -Headers $headers
    if ($registerResponse.success) {
        Write-Host "✅ 设备注册成功 (多设备API)" -ForegroundColor Green
        Write-Host "   设备ID: $registrationId" -ForegroundColor Gray
        Write-Host "   设备名称: $deviceName" -ForegroundColor Gray
    } else {
        Write-Host "❌ 设备注册失败: $($registerResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 设备注册API调用失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   这可能是多设备API未正确部署的问题" -ForegroundColor Yellow
}

# 4. 测试设备列表API
Write-Host "`n4. 测试设备列表API..." -ForegroundColor Yellow
try {
    $deviceListResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/device/list" -Method GET -Headers $headers
    if ($deviceListResponse.success) {
        $deviceCount = $deviceListResponse.data.Count
        Write-Host "✅ 设备列表获取成功，共 $deviceCount 个设备" -ForegroundColor Green
        foreach ($device in $deviceListResponse.data) {
            Write-Host "   - $($device.deviceName) ($($device.deviceType)) - $($device.registrationId.Substring(0, 10))..." -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ 设备列表获取失败: $($deviceListResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 设备列表API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 测试设备统计API
Write-Host "`n5. 测试设备统计API..." -ForegroundColor Yellow
try {
    $statsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/device/stats" -Method GET -Headers $headers
    if ($statsResponse.success) {
        $stats = $statsResponse.data
        Write-Host "✅ 设备统计获取成功:" -ForegroundColor Green
        Write-Host "   总设备数: $($stats.totalDevices)" -ForegroundColor Gray
        Write-Host "   活跃设备: $($stats.activeDevices)" -ForegroundColor Gray
        Write-Host "   Android设备: $($stats.androidDevices)" -ForegroundColor Gray
        Write-Host "   iOS设备: $($stats.iosDevices)" -ForegroundColor Gray
    } else {
        Write-Host "❌ 设备统计获取失败: $($statsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 设备统计API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 测试推送通知API
Write-Host "`n6. 测试推送通知API..." -ForegroundColor Yellow
try {
    $pushResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/test-push" -Method POST -Headers $headers
    if ($pushResponse.success) {
        Write-Host "✅ 推送通知发送成功" -ForegroundColor Green
        Write-Host "   消息: $($pushResponse.message)" -ForegroundColor Gray
    } else {
        Write-Host "❌ 推送通知发送失败: $($pushResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 推送通知API调用失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎯 验证完成！" -ForegroundColor Green
Write-Host "如果看到多个 ✅ 表示多设备推送系统工作正常" -ForegroundColor Cyan
Write-Host "如果看到多个 ❌ 表示需要检查后端配置" -ForegroundColor Yellow
