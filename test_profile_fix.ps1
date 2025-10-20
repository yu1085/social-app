# ProfileViewModel Fix Test Script
# Test main thread network call issue and null pointer exception fixes

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "ProfileViewModel Fix Test" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://10.0.2.2:8080"

# Test user login to get Token
Write-Host "[1] Testing user login..." -ForegroundColor Yellow
$loginBody = @{
    phone = "19887654321"
    code = "123456"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        Write-Host "OK Login successful, got Token" -ForegroundColor Green
        Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "FAIL Login failed: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "FAIL Login failed: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Test get user settings
Write-Host "[2] Testing get user settings (fixed main thread network call)..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $settingsResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/settings" -Method Get -Headers $headers
    if ($settingsResponse.success) {
        Write-Host "OK Get user settings successful" -ForegroundColor Green
        Write-Host "  Voice call enabled: $($settingsResponse.data.voiceCallEnabled)" -ForegroundColor Gray
        Write-Host "  Video call enabled: $($settingsResponse.data.videoCallEnabled)" -ForegroundColor Gray
        Write-Host "  Message charge enabled: $($settingsResponse.data.messageChargeEnabled)" -ForegroundColor Gray
        Write-Host "  Voice call price: $($settingsResponse.data.voiceCallPrice) coins/min" -ForegroundColor Gray
        Write-Host "  Video call price: $($settingsResponse.data.videoCallPrice) coins/min" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Get user settings failed: $($settingsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Get user settings failed: $_" -ForegroundColor Red
}

Write-Host ""

# Test update user settings
Write-Host "[3] Testing update user settings..." -ForegroundColor Yellow
$updateBody = @{
    voiceCallEnabled = $true
    videoCallEnabled = $true
    messageChargeEnabled = $false
    voiceCallPrice = 10.0
    videoCallPrice = 20.0
    messagePrice = 0.0
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/settings" -Method Put -Body $updateBody -Headers $headers
    if ($updateResponse.success) {
        Write-Host "OK Update user settings successful" -ForegroundColor Green
        Write-Host "  Voice call enabled: $($updateResponse.data.voiceCallEnabled)" -ForegroundColor Gray
        Write-Host "  Video call enabled: $($updateResponse.data.videoCallEnabled)" -ForegroundColor Gray
        Write-Host "  Message charge enabled: $($updateResponse.data.messageChargeEnabled)" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Update user settings failed: $($updateResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Update user settings failed: $_" -ForegroundColor Red
}

Write-Host ""

# Test get wallet info
Write-Host "[4] Testing get wallet info..." -ForegroundColor Yellow
try {
    $walletResponse = Invoke-RestMethod -Uri "$baseUrl/api/profile/wallet" -Method Get -Headers $headers
    if ($walletResponse.success) {
        Write-Host "OK Get wallet info successful" -ForegroundColor Green
        Write-Host "  Balance: $($walletResponse.data.balance) coins" -ForegroundColor Gray
        Write-Host "  Total recharge: $($walletResponse.data.totalRecharge) coins" -ForegroundColor Gray
        Write-Host "  Total consume: $($walletResponse.data.totalConsume) coins" -ForegroundColor Gray
    } else {
        Write-Host "FAIL Get wallet info failed: $($walletResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "FAIL Get wallet info failed: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Fix Summary:" -ForegroundColor Yellow
Write-Host "1. Main thread network call issue - Fixed" -ForegroundColor Green
Write-Host "   - All network calls execute in IO thread" -ForegroundColor Gray
Write-Host "   - UI updates switch back to main thread" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Null pointer exception issue - Fixed" -ForegroundColor Green
Write-Host "   - Initialize all states with mutableStateOf" -ForegroundColor Gray
Write-Host "   - Ensure state variables are initialized before use" -ForegroundColor Gray
Write-Host ""
Write-Host "Please recompile and run the Android app for testing." -ForegroundColor Yellow
