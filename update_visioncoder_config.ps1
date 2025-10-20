# 更新 VisionCoder 环境变量配置
# 使用新的API密钥和配置

Write-Host "正在更新 VisionCoder 环境变量配置..." -ForegroundColor Green

# 新的环境变量配置
$envVars = @{
    "ANTHROPIC_API_KEY" = "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
    "ANTHROPIC_AUTH_TOKEN" = "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
    "ANTHROPIC_BASE_URL" = "https://coder.api.visioncoder.cn"
    "CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC" = "1"
    "CLAUDE_MODEL" = "claude-sonnet-4-20250514"
}

Write-Host "`n新的配置信息：" -ForegroundColor Yellow
Write-Host "API密钥: sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38" -ForegroundColor Cyan
Write-Host "API地址: https://coder.api.visioncoder.cn" -ForegroundColor Cyan
Write-Host "模型: claude-sonnet-4-20250514" -ForegroundColor Cyan

# 更新用户环境变量
Write-Host "`n正在更新用户环境变量..." -ForegroundColor Green
foreach ($var in $envVars.GetEnumerator()) {
    try {
        [Environment]::SetEnvironmentVariable($var.Key, $var.Value, "User")
        Write-Host "✓ 已更新用户环境变量: $($var.Key)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ 更新用户环境变量失败: $($var.Key) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 更新系统环境变量（需要管理员权限）
Write-Host "`n正在更新系统环境变量..." -ForegroundColor Green
try {
    foreach ($var in $envVars.GetEnumerator()) {
        [Environment]::SetEnvironmentVariable($var.Key, $var.Value, "Machine")
        Write-Host "✓ 已更新系统环境变量: $($var.Key)" -ForegroundColor Green
    }
}
catch {
    Write-Host "⚠ 更新系统环境变量失败，可能需要管理员权限" -ForegroundColor Yellow
    Write-Host "请以管理员身份运行此脚本" -ForegroundColor Yellow
}

Write-Host "`n配置更新完成！" -ForegroundColor Cyan
Write-Host "请重新打开终端窗口以使新的环境变量生效。" -ForegroundColor Yellow

Write-Host "`n验证新配置，请运行以下命令：" -ForegroundColor Yellow
Write-Host "echo `$env:ANTHROPIC_API_KEY" -ForegroundColor White
Write-Host "echo `$env:ANTHROPIC_BASE_URL" -ForegroundColor White
Write-Host "claude --version" -ForegroundColor White

