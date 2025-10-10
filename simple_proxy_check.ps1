# Simple Proxy Check Script
# Run this in PowerShell: .\simple_proxy_check.ps1

Write-Host "=== Proxy Configuration Check ===" -ForegroundColor Cyan

# Check admin rights
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if ($isAdmin) {
    Write-Host "Admin rights: YES" -ForegroundColor Green
} else {
    Write-Host "Admin rights: NO" -ForegroundColor Yellow
}

# Check system environment variables
Write-Host "`n1. System Environment Variables:" -ForegroundColor Yellow
$sysHttpProxy = [Environment]::GetEnvironmentVariable("http_proxy", "Machine")
$sysHttpsProxy = [Environment]::GetEnvironmentVariable("https_proxy", "Machine")
Write-Host "System http_proxy: $sysHttpProxy"
Write-Host "System https_proxy: $sysHttpsProxy"

if ($sysHttpProxy -or $sysHttpsProxy) {
    Write-Host "System proxy: CONFIGURED" -ForegroundColor Green
} else {
    Write-Host "System proxy: NOT CONFIGURED" -ForegroundColor Red
}

# Check current session variables
Write-Host "`n2. Current Session Variables:" -ForegroundColor Yellow
Write-Host "Session http_proxy: $env:http_proxy"
Write-Host "Session https_proxy: $env:https_proxy"

if ($env:http_proxy -or $env:https_proxy) {
    Write-Host "Session proxy: CONFIGURED" -ForegroundColor Green
} else {
    Write-Host "Session proxy: NOT CONFIGURED" -ForegroundColor Red
}

# Check Windows system proxy
Write-Host "`n3. Windows System Proxy:" -ForegroundColor Yellow
try {
    $proxyEnable = Get-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyEnable" -ErrorAction SilentlyContinue
    $proxyServer = Get-ItemProperty -Path "HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings" -Name "ProxyServer" -ErrorAction SilentlyContinue
    
    if ($proxyEnable -and $proxyEnable.ProxyEnable -eq 1) {
        Write-Host "Windows proxy: ENABLED" -ForegroundColor Green
        Write-Host "Proxy server: $($proxyServer.ProxyServer)"
    } else {
        Write-Host "Windows proxy: DISABLED" -ForegroundColor Red
    }
} catch {
    Write-Host "Windows proxy: ERROR - $($_.Exception.Message)" -ForegroundColor Red
}

# Check Git configuration
Write-Host "`n4. Git Configuration:" -ForegroundColor Yellow
try {
    $gitHttpProxy = git config --global --get http.proxy 2>$null
    $gitHttpsProxy = git config --global --get https.proxy 2>$null
    
    Write-Host "Git HTTP proxy: $gitHttpProxy"
    Write-Host "Git HTTPS proxy: $gitHttpsProxy"
    
    if ($gitHttpProxy -or $gitHttpsProxy) {
        Write-Host "Git proxy: CONFIGURED" -ForegroundColor Green
    } else {
        Write-Host "Git proxy: NOT CONFIGURED" -ForegroundColor Red
    }
} catch {
    Write-Host "Git proxy: ERROR - $($_.Exception.Message)" -ForegroundColor Red
}

# Test network connection
Write-Host "`n5. Network Connection Test:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 10 -UseBasicParsing
    Write-Host "Google connection: SUCCESS (Status: $($response.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "Google connection: FAILED - $($_.Exception.Message)" -ForegroundColor Red
}

# Check proxy software status
Write-Host "`n6. Proxy Software Status:" -ForegroundColor Yellow
try {
    $netstat = netstat -an | Select-String ":1087"
    if ($netstat) {
        Write-Host "Port 1087: ACTIVE" -ForegroundColor Green
        Write-Host "Connections found:" -ForegroundColor White
        $netstat | ForEach-Object { Write-Host "  $_" -ForegroundColor White }
    } else {
        Write-Host "Port 1087: INACTIVE" -ForegroundColor Red
        Write-Host "Make sure your proxy software is running" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Port check: ERROR - $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Check Complete ===" -ForegroundColor Cyan
Write-Host "If all items show GREEN, proxy is configured correctly" -ForegroundColor Green
Write-Host "If any items show RED, check those configurations" -ForegroundColor Red
