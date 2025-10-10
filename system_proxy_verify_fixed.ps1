# 系统级代理验证脚本
# 使用方法：在PowerShell中运行 .\system_proxy_verify_fixed.ps1

Write-Host "=== 系统级代理配置验证工具 ===" -ForegroundColor Cyan

# 检查管理员权限
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if ($isAdmin) {
    Write-Host "✓ 当前以管理员权限运行" -ForegroundColor Green
} else {
    Write-Host "⚠ 当前未以管理员权限运行，某些系统级配置可能无法查看" -ForegroundColor Yellow
}

# 1. 检查系统级环境变量
Write-Host "`n1. 检查系统级环境变量..." -ForegroundColor Yellow
$sysHttpProxy = [Environment]::GetEnvironmentVariable("http_proxy", "Machine")
$sysHttpsProxy = [Environment]::GetEnvironmentVariable("https_proxy", "Machine")
$sysHttpProxyUpper = [Environment]::GetEnvironmentVariable("HTTP_PROXY", "Machine")
$sysHttpsProxyUpper = [Environment]::GetEnvironmentVariable("HTTPS_PROXY", "Machine")

Write-Host "系统级 http_proxy: $sysHttpProxy"
Write-Host "系统级 https_proxy: $sysHttpsProxy"
Write-Host "系统级 HTTP_PROXY: $sysHttpProxyUpper"
Write-Host "系统级 HTTPS_PROXY: $sysHttpsProxyUpper"

if ($sysHttpProxy -or $sysHttpsProxy -or $sysHttpProxyUpper -or $sysHttpsProxyUpper) {
    Write-Host "✓ 系统级代理环境变量已设置" -ForegroundColor Green
} else {
    Write-Host "✗ 系统级代理环境变量未设置" -ForegroundColor Red
}

# 2. 检查用户级环境变量
Write-Host "`n2. 检查用户级环境变量..." -ForegroundColor Yellow
$userHttpProxy = [Environment]::GetEnvironmentVariable("http_proxy", "User")
$userHttpsProxy = [Environment]::GetEnvironmentVariable("https_proxy", "User")
$userHttpProxyUpper = [Environment]::GetEnvironmentVariable("HTTP_PROXY", "User")
$userHttpsProxyUpper = [Environment]::GetEnvironmentVariable("HTTPS_PROXY", "User")

Write-Host "用户级 http_proxy: $userHttpProxy"
Write-Host "用户级 https_proxy: $userHttpsProxy"
Write-Host "用户级 HTTP_PROXY: $userHttpProxyUpper"
Write-Host "用户级 HTTPS_PROXY: $userHttpsProxyUpper"

if ($userHttpProxy -or $userHttpsProxy -or $userHttpProxyUpper -or $userHttpsProxyUpper) {
    Write-Host "✓ 用户级代理环境变量已设置" -ForegroundColor Green
} else {
    Write-Host "✗ 用户级代理环境变量未设置" -ForegroundColor Red
}

# 3. 检查当前会话环境变量
Write-Host "`n3. 检查当前会话环境变量..." -ForegroundColor Yellow
Write-Host "当前会话 http_proxy: $env:http_proxy"
Write-Host "当前会话 https_proxy: $env:https_proxy"
Write-Host "当前会话 HTTP_PROXY: $env:HTTP_PROXY"
Write-Host "当前会话 HTTPS_PROXY: $env:HTTPS_PROXY"

if ($env:http_proxy -or $env:https_proxy -or $env:HTTP_PROXY -or $env:HTTPS_PROXY) {
    Write-Host "✓ 当前会话代理环境变量已设置" -ForegroundColor Green
} else {
    Write-Host "✗ 当前会话代理环境变量未设置" -ForegroundColor Red
}

