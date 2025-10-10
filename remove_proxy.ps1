# 移除代理配置脚本
# 使用方法：在PowerShell中运行 .\remove_proxy.ps1

Write-Host "=== 移除代理配置工具 ===" -ForegroundColor Cyan

# 清除环境变量
Write-Host "`n1. 清除环境变量..." -ForegroundColor Yellow
$env:http_proxy = $null
$env:https_proxy = $null

# 清除系统环境变量
[Environment]::SetEnvironmentVariable("http_proxy", $null, "User")
[Environment]::SetEnvironmentVariable("https_proxy", $null, "User")

Write-Host "✓ 环境变量已清除" -ForegroundColor Green

# 删除配置文件
Write-Host "`n2. 删除代理配置文件..." -ForegroundColor Yellow

$configFiles = @(".gitproxy", ".npmrc", "settings.xml")

foreach ($file in $configFiles) {
    if (Test-Path $file) {
        Remove-Item $file -Force
        Write-Host "✓ 已删除 $file" -ForegroundColor Green
    } else {
        Write-Host "- $file 不存在，跳过" -ForegroundColor Gray
    }
}

# 验证清除结果
Write-Host "`n3. 验证清除结果..." -ForegroundColor Yellow
Write-Host "当前HTTP代理: $env:http_proxy"
Write-Host "当前HTTPS代理: $env:https_proxy"

Write-Host "`n=== 代理配置已移除 ===" -ForegroundColor Cyan
Write-Host "所有代理配置已清除，网络将使用直连方式" -ForegroundColor Green
