# Upload real photos from Downloads folder
param(
    [string]$ImagePath = "C:\Users\Administrator\Downloads",
    [string]$BaseUrl = "http://localhost:8080/api",
    [string]$Phone = "13800138000",
    [string]$Code = "123456",
    [int]$MaxPhotos = 3
)

Write-Host "===========================================`n" -ForegroundColor Cyan
Write-Host "Upload Photos from Downloads`n" -ForegroundColor Cyan

# 1. Check image files
Write-Host "Searching for image files in $ImagePath..." -ForegroundColor Yellow
$imageFiles = Get-ChildItem -Path $ImagePath -Include *.jpg,*.jpeg,*.png,*.gif -File -Recurse | Select-Object -First $MaxPhotos
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
    $sendCodeResponse = Invoke-RestMethod -Uri "$BaseUrl/auth/send-code?phone=$Phone" -Method Post -ErrorAction Stop
    if ($sendCodeResponse.success) {
        Write-Host "SUCCESS: Verification code sent" -ForegroundColor Green
    } else {
        Write-Host "FAILED: $($sendCodeResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. Login to get token
Write-Host "Step 2: Logging in..." -ForegroundColor Yellow
try {
    $loginUrl = "$BaseUrl/auth/login-with-code?phone=$Phone" + "&code=$Code"
    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method Post -ErrorAction Stop
    if (-not $loginResponse.success) {
        Write-Host "FAILED: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }

    $token = $loginResponse.data.token
    $userId = $loginResponse.data.userId
    Write-Host "SUCCESS: Logged in as user $userId" -ForegroundColor Green
} catch {
    Write-Host "FAILED: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 4. Upload photos
Write-Host "Step 3: Uploading photos...`n" -ForegroundColor Yellow

$uploadCount = 0
$failCount = 0
$firstPhoto = $true

foreach ($file in $imageFiles) {
    Write-Host "Uploading: $($file.Name)..." -ForegroundColor Cyan

    try {
        # Prepare multipart/form-data request
        $boundary = [System.Guid]::NewGuid().ToString()
        $LF = "`r`n"

        # Read file content
        $fileBytes = [System.IO.File]::ReadAllBytes($file.FullName)
        $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

        # Determine content type
        $contentType = "image/jpeg"
        if ($file.Extension -eq ".png") {
            $contentType = "image/png"
        } elseif ($file.Extension -eq ".gif") {
            $contentType = "image/gif"
        }

        # Build multipart body
        $bodyLines = @(
            "--$boundary",
            "Content-Disposition: form-data; name=`"photo`"; filename=`"$($file.Name)`"",
            "Content-Type: $contentType",
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
        } -ErrorAction Stop

        if ($response.success) {
            $photoId = $response.data.photoId
            $isAvatarText = if ($response.data.isAvatar) { " [SET AS AVATAR]" } else { "" }
            Write-Host "  SUCCESS - Photo ID: $photoId$isAvatarText" -ForegroundColor Green
            $uploadCount++
            $firstPhoto = $false
        } else {
            Write-Host "  FAILED: $($response.message)" -ForegroundColor Red
            $failCount++
        }
    } catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    Write-Host ""
}

# 5. Display results
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Upload Summary" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Success: $uploadCount photos" -ForegroundColor Green
Write-Host "Failed: $failCount photos" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""

# 6. Query user album
if ($uploadCount -gt 0) {
    Write-Host "Step 4: Verifying uploaded photos..." -ForegroundColor Yellow
    try {
        $photosResponse = Invoke-RestMethod -Uri "$BaseUrl/users/$userId/photos" -Method Get -Headers @{
            "Authorization" = "Bearer $token"
        } -ErrorAction Stop

        if ($photosResponse.success) {
            Write-Host "Total photos in album: $($photosResponse.data.Count)" -ForegroundColor Green
            foreach ($photo in $photosResponse.data) {
                $avatarMark = if ($photo.isAvatar) { " [AVATAR]" } else { "" }
                $uploadTime = if ($photo.uploadTime) { $photo.uploadTime } else { "N/A" }
                Write-Host "  - Photo ID: $($photo.id)$avatarMark" -ForegroundColor Gray
                Write-Host "    Upload time: $uploadTime" -ForegroundColor DarkGray
            }
        }
    } catch {
        Write-Host "Failed to query album: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Complete!" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Cyan
