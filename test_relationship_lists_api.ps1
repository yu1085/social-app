# 测试关系列表API
$baseUrl = "http://localhost:8080/api"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   测试关系列表API" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. 测试推荐用户列表
Write-Host "1. 测试推荐用户列表 (GET /api/users/recommended?size=4)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/recommended?size=4" -Method Get -ContentType "application/json"
    Write-Host "   ✅ 成功" -ForegroundColor Green
    Write-Host "   返回用户数: $($response.data.Count)" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "      - ID: $($user.id), 昵称: $($user.nickname), 位置: $($user.location)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}

Write-Host ""

# 2. 测试知友列表
Write-Host "2. 测试知友列表 (GET /api/users/acquaintances?page=0&size=5)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/acquaintances?page=0&size=5" -Method Get -ContentType "application/json"
    Write-Host "   ✅ 成功" -ForegroundColor Green
    Write-Host "   返回用户数: $($response.data.Count)" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "      - ID: $($user.id), 昵称: $($user.nickname), 位置: $($user.location)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}

Write-Host ""

# 3. 测试喜欢列表
Write-Host "3. 测试喜欢列表 (GET /api/users/likes?page=0&size=5)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/likes?page=0&size=5" -Method Get -ContentType "application/json"
    Write-Host "   ✅ 成功" -ForegroundColor Green
    Write-Host "   返回用户数: $($response.data.Count)" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "      - ID: $($user.id), 昵称: $($user.nickname), 性别: $($user.gender), 位置: $($user.location)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}

Write-Host ""

# 4. 测试亲密列表
Write-Host "4. 测试亲密列表 (GET /api/users/intimate?page=0&size=5)" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/intimate?page=0&size=5" -Method Get -ContentType "application/json"
    Write-Host "   ✅ 成功" -ForegroundColor Green
    Write-Host "   返回用户数: $($response.data.Count)" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "      - ID: $($user.id), 昵称: $($user.nickname), 位置: $($user.location)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ 失败: $_" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   测试完成!" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
