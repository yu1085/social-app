# 钱包充值测试脚本
Write-Host "=== 钱包充值测试 ===" -ForegroundColor Green

# 1. 获取验证码
Write-Host "1. 获取验证码..." -ForegroundColor Yellow
$phone = "13800138000"
$codeResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/send-code?phone=$phone" -Method POST
$codeJson = $codeResponse.Content | ConvertFrom-Json
$code = $codeJson.data
Write-Host "验证码: $code" -ForegroundColor Cyan

# 2. 登录获取token
Write-Host "2. 登录获取token..." -ForegroundColor Yellow
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login-with-code?phone=$phone&code=$code" -Method POST
$loginJson = $loginResponse.Content | ConvertFrom-Json
$token = $loginJson.data.token
Write-Host "Token: $token" -ForegroundColor Cyan

# 3. 查看当前余额
Write-Host "3. 查看当前余额..." -ForegroundColor Yellow
$balanceResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/wallet/balance" -Headers @{"Authorization"="Bearer $token"}
$balanceJson = $balanceResponse.Content | ConvertFrom-Json
$currentBalance = $balanceJson.data.balance
Write-Host "当前余额: $currentBalance" -ForegroundColor Cyan

# 4. 执行充值
Write-Host "4. 执行充值..." -ForegroundColor Yellow
$rechargeAmount = "50.00"
$rechargeResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/wallet/recharge?amount=$rechargeAmount&description=测试充值" -Method POST -Headers @{"Authorization"="Bearer $token"}
$rechargeJson = $rechargeResponse.Content | ConvertFrom-Json
Write-Host "充值结果: $($rechargeJson.message)" -ForegroundColor Cyan

# 5. 查看充值后余额
Write-Host "5. 查看充值后余额..." -ForegroundColor Yellow
$newBalanceResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/wallet/balance" -Headers @{"Authorization"="Bearer $token"}
$newBalanceJson = $newBalanceResponse.Content | ConvertFrom-Json
$newBalance = $newBalanceJson.data.balance
Write-Host "充值后余额: $newBalance" -ForegroundColor Cyan

# 6. 查看交易记录
Write-Host "6. 查看交易记录..." -ForegroundColor Yellow
$transactionsResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/wallet/transactions" -Headers @{"Authorization"="Bearer $token"}
$transactionsJson = $transactionsResponse.Content | ConvertFrom-Json
Write-Host "交易记录数量: $($transactionsJson.data.content.Count)" -ForegroundColor Cyan

Write-Host "=== 测试完成 ===" -ForegroundColor Green
