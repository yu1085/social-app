# 需要以管理员身份运行PowerShell
Write-Host "正在将ADB添加到系统PATH中..." -ForegroundColor Green

# 获取ADB路径
$adbPath = "$env:USERPROFILE\AppData\Local\Android\Sdk\platform-tools"

# 检查ADB是否存在
if (-not (Test-Path "$adbPath\adb.exe")) {
    Write-Host "错误：在 $adbPath 中找不到adb.exe" -ForegroundColor Red
    Write-Host "请确保Android SDK已正确安装" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

# 获取当前系统PATH
$currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")

# 检查是否已经存在
if ($currentPath -like "*$adbPath*") {
    Write-Host "ADB路径已经存在于系统PATH中" -ForegroundColor Yellow
} else {
    # 添加到系统PATH
    $newPath = $currentPath + ";" + $adbPath
    [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
    Write-Host "ADB路径已成功添加到系统PATH中" -ForegroundColor Green
}

Write-Host ""
Write-Host "注意：您需要重新启动命令提示符或PowerShell才能生效" -ForegroundColor Cyan
Write-Host "或者运行以下命令刷新环境变量：" -ForegroundColor Cyan
Write-Host "refreshenv" -ForegroundColor White
Write-Host ""
Read-Host "按任意键退出"
