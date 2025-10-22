# 测试亲密度系统API接口
$baseUrl = "http://localhost:8080/api"
$testPhone = "13800138000"
$testPhone2 = "19887654321"
$token = $null
$token2 = $null

Write-Host "=================================="
Write-Host "  测试亲密度系统API接口"
Write-Host "=================================="
Write-Host ""

# 登录获取token
Write-Host "1. 登录用户1 (手机号: $testPhone)..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/verify" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        phone = $testPhone
        code = "123456"
    } | ConvertTo-Json)

if ($loginResponse.success) {
    $token = $loginResponse.data.token
    $userId1 = $loginResponse.data.user.id
    Write-Host "✅ 登录成功 - userId: $userId1" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "❌ 登录失败: $($loginResponse.message)" -ForegroundColor Red
    exit 1
}

# 登录第二个用户
Write-Host "2. 登录用户2 (手机号: $testPhone2)..." -ForegroundColor Yellow
$loginResponse2 = Invoke-RestMethod -Uri "$baseUrl/auth/verify" `
    -Method POST `
    -ContentType "application/json" `
    -Body (@{
        phone = $testPhone2
        code = "123456"
    } | ConvertTo-Json)

if ($loginResponse2.success) {
    $token2 = $loginResponse2.data.token
    $userId2 = $loginResponse2.data.user.id
    Write-Host "✅ 登录成功 - userId: $userId2" -ForegroundColor Green
    Write-Host ""
} else {
    Write-Host "❌ 登录失败: $($loginResponse2.message)" -ForegroundColor Red
    exit 1
}

# 3. 查询所有等级配置
Write-Host "3. 查询所有亲密度等级配置..." -ForegroundColor Yellow
$levelsResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/levels" `
    -Method GET

if ($levelsResponse.success) {
    Write-Host "✅ 查询成功" -ForegroundColor Green
    $levelsResponse.data | ForEach-Object {
        Write-Host "  Lv.$($_.level) $($_.levelName) - 需要温度: $($_.requiredTemperature)°C, 奖励: $($_.rewardType)"
    }
    Write-Host ""
} else {
    Write-Host "❌ 查询失败: $($levelsResponse.message)" -ForegroundColor Red
}

# 4. 记录亲密度行为 - 发送10条消息（10聊币）
Write-Host "4. 用户1向用户2发送消息 - 消耗30聊币（+3°C）..." -ForegroundColor Yellow
$actionResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/action" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body (@{
        targetUserId = $userId2
        actionType = "MESSAGE"
        coinsSpent = 30
        actionCount = 3
    } | ConvertTo-Json)

if ($actionResponse.success) {
    Write-Host "✅ 行为记录成功" -ForegroundColor Green
    $intimacy = $actionResponse.data
    Write-Host "  目标用户: $($intimacy.targetUserId)"
    Write-Host "  当前温度: $($intimacy.currentTemperature)°C"
    Write-Host "  当前等级: Lv.$($intimacy.currentLevel) $($intimacy.currentLevelName)"
    if (-not $intimacy.maxLevel) {
        Write-Host "  下一等级: Lv.$($intimacy.currentLevel + 1) $($intimacy.nextLevelName) (需要 $($intimacy.nextLevelTemperature)°C)"
    }
    Write-Host "  消息总数: $($intimacy.messageCount)"
    Write-Host "  累计消耗: $($intimacy.totalCoinsSpent) 聊币"
    Write-Host "  相识天数: $($intimacy.daysKnown) 天"
    Write-Host ""
} else {
    Write-Host "❌ 行为记录失败: $($actionResponse.message)" -ForegroundColor Red
}

