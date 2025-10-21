# 测试修复后的API - RetrofitClient BASE_URL修复验证
# 修复内容：
# 1. RetrofitClient.kt: BASE_URL 从 "http://10.0.2.2:8080/" 改为 "http://10.0.2.2:8080/api/"
# 2. ApiService.java: 将所有 "messages" 路径改为 "message" (与后端 MessageController 一致)

$baseUrl = "http://localhost:8080/api"

Write-Host "================================" -ForegroundColor Cyan
Write-Host "测试修复后的API接口" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 测试1: 推荐用户列表 (之前返回500错误)
Write-Host "1. 测试推荐用户列表 API" -ForegroundColor Yellow
Write-Host "GET $baseUrl/users/recommended?size=4" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/recommended?size=4" -Method Get -ContentType "application/json"
    if ($response.success) {
        Write-Host "✅ 成功! 返回 $($response.data.Count) 个推荐用户" -ForegroundColor Green
        $response.data | ForEach-Object {
            Write-Host "  - $($_.nickname) (ID: $($_.id))" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ 失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

# 测试2: 会话列表 (之前返回500错误)
Write-Host "2. 测试会话列表 API" -ForegroundColor Yellow
Write-Host "GET $baseUrl/message/conversations?userId=23820512" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/message/conversations?userId=23820512" -Method Get -ContentType "application/json"
    if ($response.success) {
        Write-Host "✅ 成功! 返回 $($response.data.Count) 个会话" -ForegroundColor Green
        $response.data | ForEach-Object {
            Write-Host "  - 与用户 $($_.otherUserNickname) (ID: $($_.otherUserId)) 的会话" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ 失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

# 测试3: 知友列表
Write-Host "3. 测试知友列表 API" -ForegroundColor Yellow
Write-Host "GET $baseUrl/users/acquaintances?size=5" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/acquaintances?size=5" -Method Get -ContentType "application/json"
    if ($response.success) {
        Write-Host "✅ 成功! 返回 $($response.data.Count) 个知友" -ForegroundColor Green
    } else {
        Write-Host "❌ 失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

# 测试4: 喜欢列表
Write-Host "4. 测试喜欢列表 API" -ForegroundColor Yellow
Write-Host "GET $baseUrl/users/likes?size=5" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/likes?size=5" -Method Get -ContentType "application/json"
    if ($response.success) {
        Write-Host "✅ 成功! 返回 $($response.data.Count) 个喜欢的用户" -ForegroundColor Green
    } else {
        Write-Host "❌ 失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

# 测试5: 亲密列表
Write-Host "5. 测试亲密列表 API" -ForegroundColor Yellow
Write-Host "GET $baseUrl/users/intimate?size=5" -ForegroundColor Gray
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/intimate?size=5" -Method Get -ContentType "application/json"
    if ($response.success) {
        Write-Host "✅ 成功! 返回 $($response.data.Count) 个亲密用户" -ForegroundColor Green
    } else {
        Write-Host "❌ 失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "================================" -ForegroundColor Cyan
Write-Host "测试完成!" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
