# 检查后端日志中的JPush推送记录
Write-Host "=== 查找最近的消息发送和推送记录 ===" -ForegroundColor Green

# 查找logs目录
$logFiles = @(
    "backend\logs\application.log",
    "backend\logs\spring.log",
    "logs\application.log",
    "logs\spring.log"
)

foreach ($logFile in $logFiles) {
    if (Test-Path $logFile) {
        Write-Host "`n找到日志文件: $logFile" -ForegroundColor Yellow

        # 查找消息发送相关日志
        Write-Host "`n--- 消息发送记录 (最近20条) ---" -ForegroundColor Cyan
        Get-Content $logFile | Select-String "发送消息|sendMessage|消息推送|sendNotification" | Select-Object -Last 20

        Write-Host "`n--- JPush推送相关日志 ---" -ForegroundColor Cyan
        Get-Content $logFile | Select-String "JPush|推送成功|推送失败|PushSendResult" | Select-Object -Last 20
    }
}

Write-Host "`n=== 如果未找到日志，检查控制台输出 ===" -ForegroundColor Yellow
