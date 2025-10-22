# 测试喜欢功能完整流程
# 测试用户: 23820512 (小雅), 23820513 (小雨)

$baseUrl = "http://localhost:8080/api"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试喜欢功能完整流程" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 用户登录获取Token
Write-Host "1. 用户23820512 (小雅) 登录..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login-with-code?phone=13800138001&code=123456" -Method POST
$token = $loginResponse.data.token
$userId = $loginResponse.data.user.id
Write-Host "   ✓ 登录成功 - userId: $userId, token: $($token.Substring(0, 20))..." -ForegroundColor Green
Write-Host ""

# 2. 查看用户23820513的详情
Write-Host "2. 查看用户23820513 (小雨) 的详情..." -ForegroundColor Yellow
$targetUserId = 23820513
$userDetailResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId" -Method GET
Write-Host "   ✓ 用户详情:" -ForegroundColor Green
Write-Host "     - ID: $($userDetailResponse.data.id)" -ForegroundColor White
Write-Host "     - 用户名: $($userDetailResponse.data.username)" -ForegroundColor White
Write-Host "     - 昵称: $($userDetailResponse.data.nickname)" -ForegroundColor White
Write-Host ""

# 3. 检查是否已喜欢
Write-Host "3. 检查是否已喜欢用户23820513..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
}
$isLikedResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
$isLiked = $isLikedResponse.data
Write-Host "   ✓ 喜欢状态: $isLiked" -ForegroundColor $(if ($isLiked) { "Red" } else { "Gray" })
Write-Host ""

# 4. 如果已喜欢，先取消喜欢
if ($isLiked) {
    Write-Host "4. 已喜欢，先取消喜欢..." -ForegroundColor Yellow
    $removeLikeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method DELETE -Headers $headers
    Write-Host "   ✓ $($removeLikeResponse.message)" -ForegroundColor Green
    Write-Host ""

    Write-Host "5. 再次检查喜欢状态..." -ForegroundColor Yellow
    $isLikedResponse2 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
    Write-Host "   ✓ 喜欢状态: $($isLikedResponse2.data)" -ForegroundColor Gray
    Write-Host ""
}

# 5. 添加喜欢
Write-Host "$(if ($isLiked) { "6" } else { "4" }). 添加喜欢用户23820513..." -ForegroundColor Yellow
$addLikeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method POST -Headers $headers
Write-Host "   ✓ $($addLikeResponse.message)" -ForegroundColor Green
Write-Host ""

# 6. 验证喜欢状态
Write-Host "$(if ($isLiked) { "7" } else { "5" }). 验证喜欢状态..." -ForegroundColor Yellow
$isLikedResponse3 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
Write-Host "   ✓ 喜欢状态: $($isLikedResponse3.data)" -ForegroundColor Red
Write-Host ""

# 7. 查看喜欢列表
Write-Host "$(if ($isLiked) { "8" } else { "6" }). 查看当前用户的喜欢列表..." -ForegroundColor Yellow
$likesListResponse = Invoke-RestMethod -Uri "$baseUrl/users/likes?size=10" -Method GET -Headers $headers
Write-Host "   ✓ 喜欢列表 (共 $($likesListResponse.data.Count) 个用户):" -ForegroundColor Green
foreach ($user in $likesListResponse.data) {
    Write-Host "     - ID: $($user.id), 昵称: $($user.nickname), 位置: $($user.location)" -ForegroundColor White
}
Write-Host ""

# 8. 测试取消喜欢（模拟再次点击）
Write-Host "$(if ($isLiked) { "9" } else { "7" }). 测试取消喜欢..." -ForegroundColor Yellow
$removeLikeResponse2 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method DELETE -Headers $headers
Write-Host "   ✓ $($removeLikeResponse2.message)" -ForegroundColor Green
Write-Host ""

# 9. 最终验证
Write-Host "$(if ($isLiked) { "10" } else { "8" }). 最终验证喜欢状态..." -ForegroundColor Yellow
$isLikedResponse4 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
Write-Host "   ✓ 喜欢状态: $($isLikedResponse4.data)" -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "功能总结:" -ForegroundColor Yellow
Write-Host "  1. ✓ 用户登录获取Token" -ForegroundColor Green
Write-Host "  2. ✓ 查看用户详情" -ForegroundColor Green
Write-Host "  3. ✓ 检查喜欢状态" -ForegroundColor Green
Write-Host "  4. ✓ 添加喜欢" -ForegroundColor Green
Write-Host "  5. ✓ 查看喜欢列表" -ForegroundColor Green
Write-Host "  6. ✓ 取消喜欢" -ForegroundColor Green
Write-Host ""
Write-Host "Android端使用指南:" -ForegroundColor Yellow
Write-Host "  1. 从首页/用户列表进入用户详情页" -ForegroundColor White
Write-Host "  2. 如果未喜欢该用户，会显示喜欢按钮" -ForegroundColor White
Write-Host "  3. 点击喜欢按钮后，该按钮会消失" -ForegroundColor White
Write-Host "  4. 用户被加入到喜欢列表中" -ForegroundColor White
Write-Host "  5. 再次进入该用户详情页，喜欢按钮不会显示" -ForegroundColor White
Write-Host ""