# 5. 再次记录行为 - 升级到Lv.2
Write-Host "5. 继续记录行为 - 消耗170聊币（+17°C，升级到Lv.2）..." -ForegroundColor Yellow
$actionResponse2 = Invoke-RestMethod -Uri "$baseUrl/intimacy/action" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $token" } `
    -ContentType "application/json" `
    -Body (@{
        targetUserId = $userId2
        actionType = "GIFT"
        coinsSpent = 170
        actionCount = 1
    } | ConvertTo-Json)

if ($actionResponse2.success) {
    Write-Host "✅ 行为记录成功" -ForegroundColor Green
    $intimacy2 = $actionResponse2.data
    Write-Host "  当前温度: $($intimacy2.currentTemperature)°C"
    Write-Host "  当前等级: Lv.$($intimacy2.currentLevel) $($intimacy2.currentLevelName)"
    if ($intimacy2.currentLevel -gt $intimacy.currentLevel) {
        Write-Host "  🎉 升级了！从 Lv.$($intimacy.currentLevel) 升到 Lv.$($intimacy2.currentLevel)" -ForegroundColor Magenta
    }
    Write-Host "  礼物总数: $($intimacy2.giftCount)"
    Write-Host ""
} else {
    Write-Host "❌ 行为记录失败: $($actionResponse2.message)" -ForegroundColor Red
}

# 6. 查询亲密度详情
Write-Host "6. 查询与用户2的亲密度详情..." -ForegroundColor Yellow
$intimacyDetailResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/$userId2" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }

if ($intimacyDetailResponse.success) {
    Write-Host "✅ 查询成功" -ForegroundColor Green
    $detail = $intimacyDetailResponse.data
    Write-Host "  目标用户: $($detail.targetUser.nickname) (ID: $($detail.targetUserId))"
    Write-Host "  当前等级: Lv.$($detail.currentLevel) $($detail.currentLevelName)"
    Write-Host "  当前温度: $($detail.currentTemperature)°C"
    Write-Host "  消息数: $($detail.messageCount), 礼物数: $($detail.giftCount)"
    Write-Host "  视频通话: $($detail.videoCallMinutes)分钟, 语音通话: $($detail.voiceCallMinutes)分钟"
    Write-Host "  累计消耗: $($detail.totalCoinsSpent) 聊币"
    Write-Host "  首次互动: $($detail.firstInteractionDate)"
    Write-Host ""
} else {
    Write-Host "❌ 查询失败: $($intimacyDetailResponse.message)" -ForegroundColor Red
}

# 7. 查询亲密度列表
Write-Host "7. 查询用户1的亲密度列表（前10）..." -ForegroundColor Yellow
$listResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/list?limit=10" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }

if ($listResponse.success) {
    Write-Host "✅ 查询成功 - 共 $($listResponse.data.Count) 条记录" -ForegroundColor Green
    $listResponse.data | ForEach-Object {
        Write-Host "  用户 $($_.targetUserId): Lv.$($_.currentLevel) $($_.currentLevelName) - $($_.currentTemperature)°C"
    }
    Write-Host ""
} else {
    Write-Host "❌ 查询失败: $($listResponse.message)" -ForegroundColor Red
}

# 8. 查询未领取奖励
Write-Host "8. 查询用户1的未领取奖励..." -ForegroundColor Yellow
$rewardsResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/rewards/unclaimed" `
    -Method GET `
    -Headers @{ Authorization = "Bearer $token" }

if ($rewardsResponse.success) {
    Write-Host "✅ 查询成功 - 共 $($rewardsResponse.data.Count) 个未领取奖励" -ForegroundColor Green
    $rewardsResponse.data | ForEach-Object {
        Write-Host "  奖励ID: $($_.id)"
        Write-Host "    等级: Lv.$($_.level) $($_.levelName)"
        Write-Host "    类型: $($_.rewardType)"
        Write-Host "    内容: $($_.rewardValue)"
        Write-Host "    是否已领取: $($_.isClaimed)"
        Write-Host ""

        # 尝试领取第一个奖励
        if (-not $_.isClaimed -and $null -eq $rewardId) {
            $rewardId = $_.id
        }
    }

    # 如果有未领取的奖励，尝试领取
    if ($rewardId) {
        Write-Host "9. 领取奖励 (ID: $rewardId)..." -ForegroundColor Yellow
        $claimResponse = Invoke-RestMethod -Uri "$baseUrl/intimacy/rewards/$rewardId/claim" `
            -Method POST `
            -Headers @{ Authorization = "Bearer $token" }

        if ($claimResponse.success) {
            Write-Host "✅ 领取成功" -ForegroundColor Green
            Write-Host "  奖励类型: $($claimResponse.data.rewardType)"
            Write-Host ""
        } else {
            Write-Host "❌ 领取失败: $($claimResponse.message)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "❌ 查询失败: $($rewardsResponse.message)" -ForegroundColor Red
}

Write-Host "=================================="
Write-Host "  亲密度系统API测试完成"
Write-Host "=================================="
