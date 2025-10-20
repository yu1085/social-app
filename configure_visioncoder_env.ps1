# VisionCoder 环境变量配置脚本
# 使用您提供的API密钥

Write-Host "正在配置 VisionCoder 环境变量..." -ForegroundColor Green

# 设置环境变量
$envVars = @{
    "ANTHROPIC_API_KEY" = "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
    "ANTHROPIC_AUTH_TOKEN" = "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
    "ANTHROPIC_BASE_URL" = "https://coder.api.visioncoder.cn"
    "CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC" = "1"
    "CLAUDE_MODEL" = "claude-sonnet-4-20250514"
}

# 为当前用户设置环境变量
foreach ($var in $envVars.GetEnumerator()) {
    try {
        [Environment]::SetEnvironmentVariable($var.Key, $var.Value, "User")
        Write-Host "✓ 已设置用户环境变量: $($var.Key)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ 设置用户环境变量失败: $($var.Key) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n配置完成！请重新打开终端窗口以使环境变量生效。" -ForegroundColor Cyan
Write-Host "`n验证配置，请运行以下命令：" -ForegroundColor Yellow
Write-Host "echo `$env:ANTHROPIC_API_KEY" -ForegroundColor White
Write-Host "claude --version" -ForegroundColor White
