# 测试用户照片API

$baseUrl = "http://localhost:8080/api"
$testUser = @{
    phone = "13800138000"
    code = "123456"
}

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "测试用户照片API" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 登录获取token
Write-Host "1. 登录获取token..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login-with-code?phone=$($testUser.phone)&code=$($testUser.code)" -Method Post
$token = $loginResponse.data.token
$userId = $loginResponse.data.userId
Write-Host "登录成功 - userId: $userId" -ForegroundColor Green
Write-Host ""

# 2. 获取用户相册（应该是空的）
Write-Host "2. 获取用户相册..." -ForegroundColor Yellow
try {
    $photosResponse = Invoke-RestMethod -Uri "$baseUrl/users/$userId/photos" -Method Get -Headers @{
        "Authorization" = "Bearer $token"
    }
    Write-Host "相册照片数量: $($photosResponse.data.Count)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "获取相册失败: $_" -ForegroundColor Red
    Write-Host ""
}

# 3. 测试获取不存在用户的相册
Write-Host "3. 获取不存在用户的相册..." -ForegroundColor Yellow
try {
    $photosResponse = Invoke-RestMethod -Uri "$baseUrl/users/999999/photos" -Method Get
    Write-Host "照片数量: $($photosResponse.data.Count)" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "获取相册失败（预期）: $_" -ForegroundColor Yellow
    Write-Host ""
}

# 4. 测试上传照片（需要实际的图片文件，这里只显示API结构）
Write-Host "4. 上传照片接口测试说明" -ForegroundColor Yellow
Write-Host "上传照片需要使用 multipart/form-data 格式" -ForegroundColor Cyan
Write-Host "接口: POST $baseUrl/users/$userId/photos" -ForegroundColor Cyan
Write-Host "参数: photo (MultipartFile), isAvatar (boolean)" -ForegroundColor Cyan
Write-Host "需要使用工具如 Postman 或 curl 进行测试" -ForegroundColor Cyan
Write-Host ""

# 显示完整的API列表
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "照片API接口列表" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. GET /api/users/{id}/photos" -ForegroundColor White
Write-Host "   获取用户相册" -ForegroundColor Gray
Write-Host ""
Write-Host "2. POST /api/users/{id}/photos" -ForegroundColor White
Write-Host "   上传照片 (需要 Authorization 和 multipart/form-data)" -ForegroundColor Gray
Write-Host ""
Write-Host "3. DELETE /api/users/{id}/photos/{photoId}" -ForegroundColor White
Write-Host "   删除照片 (需要 Authorization)" -ForegroundColor Gray
Write-Host ""
Write-Host "4. PUT /api/users/{id}/photos/{photoId}/avatar" -ForegroundColor White
Write-Host "   设置为头像 (需要 Authorization)" -ForegroundColor Gray
Write-Host ""

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "测试完成" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
