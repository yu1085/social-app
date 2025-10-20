# 批量上传照片到用户相册
param(
    [string]$ImagePath = "C:\Users\Administrator\Downloads",
    [string]$BaseUrl = "http://localhost:8080/api",
    [string]$Phone = "13800138000",
    [string]$Code = "123456"
)

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "批量上传用户照片工具" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 检查图片文件
Write-Host "正在检查图片文件..." -ForegroundColor Yellow
$imageFiles = Get-ChildItem -Path $ImagePath -Include *.jpg,*.jpeg,*.png,*.gif -Recurse -File | Select-Object -First 5
if ($imageFiles.Count -eq 0) {
    Write-Host "错误: 在 $ImagePath 中没有找到图片文件" -ForegroundColor Red
    Write-Host "支持的格式: .jpg, .jpeg, .png, .gif" -ForegroundColor Yellow
    exit 1
}

Write-Host "找到 $($imageFiles.Count) 个图片文件:" -ForegroundColor Green
foreach ($file in $imageFiles) {
    $sizeKB = [math]::Round($file.Length / 1KB, 2)
    Write-Host "  - $($file.Name) ($sizeKB KB)" -ForegroundColor Gray
}
Write-Host ""

# 2. 发送验证码
Write-Host "1. 发送验证码..." -ForegroundColor Yellow
try {
    $sendCodeResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/send-code?phone=$Phone" -Method Post
    if ($sendCodeResponse.success) {
        Write-Host "验证码已发送: $($sendCodeResponse.message)" -ForegroundColor Green
    } else {
        Write-Host "发送验证码失败: $($sendCodeResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "发送验证码失败: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. 登录获取token
Write-Host "2. 登录获取token..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/login-with-code?phone=$Phone&code=$Code" -Method Post
    if (-not $loginResponse.success) {
        Write-Host "登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }

    $token = $loginResponse.data.token
    $userId = $loginResponse.data.userId
    Write-Host "登录成功 - userId: $userId" -ForegroundColor Green
} catch {
    Write-Host "登录失败: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 4. 上传照片
Write-Host "3. 开始上传照片..." -ForegroundColor Yellow
Write-Host ""

$uploadCount = 0
$failCount = 0
$firstPhoto = $true

foreach ($file in $imageFiles) {
    Write-Host "正在上传: $($file.Name)" -ForegroundColor Cyan

    try {
        # 准备multipart/form-data请求
        $boundary = [System.Guid]::NewGuid().ToString()
        $LF = "`r`n"

        # 读取文件内容
        $fileBytes = [System.IO.File]::ReadAllBytes($file.FullName)
        $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

        # 构建multipart body
        $bodyLines = @(
            "--$boundary",
            "Content-Disposition: form-data; name=`"photo`"; filename=`"$($file.Name)`"",
            "Content-Type: image/jpeg",
            "",
            $fileEnc,
            "--$boundary--"
        ) -join $LF

        # 设置是否为头像(第一张设为头像)
        $isAvatar = if ($firstPhoto) { "true" } else { "false" }

        # 发送POST请求
        $uri = "$BaseUrl/users/$userId/photos?isAvatar=$isAvatar"
        $response = Invoke-RestMethod -Uri $uri -Method Post -Body $bodyLines -ContentType "multipart/form-data; boundary=$boundary" -Headers @{
            "Authorization" = "Bearer $token"
        }

        if ($response.success) {
            $photoId = $response.data.photoId
            $photoUrl = $response.data.photoUrl
            $isAvatarText = if ($response.data.isAvatar) { " (设为头像)" } else { "" }
            Write-Host "  ✓ 上传成功 - photoId: $photoId$isAvatarText" -ForegroundColor Green
            Write-Host "    URL: $photoUrl" -ForegroundColor Gray
            $uploadCount++
            $firstPhoto = $false
        } else {
            Write-Host "  ✗ 上传失败: $($response.message)" -ForegroundColor Red
            $failCount++
        }
    } catch {
        Write-Host "  ✗ 上传失败: $_" -ForegroundColor Red
        $failCount++
    }

    Write-Host ""
}

# 5. 显示上传结果
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "上传完成" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "成功: $uploadCount 张" -ForegroundColor Green
Write-Host "失败: $failCount 张" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""

# 6. 查询用户相册
Write-Host "4. 查询用户相册..." -ForegroundColor Yellow
try {
    $photosResponse = Invoke-RestMethod -Uri "$BaseUrl/users/$userId/photos" -Method Get -Headers @{
        "Authorization" = "Bearer $token"
    }

    if ($photosResponse.success) {
        Write-Host "相册照片总数: $($photosResponse.data.Count)" -ForegroundColor Green
        foreach ($photo in $photosResponse.data) {
            $avatarText = if ($photo.isAvatar) { " [头像]" } else { "" }
            Write-Host "  - ID: $($photo.id) | $($photo.photoUrl)$avatarText" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "查询相册失败: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "完成!" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
