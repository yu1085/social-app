# 测试用户关系API
# 知友、喜欢、亲密功能测试脚本

$baseUrl = "http://localhost:8080/api"
$token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMjQ5MTcyOSIsImV4cCI6MTc2MjQwNDgyM30.dummy" # 需要替换为真实token

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "用户关系API测试脚本" -ForegroundColor Cyan
Write-Host "================================================`n" -ForegroundColor Cyan

# 测试1: 添加喜欢
Write-Host "测试1: 添加喜欢 (POST /users/{targetUserId}/like)" -ForegroundColor Yellow
$targetUserId = 23820512
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method POST -Headers @{
    "Authorization" = $token
    "Content-Type" = "application/json"
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 添加喜欢成功: $($response.message)" -ForegroundColor Green
} else {
    Write-Host "❌ 添加喜欢失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试2: 检查是否已喜欢
Write-Host "测试2: 检查是否已喜欢 (GET /users/{targetUserId}/is-liked)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 检查成功 - 已喜欢: $($response.data)" -ForegroundColor Green
} else {
    Write-Host "❌ 检查失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试3: 获取喜欢列表
Write-Host "测试3: 获取喜欢列表 (GET /users/likes)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/likes?size=5" -Method GET -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 获取喜欢列表成功 - 共 $($response.data.Count) 个用户" -ForegroundColor Green
    $response.data | ForEach-Object {
        Write-Host "  - 用户ID: $($_.id), 昵称: $($_.nickname)" -ForegroundColor Gray
    }
} else {
    Write-Host "❌ 获取喜欢列表失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试4: 添加知友
Write-Host "测试4: 添加知友 (POST /users/{targetUserId}/friend)" -ForegroundColor Yellow
$targetUserId2 = 23820513
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId2/friend" -Method POST -Headers @{
    "Authorization" = $token
    "Content-Type" = "application/json"
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 添加知友成功: $($response.message)" -ForegroundColor Green
} else {
    Write-Host "❌ 添加知友失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试5: 检查是否是知友
Write-Host "测试5: 检查是否是知友 (GET /users/{targetUserId}/is-friend)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId2/is-friend" -Method GET -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 检查成功 - 是知友: $($response.data)" -ForegroundColor Green
} else {
    Write-Host "❌ 检查失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试6: 获取知友列表
Write-Host "测试6: 获取知友列表 (GET /users/acquaintances)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/acquaintances?size=5" -Method GET -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 获取知友列表成功 - 共 $($response.data.Count) 个用户" -ForegroundColor Green
    $response.data | ForEach-Object {
        Write-Host "  - 用户ID: $($_.id), 昵称: $($_.nickname)" -ForegroundColor Gray
    }
} else {
    Write-Host "❌ 获取知友列表失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试7: 获取亲密列表
Write-Host "测试7: 获取亲密列表 (GET /users/intimate)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/intimate?size=5" -Method GET -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 获取亲密列表成功 - 共 $($response.data.Count) 个用户" -ForegroundColor Green
    $response.data | ForEach-Object {
        Write-Host "  - 用户ID: $($_.id), 昵称: $($_.nickname)" -ForegroundColor Gray
    }
} else {
    Write-Host "❌ 获取亲密列表失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试8: 取消喜欢
Write-Host "测试8: 取消喜欢 (DELETE /users/{targetUserId}/like)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method DELETE -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 取消喜欢成功: $($response.message)" -ForegroundColor Green
} else {
    Write-Host "❌ 取消喜欢失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

# 测试9: 删除知友
Write-Host "测试9: 删除知友 (DELETE /users/{targetUserId}/friend)" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId2/friend" -Method DELETE -Headers @{
    "Authorization" = $token
} -ErrorAction SilentlyContinue

if ($response.success) {
    Write-Host "✅ 删除知友成功: $($response.message)" -ForegroundColor Green
} else {
    Write-Host "❌ 删除知友失败: $($response.message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "所有测试完成！" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
