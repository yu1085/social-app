# 测试用户列表价格显示功能

$baseUrl = "http://localhost:8080/api"

# 测试账号的 token（需要先登录获取）
# 使用 video_receiver 账号，从之前的日志中获取的 token
$token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIyNDkxNzI5LCJ1c2VybmFtZSI6InZpZGVvX3JlY2VpdmVyIiwic3ViIjoidmlkZW9fcmVjZWl2ZXIiLCJpYXQiOjE3NjA5NDk0NjcsImV4cCI6MTc2MTAzNTg2N30.-D1SichjoDl-nSvWngPP0UvSUBqcJyEGrbo5gCDCa9o"

Write-Host "=" * 80
Write-Host "测试用户列表价格显示功能"
Write-Host "=" * 80
Write-Host ""

# 1. 获取当前用户信息（包含价格）
Write-Host "1. 获取当前用户信息（应包含价格字段）"
Write-Host "-" * 80
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/profile" `
        -Method Get `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }

    Write-Host "✅ 成功获取当前用户信息"
    Write-Host "用户ID: $($response.data.id)"
    Write-Host "用户名: $($response.data.username)"
    Write-Host "昵称: $($response.data.nickname)"
    Write-Host "语音通话价格: $($response.data.voiceCallPrice) 元/分钟"
    Write-Host "视频通话价格: $($response.data.videoCallPrice) 元/分钟"
} catch {
    Write-Host "❌ 获取当前用户信息失败: $_"
}
Write-Host ""

# 2. 获取指定用户信息（包含价格）
Write-Host "2. 获取指定用户信息（ID: 22491729）"
Write-Host "-" * 80
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/22491729" `
        -Method Get `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }

    Write-Host "✅ 成功获取用户信息"
    Write-Host "用户ID: $($response.data.id)"
    Write-Host "用户名: $($response.data.username)"
    Write-Host "昵称: $($response.data.nickname)"
    Write-Host "语音通话价格: $($response.data.voiceCallPrice) 元/分钟"
    Write-Host "视频通话价格: $($response.data.videoCallPrice) 元/分钟"
} catch {
    Write-Host "❌ 获取用户信息失败: $_"
}
Write-Host ""

# 3. 搜索用户列表（所有用户都应包含价格）
Write-Host "3. 搜索用户列表（应显示所有用户的价格）"
Write-Host "-" * 80
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/search?page=0&size=5" `
        -Method Get `
        -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }

    Write-Host "✅ 成功获取用户列表"
    Write-Host "返回用户数: $($response.data.Count)"
    Write-Host ""

    foreach ($user in $response.data) {
        Write-Host "  用户: $($user.nickname) (ID: $($user.id))"
        Write-Host "    - 语音通话: $($user.voiceCallPrice) 元/分钟"
        Write-Host "    - 视频通话: $($user.videoCallPrice) 元/分钟"
        Write-Host ""
    }
} catch {
    Write-Host "❌ 搜索用户列表失败: $_"
}

Write-Host "=" * 80
Write-Host "测试完成！"
Write-Host "=" * 80
Write-Host ""
Write-Host "说明："
Write-Host "- 当用户修改价格后，所有API都会返回最新的价格"
Write-Host "- 首页用户列表刷新后会显示更新的价格"
Write-Host "- 价格为 0 表示免费"
Write-Host ""
