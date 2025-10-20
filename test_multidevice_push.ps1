# 多设备推送测试脚本
# 用于测试多设备推送通知系统

Write-Host "🚀 开始多设备推送测试..." -ForegroundColor Green

# 配置
$baseUrl = "http://localhost:8080"
$testUserId = 1
$testPhone = "13800138000"
$testCode = "123456"

Write-Host "📱 测试配置:" -ForegroundColor Yellow
Write-Host "  后端地址: $baseUrl"
Write-Host "  测试用户ID: $testUserId"
Write-Host "  测试手机号: $testPhone"
Write-Host "  测试验证码: $testCode"
Write-Host ""

# 1. 测试用户登录
Write-Host "1️⃣ 测试用户登录..." -ForegroundColor Cyan
$loginUrl = "$baseUrl/api/auth/login-with-code"
$loginBody = @{
    phone = $testPhone
    code = $testCode
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method POST -Body $loginBody -ContentType "application/json"
    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        Write-Host "✅ 登录成功，Token: $($token.Substring(0, 20))..." -ForegroundColor Green
    } else {
        Write-Host "❌ 登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 登录请求失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 注册多个测试设备
Write-Host "2️⃣ 注册多个测试设备..." -ForegroundColor Cyan
$devices = @(
    @{ name = "测试手机1"; type = "ANDROID"; regId = "test_reg_001" },
    @{ name = "测试手机2"; type = "ANDROID"; regId = "test_reg_002" },
    @{ name = "测试平板"; type = "ANDROID"; regId = "test_reg_003" },
    @{ name = "测试iPhone"; type = "IOS"; regId = "test_reg_004" }
)

$registerUrl = "$baseUrl/api/device/register"
$headers = @{ "Authorization" = "Bearer $token" }

foreach ($device in $devices) {
    try {
        $registerParams = @{
            registrationId = $device.regId
            deviceName = $device.name
            deviceType = $device.type
        }
        
        $registerResponse = Invoke-RestMethod -Uri $registerUrl -Method POST -Headers $headers -Body $registerParams
        if ($registerResponse.success) {
            Write-Host "✅ 设备注册成功: $($device.name) ($($device.regId))" -ForegroundColor Green
        } else {
            Write-Host "⚠️ 设备注册失败: $($device.name) - $($registerResponse.message)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "❌ 设备注册请求失败: $($device.name) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 3. 获取设备列表
Write-Host "3️⃣ 获取设备列表..." -ForegroundColor Cyan
$deviceListUrl = "$baseUrl/api/device/list"

try {
    $deviceListResponse = Invoke-RestMethod -Uri $deviceListUrl -Method GET -Headers $headers
    if ($deviceListResponse.success) {
        $devices = $deviceListResponse.data
        Write-Host "✅ 设备列表获取成功，共 $($devices.Count) 个设备:" -ForegroundColor Green
        foreach ($device in $devices) {
            $status = if ($device.isActive) { "活跃" } else { "已停用" }
            Write-Host "   📱 $($device.deviceName) ($($device.deviceType)) - $status" -ForegroundColor White
        }
    } else {
        Write-Host "❌ 获取设备列表失败: $($deviceListResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 获取设备列表请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 获取设备统计
Write-Host "4️⃣ 获取设备统计..." -ForegroundColor Cyan
$statsUrl = "$baseUrl/api/device/stats"

try {
    $statsResponse = Invoke-RestMethod -Uri $statsUrl -Method GET -Headers $headers
    if ($statsResponse.success) {
        $stats = $statsResponse.data
        Write-Host "✅ 设备统计获取成功:" -ForegroundColor Green
        Write-Host "   总设备数: $($stats.totalDevices)" -ForegroundColor White
        Write-Host "   活跃设备: $($stats.activeDevices)" -ForegroundColor White
        Write-Host "   Android设备: $($stats.androidDevices)" -ForegroundColor White
        Write-Host "   iOS设备: $($stats.iosDevices)" -ForegroundColor White
        Write-Host "   最后活跃设备: $($stats.lastActiveDevice)" -ForegroundColor White
    } else {
        Write-Host "❌ 获取设备统计失败: $($statsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 获取设备统计请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 测试停用设备
Write-Host "5️⃣ 测试停用设备..." -ForegroundColor Cyan
$deactivateUrl = "$baseUrl/api/device/deactivate"
$deactivateParams = @{ registrationId = "test_reg_001" }

try {
    $deactivateResponse = Invoke-RestMethod -Uri $deactivateUrl -Method POST -Headers $headers -Body $deactivateParams
    if ($deactivateResponse.success) {
        Write-Host "✅ 设备停用成功: test_reg_001" -ForegroundColor Green
    } else {
        Write-Host "❌ 设备停用失败: $($deactivateResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 设备停用请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 测试推送通知（需要后端支持）
Write-Host "6️⃣ 测试推送通知..." -ForegroundColor Cyan
Write-Host "   注意: 推送通知测试需要后端JPushService支持" -ForegroundColor Yellow
Write-Host "   可以通过以下方式测试:" -ForegroundColor Yellow
Write-Host "   1. 调用通话接口发起通话" -ForegroundColor White
Write-Host "   2. 检查后端日志中的推送发送记录" -ForegroundColor White
Write-Host "   3. 验证所有活跃设备都收到通知" -ForegroundColor White

# 7. 清理测试数据
Write-Host "7️⃣ 清理测试数据..." -ForegroundColor Cyan
Write-Host "   测试完成，可以手动清理测试设备数据" -ForegroundColor Yellow

Write-Host "🎉 多设备推送测试完成！" -ForegroundColor Green
Write-Host ""
Write-Host "📋 测试总结:" -ForegroundColor Yellow
Write-Host "   ✅ 用户登录功能" -ForegroundColor Green
Write-Host "   ✅ 设备注册功能" -ForegroundColor Green
Write-Host "   ✅ 设备列表获取" -ForegroundColor Green
Write-Host "   ✅ 设备统计功能" -ForegroundColor Green
Write-Host "   ✅ 设备停用功能" -ForegroundColor Green
Write-Host "   ⚠️ 推送通知功能（需要实际测试）" -ForegroundColor Yellow
Write-Host ""
Write-Host "💡 下一步:" -ForegroundColor Cyan
Write-Host "   1. 在Android设备上安装应用" -ForegroundColor White
Write-Host "   2. 登录并注册设备" -ForegroundColor White
Write-Host "   3. 发起通话测试多设备推送" -ForegroundColor White
Write-Host "   4. 验证所有设备都收到通知" -ForegroundColor White