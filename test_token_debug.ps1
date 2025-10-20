# 测试前端Token获取和发送
Write-Host "=== 测试前端Token获取和发送 ==="

# 1. 先登录获取Token
Write-Host "1. 发送验证码..."
$sendCodeResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/send-code" -Method POST -Body "phone=13800138000" -ContentType "application/x-www-form-urlencoded"
Write-Host "验证码发送结果: $($sendCodeResponse.Content)"

Start-Sleep -Seconds 2

Write-Host "2. 使用验证码登录..."
$loginBody = "phone=13800138000&code=123456"
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login-with-code" -Method POST -Body $loginBody -ContentType "application/x-www-form-urlencoded"
$loginJson = $loginResponse.Content | ConvertFrom-Json

if ($loginJson.success) {
    $token = $loginJson.data.token
    Write-Host "登录成功！"
    Write-Host "完整Token: $token"
    Write-Host "Token长度: $($token.Length)"
    $dotCount = ($token.ToCharArray() | Where-Object { $_ -eq '.' }).Count
    Write-Host "Token包含点数量: $dotCount"
    
    # 3. 测试个人资料API
    Write-Host "3. 测试个人资料API..."
    $profileResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers @{"Authorization"="Bearer $token"}
    Write-Host "个人资料API响应: $($profileResponse.Content)"
} else {
    Write-Host "登录失败: $($loginJson.message)"
}