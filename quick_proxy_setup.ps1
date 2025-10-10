# 一键代理配置脚本
# 使用方法：在PowerShell中运行 .\quick_proxy_setup.ps1

Write-Host "=== 一键代理配置工具 ===" -ForegroundColor Cyan

# 检查是否以管理员身份运行
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if (-not $isAdmin) {
    Write-Host "警告：建议以管理员身份运行此脚本以获得最佳效果" -ForegroundColor Yellow
}

# 设置代理
Write-Host "`n1. 设置代理环境变量..." -ForegroundColor Yellow
$env:http_proxy = "http://127.0.0.1:1087"
$env:https_proxy = "http://127.0.0.1:1087"

# 设置系统环境变量
[Environment]::SetEnvironmentVariable("http_proxy", "http://127.0.0.1:1087", "User")
[Environment]::SetEnvironmentVariable("https_proxy", "http://127.0.0.1:1087", "User")

Write-Host "✓ 代理环境变量设置完成" -ForegroundColor Green

# 创建代理配置文件
Write-Host "`n2. 创建代理配置文件..." -ForegroundColor Yellow

# 创建Git代理配置
$gitConfig = @"
[http]
    proxy = http://127.0.0.1:1087
[https]
    proxy = http://127.0.0.1:1087
"@

$gitConfig | Out-File -FilePath ".gitproxy" -Encoding UTF8
Write-Host "✓ Git代理配置文件已创建" -ForegroundColor Green

# 创建npm代理配置
$npmConfig = @"
registry=https://registry.npmjs.org/
proxy=http://127.0.0.1:1087
https-proxy=http://127.0.0.1:1087
"@

$npmConfig | Out-File -FilePath ".npmrc" -Encoding UTF8
Write-Host "✓ NPM代理配置文件已创建" -ForegroundColor Green

# 创建Maven代理配置
$mavenConfig = @"
<settings>
  <proxies>
    <proxy>
      <id>http-proxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>127.0.0.1</host>
      <port>1087</port>
    </proxy>
    <proxy>
      <id>https-proxy</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>127.0.0.1</host>
      <port>1087</port>
    </proxy>
  </proxies>
</settings>
"@

$mavenConfig | Out-File -FilePath "settings.xml" -Encoding UTF8
Write-Host "✓ Maven代理配置文件已创建" -ForegroundColor Green

# 验证配置
Write-Host "`n3. 验证配置..." -ForegroundColor Yellow
Write-Host "当前HTTP代理: $env:http_proxy"
Write-Host "当前HTTPS代理: $env:https_proxy"

# 测试连接
Write-Host "`n4. 测试网络连接..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 5 -UseBasicParsing
    Write-Host "✓ 网络连接测试成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 网络连接测试失败，请检查代理软件是否运行" -ForegroundColor Red
}

Write-Host "`n=== 配置完成 ===" -ForegroundColor Cyan
Write-Host "已创建以下配置文件：" -ForegroundColor Green
Write-Host "- .gitproxy (Git代理配置)" -ForegroundColor White
Write-Host "- .npmrc (NPM代理配置)" -ForegroundColor White
Write-Host "- settings.xml (Maven代理配置)" -ForegroundColor White
Write-Host "`n请确保您的代理软件正在运行在 127.0.0.1:1087" -ForegroundColor Red
Write-Host "建议重启终端以确保所有配置生效" -ForegroundColor Yellow
