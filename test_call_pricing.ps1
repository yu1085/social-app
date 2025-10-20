# Call Pricing Test Script
# Tests the complete call pricing functionality

$baseUrl = "http://localhost:8080/api"
$phone1 = "18888888888"
$phone2 = "16666666666"
$code = "123456"

Write-Host "=== Call Pricing Test ===" -ForegroundColor Cyan
Write-Host ""

# 1. Login user 1
Write-Host "1. Login user 1..." -ForegroundColor Yellow
$body1 = @{ phone = $phone1; verificationCode = $code } | ConvertTo-Json
$login1 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $body1 -ContentType "application/json"
$token1 = $login1.data.token
$userId1 = $login1.data.userId
Write-Host "User 1 ID: $userId1" -ForegroundColor Green

# 2. Login user 2
Write-Host "2. Login user 2..." -ForegroundColor Yellow
$body2 = @{ phone = $phone2; verificationCode = $code } | ConvertTo-Json
$login2 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $body2 -ContentType "application/json"
$token2 = $login2.data.token
$userId2 = $login2.data.userId
Write-Host "User 2 ID: $userId2" -ForegroundColor Green

# 3. Set user 2 prices
Write-Host "3. Set user 2 call prices..." -ForegroundColor Yellow
$headers2 = @{ "Authorization" = "Bearer $token2"; "Content-Type" = "application/json" }
$priceBody = @{
    videoCallEnabled = $true
    voiceCallEnabled = $true
    videoCallPrice = 200.0
    voiceCallPrice = 100.0
} | ConvertTo-Json
$priceResp = Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $priceBody
Write-Host "Video: $($priceResp.data.videoCallPrice), Voice: $($priceResp.data.voiceCallPrice)" -ForegroundColor Green

# 4. Check user 1 balance
Write-Host "4. Check user 1 initial balance..." -ForegroundColor Yellow
$headers1 = @{ "Authorization" = "Bearer $token1"; "Content-Type" = "application/json" }
$wallet1 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers1
$balance1Before = $wallet1.data.balance
Write-Host "User 1 balance: $balance1Before" -ForegroundColor Green

# 5. Check user 2 balance
Write-Host "5. Check user 2 initial balance..." -ForegroundColor Yellow
$wallet2 = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers2
$balance2Before = $wallet2.data.balance
Write-Host "User 2 balance: $balance2Before" -ForegroundColor Green

# 6. Get user 2 prices
Write-Host "6. Get user 2 call prices..." -ForegroundColor Yellow
$prices = Invoke-RestMethod -Uri "$baseUrl/call/prices/$userId2" -Method Get -Headers $headers1
Write-Host "Video: $($prices.data.videoCallPrice), Enabled: $($prices.data.videoCallEnabled)" -ForegroundColor Green

# 7. Initiate call
Write-Host "7. User 1 initiates video call..." -ForegroundColor Yellow
$callBody = @{ receiverId = $userId2; callType = "VIDEO" } | ConvertTo-Json
$callResp = Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers1 -Body $callBody
$sessionId = $callResp.data.callSessionId
$price = $callResp.data.pricePerMinute
Write-Host "Session: $sessionId, Price: $price" -ForegroundColor Green

# 8. Accept call
Write-Host "8. User 2 accepts call..." -ForegroundColor Yellow
$acceptBody = @{ callSessionId = $sessionId } | ConvertTo-Json
$acceptResp = Invoke-RestMethod -Uri "$baseUrl/call/accept" -Method Post -Headers $headers2 -Body $acceptBody
Write-Host "Status: $($acceptResp.data.status)" -ForegroundColor Green

# 9. Wait 3 seconds
Write-Host "9. Call in progress (3 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
Write-Host "Done" -ForegroundColor Green

# 10. End call
Write-Host "10. User 1 ends call..." -ForegroundColor Yellow
$endBody = @{ callSessionId = $sessionId } | ConvertTo-Json
$endResp = Invoke-RestMethod -Uri "$baseUrl/call/end" -Method Post -Headers $headers1 -Body $endBody
$duration = $endResp.data.durationSeconds
$cost = $endResp.data.totalCost
Write-Host "Duration: $duration seconds, Cost: $cost" -ForegroundColor Green

# 11. Check user 1 final balance
Write-Host "11. Check user 1 final balance..." -ForegroundColor Yellow
$wallet1Final = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers1
$balance1After = $wallet1Final.data.balance
Write-Host "Before: $balance1Before, After: $balance1After, Charged: $cost" -ForegroundColor Green
$expectedBalance1 = $balance1Before - $cost
Write-Host "Expected: $expectedBalance1, Actual: $balance1After" -ForegroundColor $(if ([Math]::Abs($balance1After - $expectedBalance1) -lt 0.01) { "Green" } else { "Red" })

# 12. Check user 2 final balance
Write-Host "12. Check user 2 final balance..." -ForegroundColor Yellow
$wallet2Final = Invoke-RestMethod -Uri "$baseUrl/profile/wallet" -Method Get -Headers $headers2
$balance2After = $wallet2Final.data.balance
Write-Host "Before: $balance2Before, After: $balance2After, Earned: $cost" -ForegroundColor Green
$expectedBalance2 = $balance2Before + $cost
Write-Host "Expected: $expectedBalance2, Actual: $balance2After" -ForegroundColor $(if ([Math]::Abs($balance2After - $expectedBalance2) -lt 0.01) { "Green" } else { "Red" })

# 13. Test insufficient balance
Write-Host "13. Test insufficient balance..." -ForegroundColor Yellow
$phone3 = "13333333333"
$body3 = @{ phone = $phone3; verificationCode = $code } | ConvertTo-Json
$login3 = Invoke-RestMethod -Uri "$baseUrl/auth/verify-code" -Method Post -Body $body3 -ContentType "application/json"
$token3 = $login3.data.token
$headers3 = @{ "Authorization" = "Bearer $token3"; "Content-Type" = "application/json" }
$callBody3 = @{ receiverId = $userId2; callType = "VIDEO" } | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers3 -Body $callBody3
    Write-Host "FAIL: Should reject insufficient balance" -ForegroundColor Red
} catch {
    Write-Host "PASS: Correctly rejected insufficient balance" -ForegroundColor Green
}

# 14. Test disabled call
Write-Host "14. Test disabled video call..." -ForegroundColor Yellow
$disableBody = @{ videoCallEnabled = $false } | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $disableBody | Out-Null
try {
    Invoke-RestMethod -Uri "$baseUrl/call/initiate" -Method Post -Headers $headers1 -Body $callBody
    Write-Host "FAIL: Should reject disabled call" -ForegroundColor Red
} catch {
    Write-Host "PASS: Correctly rejected disabled call" -ForegroundColor Green
}

# Restore settings
$enableBody = @{ videoCallEnabled = $true } | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/profile/settings" -Method Put -Headers $headers2 -Body $enableBody | Out-Null

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan
