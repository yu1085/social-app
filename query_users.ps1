# Query existing users
$baseUrl = "http://localhost:8080/api"

Write-Host "========================================"
Write-Host "  Query Database Users"
Write-Host "========================================"

Write-Host "`nQuerying user list..."
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users/search?page=0&size=100" -Method GET
    Write-Host "Success: Found $($response.data.Count) users"
    Write-Host "`nUser List:"
    Write-Host "----------------------------------------"
    foreach ($user in $response.data) {
        Write-Host "ID: $($user.id) | Nickname: $($user.nickname) | Phone: $($user.phone) | Gender: $($user.gender) | Location: $($user.location)"
    }
    Write-Host "----------------------------------------"

    # Check if users 23820514 and 23820515 exist
    $user14 = $response.data | Where-Object { $_.id -eq 23820514 }
    $user15 = $response.data | Where-Object { $_.id -eq 23820515 }

    Write-Host "`nTarget Users Status:"
    if ($user14) {
        Write-Host "User 23820514: EXISTS - $($user14.nickname)"
    } else {
        Write-Host "User 23820514: NOT FOUND"
    }

    if ($user15) {
        Write-Host "User 23820515: EXISTS - $($user15.nickname)"
    } else {
        Write-Host "User 23820515: NOT FOUND"
    }

} catch {
    Write-Host "Failed: $_"
}

Write-Host "`n========================================"
Write-Host "  Query Complete"
Write-Host "========================================"

