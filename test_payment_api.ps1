# 测试支付API
$baseUrl = "http://localhost:8080"

# 先登录获取Token
Write-Host "正在登录..." -ForegroundColor Yellow
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login-with-code?phone=19887654321&code=123456" -Method Post
$token = $loginResponse.data.token
Write-Host "登录成功，Token: $($token.Substring(0, 20))..." -ForegroundColor Green

# 测试支付API
Write-Host "测试支付API..." -ForegroundColor Yellow
$paymentBody = @{
    amount = 12
    coins = 1200
    description = "充值1200金币"
    packageId = "package_1200"
    paymentMethod = "alipay"
} | ConvertTo-Json

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $paymentResponse = Invoke-RestMethod -Uri "$baseUrl/api/payment/alipay/create" -Method Post -Body $paymentBody -Headers $headers
    Write-Host "支付API测试成功!" -ForegroundColor Green
    Write-Host "响应: $($paymentResponse | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
} catch {
    Write-Host "支付API测试失败: $_" -ForegroundColor Red
    Write-Host "错误详情: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}
