# 验证代理配置脚本
# 使用方法：在PowerShell中运行 .\verify_proxy.ps1

Write-Host "=== 代理配置验证工具 ===" -ForegroundColor Cyan

# 检查环境变量
Write-Host "`n1. 检查环境变量..." -ForegroundColor Yellow
Write-Host "HTTP代理: $env:http_proxy"
Write-Host "HTTPS代理: $env:https_proxy"

# 检查系统环境变量
Write-Host "`n2. 检查系统环境变量..." -ForegroundColor Yellow
$sysHttpProxy = [Environment]::GetEnvironmentVariable("http_proxy", "User")
$sysHttpsProxy = [Environment]::GetEnvironmentVariable("https_proxy", "User")
Write-Host "系统HTTP代理: $sysHttpProxy"
Write-Host "系统HTTPS代理: $sysHttpsProxy"

# 测试网络连接
Write-Host "`n3. 测试网络连接..." -ForegroundColor Yellow

# 测试Google连接
try {
    $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 10 -UseBasicParsing
    Write-Host "✓ Google连接成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ Google连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试Anthropic连接
try {
    $response = Invoke-WebRequest -Uri "https://www.anthropic.com" -TimeoutSec 10 -UseBasicParsing
    Write-Host "✓ Anthropic连接成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ Anthropic连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试Cursor连接
try {
    $response = Invoke-WebRequest -Uri "https://www.cursor.so" -TimeoutSec 10 -UseBasicParsing
    Write-Host "✓ Cursor连接成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ Cursor连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 验证完成 ===" -ForegroundColor Cyan
Write-Host "如果所有连接都成功，说明代理配置正确" -ForegroundColor Green
Write-Host "如果有连接失败，请检查代理软件是否正在运行" -ForegroundColor Red