# 4. 检查Windows系统代理
Write-Host "`n4. 检查Windows系统代理..." -ForegroundColor Yellow
try {
    $proxyEnable = Get-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyEnable" -ErrorAction SilentlyContinue
    $proxyServer = Get-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyServer" -ErrorAction SilentlyContinue
    
    if ($proxyEnable -and $proxyEnable.ProxyEnable -eq 1) {
        Write-Host "✓ Windows系统代理已启用" -ForegroundColor Green
        Write-Host "代理服务器: $($proxyServer.ProxyServer)" -ForegroundColor White
    } else {
        Write-Host "✗ Windows系统代理未启用" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法检查Windows系统代理: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 检查Git配置
Write-Host "`n5. 检查Git全局代理配置..." -ForegroundColor Yellow
try {
    $gitHttpProxy = git config --global --get http.proxy 2>$null
    $gitHttpsProxy = git config --global --get https.proxy 2>$null
    
    Write-Host "Git HTTP代理: $gitHttpProxy"
    Write-Host "Git HTTPS代理: $gitHttpsProxy"
    
    if ($gitHttpProxy -or $gitHttpsProxy) {
        Write-Host "✓ Git全局代理已配置" -ForegroundColor Green
    } else {
        Write-Host "✗ Git全局代理未配置" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法检查Git配置: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 检查NPM配置
Write-Host "`n6. 检查NPM全局代理配置..." -ForegroundColor Yellow
try {
    $npmProxy = npm config get proxy 2>$null
    $npmHttpsProxy = npm config get https-proxy 2>$null
    
    Write-Host "NPM代理: $npmProxy"
    Write-Host "NPM HTTPS代理: $npmHttpsProxy"
    
    if ($npmProxy -and $npmProxy -ne "null" -and $npmProxy -ne "") {
        Write-Host "✓ NPM全局代理已配置" -ForegroundColor Green
    } else {
        Write-Host "✗ NPM全局代理未配置" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法检查NPM配置: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. 检查Yarn配置
Write-Host "`n7. 检查Yarn全局代理配置..." -ForegroundColor Yellow
try {
    $yarnProxy = yarn config get proxy 2>$null
    $yarnHttpsProxy = yarn config get https-proxy 2>$null
    
    Write-Host "Yarn代理: $yarnProxy"
    Write-Host "Yarn HTTPS代理: $yarnHttpsProxy"
    
    if ($yarnProxy -and $yarnProxy -ne "null" -and $yarnProxy -ne "") {
        Write-Host "✓ Yarn全局代理已配置" -ForegroundColor Green
    } else {
        Write-Host "✗ Yarn全局代理未配置" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法检查Yarn配置: $($_.Exception.Message)" -ForegroundColor Red
}

# 8. 检查Maven配置
Write-Host "`n8. 检查Maven全局代理配置..." -ForegroundColor Yellow
try {
    $mavenHome = $env:MAVEN_HOME
    if (-not $mavenHome) {
        $mavenHome = "${env:ProgramFiles}\Apache\maven"
    }
    
    if (Test-Path $mavenHome) {
        $settingsPath = "$mavenHome\conf\settings.xml"
        if (Test-Path $settingsPath) {
            $settingsContent = Get-Content $settingsPath -Raw
            if ($settingsContent -match "proxy") {
                Write-Host "✓ Maven全局代理已配置" -ForegroundColor Green
            } else {
                Write-Host "✗ Maven全局代理未配置" -ForegroundColor Red
            }
        } else {
            Write-Host "✗ Maven配置文件不存在" -ForegroundColor Red
        }
    } else {
        Write-Host "- Maven未安装，跳过检查" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ 无法检查Maven配置: $($_.Exception.Message)" -ForegroundColor Red
}

# 9. 检查Docker配置
Write-Host "`n9. 检查Docker代理配置..." -ForegroundColor Yellow
try {
    $dockerConfigPath = "$env:USERPROFILE\.docker\config.json"
    if (Test-Path $dockerConfigPath) {
        $dockerConfig = Get-Content $dockerConfigPath -Raw | ConvertFrom-Json
        if ($dockerConfig.proxies) {
            Write-Host "✓ Docker代理已配置" -ForegroundColor Green
        } else {
            Write-Host "✗ Docker代理未配置" -ForegroundColor Red
        }
    } else {
        Write-Host "✗ Docker配置文件不存在" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 无法检查Docker配置: $($_.Exception.Message)" -ForegroundColor Red
}

# 10. 测试网络连接
Write-Host "`n10. 测试网络连接..." -ForegroundColor Yellow

$testUrls = @(
    @{Name="Google"; Url="https://www.google.com"},
    @{Name="Anthropic"; Url="https://www.anthropic.com"},
    @{Name="Cursor"; Url="https://www.cursor.so"},
    @{Name="OpenAI"; Url="https://www.openai.com"}
)

foreach ($test in $testUrls) {
    try {
        $response = Invoke-WebRequest -Uri $test.Url -TimeoutSec 10 -UseBasicParsing
        Write-Host "✓ $($test.Name) 连接成功 (状态码: $($response.StatusCode))" -ForegroundColor Green
    } catch {
        Write-Host "✗ $($test.Name) 连接失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 11. 检查代理软件状态
Write-Host "`n11. 检查代理软件状态..." -ForegroundColor Yellow
try {
    $netstat = netstat -an | Select-String ":1087"
    if ($netstat) {
        Write-Host "✓ 检测到端口1087有活动连接" -ForegroundColor Green
        Write-Host "活动连接:" -ForegroundColor White
        $netstat | ForEach-Object { Write-Host "  $_" -ForegroundColor White }
    } else {
        Write-Host "✗ 未检测到端口1087的活动连接" -ForegroundColor Red
        Write-Host "请确保代理软件正在运行" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ 无法检查端口状态: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 验证完成 ===" -ForegroundColor Cyan
Write-Host "如果所有检查都显示为绿色，说明代理配置正确" -ForegroundColor Green
Write-Host "如果有红色项目，请检查相应配置" -ForegroundColor Red
Write-Host "建议定期运行此脚本以验证代理配置状态" -ForegroundColor Yellow
