# Test new relationship APIs
$baseUrl = "http://localhost:8080/api"

Write-Host "========================================"
Write-Host "  Test New Relationship APIs"
Write-Host "========================================"

# Wait for backend to start
Write-Host "`nWaiting for backend to start..."
Start-Sleep -Seconds 15

# Test 1: Get recommended users
Write-Host "`n[Test 1] Get recommended users (size=4)..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/recommended?size=4" -Method GET
    Write-Host "Success: Found $($response.data.Count) recommended users" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "  - ID: $($user.id), Nickname: $($user.nickname), Gender: $($user.gender)"
    }
} catch {
    Write-Host "Failed: $_" -ForegroundColor Red
}

# Test 2: Get acquaintances
Write-Host "`n[Test 2] Get acquaintances list..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/acquaintances?size=5" -Method GET
    Write-Host "Success: Found $($response.data.Count) acquaintances" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "  - ID: $($user.id), Nickname: $($user.nickname)"
    }
} catch {
    Write-Host "Failed: $_" -ForegroundColor Red
}

# Test 3: Get likes list
Write-Host "`n[Test 3] Get likes list..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/likes?size=5" -Method GET
    Write-Host "Success: Found $($response.data.Count) likes" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "  - ID: $($user.id), Nickname: $($user.nickname), Gender: $($user.gender)"
    }
} catch {
    Write-Host "Failed: $_" -ForegroundColor Red
}

# Test 4: Get intimate list
Write-Host "`n[Test 4] Get intimate list..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/intimate?size=5" -Method GET
    Write-Host "Success: Found $($response.data.Count) intimate users" -ForegroundColor Green
    foreach ($user in $response.data) {
        Write-Host "  - ID: $($user.id), Nickname: $($user.nickname)"
    }
} catch {
    Write-Host "Failed: $_" -ForegroundColor Red
}

Write-Host "`n========================================"
Write-Host "  Testing Complete"
Write-Host "========================================"
