# Upload photos to user album
param(
    [string]$ImagePath = "C:\Users\Administrator\Downloads\test_photos",
    [string]$BaseUrl = "http://localhost:8080/api",
    [string]$Phone = "13800138000",
    [string]$Code = "123456"
)

Write-Host "===========================================`n" -ForegroundColor Cyan
Write-Host "Batch Upload Photos Tool`n" -ForegroundColor Cyan

# 1. Check image files
Write-Host "Checking for image files..." -ForegroundColor Yellow
$imageFiles = Get-ChildItem -Path $ImagePath -Include *.jpg,*.jpeg,*.png,*.gif -Recurse -File | Select-Object -First 5
if ($imageFiles.Count -eq 0) {
    Write-Host "Error: No image files found in $ImagePath" -ForegroundColor Red
    exit 1
}

Write-Host "Found $($imageFiles.Count) image files:`n" -ForegroundColor Green
foreach ($file in $imageFiles) {
    $sizeKB = [math]::Round($file.Length / 1KB, 2)
    Write-Host "  - $($file.Name) - $sizeKB KB" -ForegroundColor Gray
}
Write-Host ""

# 2. Send verification code
Write-Host "Step 1: Sending verification code..." -ForegroundColor Yellow
try {
    $sendCodeResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/send-code?phone=$Phone" -Method Post
    if ($sendCodeResponse.success) {
        Write-Host "Verification code sent: $($sendCodeResponse.message)" -ForegroundColor Green
    } else {
        Write-Host "Failed to send code: $($sendCodeResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Failed to send code: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. Login to get token
Write-Host "Step 2: Logging in to get token..." -ForegroundColor Yellow
try {
    $loginUrl = "$BaseUrl/auth/login-with-code?phone=$Phone" + "&code=$Code"
    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method Post
    if (-not $loginResponse.success) {
        Write-Host "Login failed: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }

    $token = $loginResponse.data.token
    $userId = $loginResponse.data.userId
    Write-Host "Login successful - userId: $userId" -ForegroundColor Green
} catch {
    Write-Host "Login failed: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 4. Upload photos
Write-Host "Step 3: Starting photo upload...`n" -ForegroundColor Yellow

$uploadCount = 0
$failCount = 0
$firstPhoto = $true

foreach ($file in $imageFiles) {
    Write-Host "Uploading: $($file.Name)" -ForegroundColor Cyan

    try {
        # Prepare multipart/form-data request
        $boundary = [System.Guid]::NewGuid().ToString()
        $LF = "`r`n"

        # Read file content
        $fileBytes = [System.IO.File]::ReadAllBytes($file.FullName)
        $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

        # Build multipart body
        $bodyLines = @(
            "--$boundary",
            "Content-Disposition: form-data; name=`"photo`"; filename=`"$($file.Name)`"",
            "Content-Type: image/jpeg",
            "",
            $fileEnc,
            "--$boundary--"
        ) -join $LF

        # Set as avatar (first photo only)
        $isAvatar = if ($firstPhoto) { "true" } else { "false" }

        # Send POST request
        $uri = "$BaseUrl/users/$userId/photos?isAvatar=$isAvatar"
        $response = Invoke-RestMethod -Uri $uri -Method Post -Body $bodyLines -ContentType "multipart/form-data; boundary=$boundary" -Headers @{
            "Authorization" = "Bearer $token"
        }

        if ($response.success) {
            $photoId = $response.data.photoId
            $photoUrl = $response.data.photoUrl
            $isAvatarText = if ($response.data.isAvatar) { " (set as avatar)" } else { "" }
            Write-Host "  SUCCESS - photoId: $photoId$isAvatarText" -ForegroundColor Green
            Write-Host "    URL: $photoUrl" -ForegroundColor Gray
            $uploadCount++
            $firstPhoto = $false
        } else {
            Write-Host "  FAILED: $($response.message)" -ForegroundColor Red
            $failCount++
        }
    } catch {
        Write-Host "  FAILED: $_" -ForegroundColor Red
        $failCount++
    }

    Write-Host ""
}

# 5. Display upload results
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Upload Complete" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Success: $uploadCount photos" -ForegroundColor Green
Write-Host "Failed: $failCount photos" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""

# 6. Query user album
Write-Host "Step 4: Querying user album..." -ForegroundColor Yellow
try {
    $photosResponse = Invoke-RestMethod -Uri "$BaseUrl/users/$userId/photos" -Method Get -Headers @{
        "Authorization" = "Bearer $token"
    }

    if ($photosResponse.success) {
        Write-Host "Total photos in album: $($photosResponse.data.Count)" -ForegroundColor Green
        foreach ($photo in $photosResponse.data) {
            $avatarText = if ($photo.isAvatar) { " [AVATAR]" } else { "" }
            Write-Host "  - ID: $($photo.id) | $($photo.photoUrl)$avatarText" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "Failed to query album: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Done!" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
