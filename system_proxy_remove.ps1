# 系统级代理移除脚本
# 使用方法：以管理员身份运行PowerShell，然后执行 .\system_proxy_remove.ps1

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if (-not $isAdmin) {
    Write-Host "错误：此脚本需要管理员权限才能修改系统级设置" -ForegroundColor Red
    Write-Host "请以管理员身份运行PowerShell，然后重新执行此脚本" -ForegroundColor Yellow
    Read-Host "按任意键退出"
    exit 1
}

Write-Host "=== 系统级代理移除工具 ===" -ForegroundColor Cyan

# 清除系统级环境变量
Write-Host "`n1. 清除系统级环境变量..." -ForegroundColor Yellow
[Environment]::SetEnvironmentVariable("http_proxy", $null, "Machine")
[Environment]::SetEnvironmentVariable("https_proxy", $null, "Machine")
[Environment]::SetEnvironmentVariable("HTTP_PROXY", $null, "Machine")
[Environment]::SetEnvironmentVariable("HTTPS_PROXY", $null, "Machine")
[Environment]::SetEnvironmentVariable("no_proxy", $null, "Machine")
[Environment]::SetEnvironmentVariable("NO_PROXY", $null, "Machine")

Write-Host "✓ 系统级环境变量已清除" -ForegroundColor Green

# 清除当前会话环境变量
Write-Host "`n2. 清除当前会话环境变量..." -ForegroundColor Yellow
$env:http_proxy = $null
$env:https_proxy = $null
$env:HTTP_PROXY = $null
$env:HTTPS_PROXY = $null
$env:no_proxy = $null
$env:NO_PROXY = $null

Write-Host "✓ 当前会话环境变量已清除" -ForegroundColor Green

# 禁用Windows系统代理
Write-Host "`n3. 禁用Windows系统代理..." -ForegroundColor Yellow
try {
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyEnable" -Value 0
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyServer" -Value ""
    Set-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyOverride" -Value ""
    
    Write-Host "✓ Windows系统代理已禁用" -ForegroundColor Green
} catch {
    Write-Host "✗ Windows系统代理禁用失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 清除Git全局代理
Write-Host "`n4. 清除Git全局代理..." -ForegroundColor Yellow
try {
    git config --global --unset http.proxy
    git config --global --unset https.proxy
    Write-Host "✓ Git全局代理已清除" -ForegroundColor Green
} catch {
    Write-Host "✗ Git全局代理清除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 清除NPM全局代理
Write-Host "`n5. 清除NPM全局代理..." -ForegroundColor Yellow
try {
    npm config delete proxy
    npm config delete https-proxy
    Write-Host "✓ NPM全局代理已清除" -ForegroundColor Green
} catch {
    Write-Host "✗ NPM全局代理清除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 清除Yarn全局代理
Write-Host "`n6. 清除Yarn全局代理..." -ForegroundColor Yellow
try {
    yarn config delete proxy
    yarn config delete https-proxy
    Write-Host "✓ Yarn全局代理已清除" -ForegroundColor Green
} catch {
    Write-Host "✗ Yarn全局代理清除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 清除Maven全局代理
Write-Host "`n7. 清除Maven全局代理..." -ForegroundColor Yellow
try {
    $mavenHome = $env:MAVEN_HOME
    if (-not $mavenHome) {
        $mavenHome = "${env:ProgramFiles}\Apache\maven"
    }
    
    if (Test-Path $mavenHome) {
        $settingsPath = "$mavenHome\conf\settings.xml"
        if (Test-Path $settingsPath) {
            # 备份原文件
            Copy-Item $settingsPath "$settingsPath.backup.$(Get-Date -Format 'yyyyMMdd-HHmmss')" -Force
            Write-Host "✓ Maven配置文件已备份" -ForegroundColor Green
        }
        
        # 创建空的settings.xml
        $emptySettings = @"
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
</settings>
"@
        $emptySettings | Out-File -FilePath $settingsPath -Encoding UTF8
        Write-Host "✓ Maven全局代理已清除" -ForegroundColor Green
    } else {
        Write-Host "- Maven未找到，跳过Maven配置清除" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Maven全局代理清除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 清除Docker代理
Write-Host "`n8. 清除Docker代理..." -ForegroundColor Yellow
try {
    $dockerConfigPath = "$env:USERPROFILE\.docker\config.json"
    if (Test-Path $dockerConfigPath) {
        # 备份原文件
        Copy-Item $dockerConfigPath "$dockerConfigPath.backup.$(Get-Date -Format 'yyyyMMdd-HHmmss')" -Force
        Write-Host "✓ Docker配置文件已备份" -ForegroundColor Green
    }
    
    # 创建空的Docker配置
    $emptyDockerConfig = @{
        "auths" = @{}
    }
    
    $emptyDockerConfig | ConvertTo-Json -Depth 3 | Out-File -FilePath $dockerConfigPath -Encoding UTF8
    Write-Host "✓ Docker代理已清除" -ForegroundColor Green
} catch {
    Write-Host "✗ Docker代理清除失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 验证清除结果
Write-Host "`n9. 验证清除结果..." -ForegroundColor Yellow
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
}

Write-Host "`n=== 系统级代理已完全移除 ===" -ForegroundColor Cyan
Write-Host "已清除以下配置：" -ForegroundColor Green
Write-Host "- 系统环境变量" -ForegroundColor White
Write-Host "- Windows系统代理" -ForegroundColor White
Write-Host "- Git全局配置" -ForegroundColor White
Write-Host "- NPM全局配置" -ForegroundColor White
Write-Host "- Yarn全局配置" -ForegroundColor White
Write-Host "- Maven全局配置" -ForegroundColor White
Write-Host "- Docker代理配置" -ForegroundColor White
Write-Host "`n网络将使用直连方式，建议重启计算机以确保所有配置完全生效" -ForegroundColor Yellow
