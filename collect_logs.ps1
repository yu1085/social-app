# 日志收集脚本
# 自动收集Android logcat和后端日志

$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$logDir = "logs_$timestamp"
New-Item -ItemType Directory -Path $logDir -Force

Write-Host "================================" -ForegroundColor Cyan
Write-Host "开始收集日志..." -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# 1. 收集Android Logcat
Write-Host "1. 收集Android Logcat (最近1000行)..." -ForegroundColor Yellow
try {
    adb logcat -d -t 1000 > "$logDir/android_logcat.txt"
    Write-Host "✅ Android Logcat已保存到: $logDir/android_logcat.txt" -ForegroundColor Green
} catch {
    Write-Host "❌ 收集Android Logcat失败: $_" -ForegroundColor Red
}
Write-Host ""

# 2. 过滤ChatActivity相关日志
Write-Host "2. 过滤ChatActivity相关日志..." -ForegroundColor Yellow
try {
    adb logcat -d -t 1000 | Select-String "ChatActivity|JPushReceiver|AuthManager" > "$logDir/chat_filtered.txt"
    Write-Host "✅ ChatActivity日志已保存到: $logDir/chat_filtered.txt" -ForegroundColor Green
} catch {
    Write-Host "❌ 过滤日志失败: $_" -ForegroundColor Red
}
Write-Host ""

# 3. 显示最近的关键日志
Write-Host "3. 显示最近的关键日志:" -ForegroundColor Yellow
Write-Host "--- ChatActivity ---" -ForegroundColor Cyan
adb logcat -d -t 100 | Select-String "ChatActivity" | Select-Object -Last 10
Write-Host ""
Write-Host "--- JPushReceiver ---" -ForegroundColor Cyan
adb logcat -d -t 100 | Select-String "JPushReceiver" | Select-Object -Last 10
Write-Host ""

Write-Host "================================" -ForegroundColor Cyan
Write-Host "日志收集完成！保存在: $logDir" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
