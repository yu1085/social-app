# Create missing users via API
$baseUrl = "http://localhost:8080/api"

Write-Host "========================================"
Write-Host "  Create Missing Users via API"
Write-Host "========================================"

# Wait for backend to be ready
Write-Host "`nWaiting for backend to start..."
Start-Sleep -Seconds 2

# User 1: 23820514 - phone 13900003333
Write-Host "`n[1] Registering user with phone 13900003333..."
try {
    # Send verification code
    $url1 = "$baseUrl/auth/send-code?phone=13900003333"
    $response1 = Invoke-RestMethod -Uri $url1 -Method POST
    Write-Host "Verification code sent"

    # Login
    $url2 = "$baseUrl/auth/login-with-code?phone=13900003333``&code=123456"
    $response2 = Invoke-RestMethod -Uri $url2 -Method POST
    Write-Host "User registered - ID: $($response2.data.user.id)"
} catch {
    Write-Host "Failed: $_"
}

# User 2: 23820515 - phone 13900004444
Write-Host "`n[2] Registering user with phone 13900004444..."
try {
    # Send verification code
    $url1 = "$baseUrl/auth/send-code?phone=13900004444"
    $response1 = Invoke-RestMethod -Uri $url1 -Method POST
    Write-Host "Verification code sent"

    # Login
    $url2 = "$baseUrl/auth/login-with-code?phone=13900004444``&code=123456"
    $response2 = Invoke-RestMethod -Uri $url2 -Method POST
    Write-Host "User registered - ID: $($response2.data.user.id)"
} catch {
    Write-Host "Failed: $_"
}

Write-Host "`n========================================"
Write-Host "  User Creation Complete"
Write-Host "========================================"
