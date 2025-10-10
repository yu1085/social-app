# 系统级代理配置脚本
# 使用方法：以管理员身份运行PowerShell，然后执行 .\system_proxy_setup.ps1

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if (-not $isAdmin) {
    Write-Host "错误：此脚本需要管理员权限才能修改系统级设置" -ForegroundColor Red
    Write-Host "请以管理员身份运行PowerShell，然后重新执行此脚本" -ForegroundColor Yellow
    Read-Host "按任意键退出"
    exit 1
}

Write-Host "=== 系统级代理配置工具 ===" -ForegroundColor Cyan

# 设置系统级环境变量
Write-Host "`n1. 设置系统级环境变量..." -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("http_proxy", "http://127.0.0.1:1087", "Machine")
[Environment]::SetEnvironmentVariable("https_proxy", "http://127.0.0.1:1087", "Machine")
[Environment]::SetEnvironmentVariable("HTTP_PROXY", "http://127.0.0.1:1087", "Machine")
[Environment]::SetEnvironmentVariable("HTTPS_PROXY", "http://127.0.0.1:1087", "Machine")
[Environment]::SetEnvironmentVariable("no_proxy", "localhost,127.0.0.1", "Machine")
[Environment]::SetEnvironmentVariable("NO_PROXY", "localhost,127.0.0.1", "Machine")

Write-Host "✓ 系统级环境变量设置完成" -ForegroundColor Green

# 设置当前会话环境变量
Write-Host "`n2. 设置当前会话环境变量..." -ForegroundColor Yellow
$env:http_proxy = "http://127.0.0.1:1087"
$env:https_proxy = "http://127.0.0.1:1087"
$env:HTTP_PROXY = "http://127.0.0.1:1087"
$env:HTTPS_PROXY = "http://127.0.0.1:1087"
$env:no_proxy = "localhost,127.0.0.1"
$env:NO_PROXY = "localhost,127.0.0.1"

Write-Host "✓ 当前会话环境变量设置完成" -ForegroundColor Green

# 配置Windows系统代理
Write-Host "`n3. 配置Windows系统代理..." -ForegroundColor Yellow
try {
    # 启用系统代理
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyEnable" -Value 1
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyServer" -Value "127.0.0.1:1087"
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyOverride" -Value "localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.*"
    
    Write-Host "✓ Windows系统代理配置完成" -ForegroundColor Green
} catch {
    Write-Host "✗ Windows系统代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 配置Git全局代理
Write-Host "`n4. 配置Git全局代理..." -ForegroundColor Yellow
try {
    git config --global http.proxy http://127.0.0.1:1087
    git config --global https.proxy http://127.0.0.1:1087
    Write-Host "✓ Git全局代理配置完成" -ForegroundColor Green
} catch {
    Write-Host "✗ Git全局代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 配置NPM全局代理
Write-Host "`n5. 配置NPM全局代理..." -ForegroundColor Yellow
try {
    npm config set proxy http://127.0.0.1:1087
    npm config set https-proxy http://127.0.0.1:1087
    npm config set registry https://registry.npmjs.org/
    Write-Host "✓ NPM全局代理配置完成" -ForegroundColor Green
} catch {
    Write-Host "✗ NPM全局代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 配置Yarn全局代理
Write-Host "`n6. 配置Yarn全局代理..." -ForegroundColor Yellow
try {
    yarn config set proxy http://127.0.0.1:1087
    yarn config set https-proxy http://127.0.0.1:1087
    Write-Host "✓ Yarn全局代理配置完成" -ForegroundColor Green
} catch {
    Write-Host "✗ Yarn全局代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 配置Maven全局代理
Write-Host "`n7. 配置Maven全局代理..." -ForegroundColor Yellow
try {
    $mavenHome = $env:MAVEN_HOME
    if (-not $mavenHome) {
        $mavenHome = "${env:ProgramFiles}\Apache\maven"
    }
    
    if (Test-Path $mavenHome) {
        $settingsPath = "$mavenHome\conf\settings.xml"
        $mavenConfig = @"
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
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
        $mavenConfig | Out-File -FilePath $settingsPath -Encoding UTF8
        Write-Host "✓ Maven全局代理配置完成" -ForegroundColor Green
    } else {
        Write-Host "- Maven未找到，跳过Maven配置" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Maven全局代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 配置Docker代理（如果存在）
Write-Host "`n8. 配置Docker代理..." -ForegroundColor Yellow
try {
    $dockerConfigPath = "$env:USERPROFILE\.docker\config.json"
    $dockerDir = Split-Path $dockerConfigPath -Parent
    
    if (-not (Test-Path $dockerDir)) {
        New-Item -ItemType Directory -Path $dockerDir -Force | Out-Null
    }
    
    $dockerConfig = @{
        "proxies" = @{
            "default" = @{
                "httpProxy" = "http://127.0.0.1:1087"
                "httpsProxy" = "http://127.0.0.1:1087"
                "noProxy" = "localhost,127.0.0.1"
            }
        }
    }
    
    $dockerConfig | ConvertTo-Json -Depth 3 | Out-File -FilePath $dockerConfigPath -Encoding UTF8
    Write-Host "✓ Docker代理配置完成" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker代理配置失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 验证配置
Write-Host "`n9. 验证配置..." -ForegroundColor Yellow
Write-Host "系统HTTP代理: $([Environment]::GetEnvironmentVariable('http_proxy', 'Machine'))"
Write-Host "系统HTTPS代理: $([Environment]::GetEnvironmentVariable('https_proxy', 'Machine'))"
Write-Host "当前会话HTTP代理: $env:http_proxy"
Write-Host "当前会话HTTPS代理: $env:https_proxy"

# 测试网络连接
Write-Host "`n10. 测试网络连接..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 10 -UseBasicParsing
    Write-Host "✓ 网络连接测试成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "✗ 网络连接测试失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "请检查代理软件是否正在运行" -ForegroundColor Yellow
}

Write-Host "`n=== 系统级代理配置完成 ===" -ForegroundColor Cyan
Write-Host "配置已应用到整个系统，包括：" -ForegroundColor Green
Write-Host "- 系统环境变量" -ForegroundColor White
Write-Host "- Windows系统代理" -ForegroundColor White
Write-Host "- Git全局配置" -ForegroundColor White
Write-Host "- NPM全局配置" -ForegroundColor White
Write-Host "- Yarn全局配置" -ForegroundColor White
Write-Host "- Maven全局配置" -ForegroundColor White
Write-Host "- Docker代理配置" -ForegroundColor White
Write-Host "`n注意：请确保您的代理软件正在运行在 127.0.0.1:1087" -ForegroundColor Red
Write-Host "建议重启计算机以确保所有配置完全生效" -ForegroundColor Yellow
