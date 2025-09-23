# 测试增强的动态API接口

$baseUrl = "http://localhost:8080"
$token = "your_test_token_here" # 需要替换为实际的JWT token

Write-Host "开始测试增强的动态API接口..." -ForegroundColor Green

# 1. 测试获取增强的动态列表
Write-Host "`n1. 测试获取增强的动态列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/enhanced?filter=nearby&page=0&size=10" -Method GET -Headers @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    Write-Host "✅ 获取动态列表成功" -ForegroundColor Green
    Write-Host "动态数量: $($response.data.content.Count)"
    if ($response.data.content.Count -gt 0) {
        $firstPost = $response.data.content[0]
        Write-Host "第一个动态: $($firstPost.content)"
        Write-Host "用户: $($firstPost.userName)"
        Write-Host "点赞数: $($firstPost.likeCount)"
    }
} catch {
    Write-Host "❌ 获取动态列表失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. 测试点赞动态（需要先有一个动态ID）
Write-Host "`n2. 测试点赞动态" -ForegroundColor Yellow
try {
    $postId = 1 # 假设动态ID为1
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/$postId/toggle-like" -Method POST -Headers @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    Write-Host "✅ 点赞动态成功" -ForegroundColor Green
    Write-Host "点赞状态: $($response.data.isLiked)"
    Write-Host "点赞数: $($response.data.likeCount)"
} catch {
    Write-Host "❌ 点赞动态失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 测试添加评论
Write-Host "`n3. 测试添加评论" -ForegroundColor Yellow
try {
    $postId = 1 # 假设动态ID为1
    $commentContent = "这是一条测试评论"
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/$postId/comments?content=$commentContent" -Method POST -Headers @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    Write-Host "✅ 添加评论成功" -ForegroundColor Green
    Write-Host "评论数: $($response.data.commentCount)"
} catch {
    Write-Host "❌ 添加评论失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 测试获取动态评论
Write-Host "`n4. 测试获取动态评论" -ForegroundColor Yellow
try {
    $postId = 1 # 假设动态ID为1
    $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/$postId/comments?page=0&size=20" -Method GET -Headers @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    Write-Host "✅ 获取评论成功" -ForegroundColor Green
    Write-Host "评论数量: $($response.data.Count)"
    if ($response.data.Count -gt 0) {
        $firstComment = $response.data[0]
        Write-Host "第一个评论: $($firstComment.content)"
        Write-Host "评论用户: $($firstComment.userName)"
    }
} catch {
    Write-Host "❌ 获取评论失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 测试不同筛选条件
Write-Host "`n5. 测试不同筛选条件" -ForegroundColor Yellow
$filters = @("nearby", "latest", "friends", "like")
foreach ($filter in $filters) {
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/posts/enhanced?filter=$filter&page=0&size=5" -Method GET -Headers @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        Write-Host "✅ 筛选条件 '$filter' 成功，返回 $($response.data.content.Count) 条动态" -ForegroundColor Green
    } catch {
        Write-Host "❌ 筛选条件 '$filter' 失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n测试完成！" -ForegroundColor Green
