# Test Like Feature
$baseUrl = "http://localhost:8080/api"

Write-Host "========================================"
Write-Host "Testing Like Feature"
Write-Host "========================================"
Write-Host ""

# 1. Login
Write-Host "1. Login user 23820512..."
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login-with-code?phone=13800138001&code=123456" -Method POST
$token = $loginResponse.data.token
$userId = $loginResponse.data.user.id
Write-Host "   Success - userId: $userId"
Write-Host ""

# 2. Check like status
Write-Host "2. Check if user 23820513 is liked..."
$targetUserId = 23820513
$headers = @{ "Authorization" = "Bearer $token" }
$isLikedResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
Write-Host "   Liked: $($isLikedResponse.data)"
Write-Host ""

# 3. Add like
Write-Host "3. Add like for user 23820513..."
$addLikeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method POST -Headers $headers
Write-Host "   $($addLikeResponse.message)"
Write-Host ""

# 4. Verify like status
Write-Host "4. Verify like status..."
$isLikedResponse2 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
Write-Host "   Liked: $($isLikedResponse2.data)"
Write-Host ""

# 5. View likes list
Write-Host "5. View likes list..."
$likesListResponse = Invoke-RestMethod -Uri "$baseUrl/users/likes?size=10" -Method GET -Headers $headers
Write-Host "   Total: $($likesListResponse.data.Count) users"
foreach ($user in $likesListResponse.data) {
    Write-Host "   - ID: $($user.id), Name: $($user.nickname)"
}
Write-Host ""

# 6. Remove like
Write-Host "6. Remove like..."
$removeLikeResponse = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/like" -Method DELETE -Headers $headers
Write-Host "   $($removeLikeResponse.message)"
Write-Host ""

# 7. Final verification
Write-Host "7. Final verification..."
$isLikedResponse3 = Invoke-RestMethod -Uri "$baseUrl/users/$targetUserId/is-liked" -Method GET -Headers $headers
Write-Host "   Liked: $($isLikedResponse3.data)"
Write-Host ""

Write-Host "========================================"
Write-Host "Test Complete!"
Write-Host "========================================"
