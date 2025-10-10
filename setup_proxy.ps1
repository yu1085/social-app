# 设置终端代理配置脚本
# 使用方法：在PowerShell中运行 .\setup_proxy.ps1

Write-Host "正在设置终端代理配置..." -ForegroundColor Green

# 设置HTTP代理
$env:http_proxy = "http://127.0.0.1:1087"
$env:https_proxy = "http://127.0.0.1:1087"

# 设置系统环境变量（永久）
[Environment]::SetEnvironmentVariable("http_proxy", "http://127.0.0.1:1087", "User")
[Environment]::SetEnvironmentVariable("https_proxy", "http://127.0.0.1:1087", "User")

Write-Host "代理设置完成！" -ForegroundColor Green
Write-Host "HTTP代理: $env:http_proxy" -ForegroundColor Yellow
Write-Host "HTTPS代理: $env:https_proxy" -ForegroundColor Yellow

# 验证代理设置
Write-Host "`n验证代理设置..." -ForegroundColor Cyan
Write-Host "当前HTTP代理: $env:http_proxy"
Write-Host "当前HTTPS代理: $env:https_proxy"

Write-Host "`n代理配置已设置完成！" -ForegroundColor Green
Write-Host "注意：请确保您的代理软件正在运行在 127.0.0.1:1087" -ForegroundColor Red
