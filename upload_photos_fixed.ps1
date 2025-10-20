# Fixed photo upload script
param(
    [string]$BaseUrl = "http://localhost:8080/api",
    [string]$Phone = "13800138000",
    [string]$Code = "123456"
)

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Photo Upload Tool (Fixed)" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Send verification code
Write-Host "Step 1: Sending verification code..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/auth/send-code?phone=$Phone" -Method Post -UseBasicParsing
    $result = $response.Content | ConvertFrom-Json
    if ($result.success) {
        Write-Host "  SUCCESS: Code sent" -ForegroundColor Green
    } else {
        Write-Host "  FAILED: $($result.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 2. Login
Write-Host "Step 2: Logging in..." -ForegroundColor Yellow
try {
    $loginUrl = "$BaseUrl/auth/login-with-code?phone=$Phone&code=$Code"
    $response = Invoke-WebRequest -Uri $loginUrl -Method Post -UseBasicParsing
    $result = $response.Content | ConvertFrom-Json

    if ($result.success -and $result.data) {
        $token = $result.data.token
        $userId = $result.data.userId
        Write-Host "  SUCCESS: Logged in as user ID $userId" -ForegroundColor Green
        Write-Host "  Token: $($token.Substring(0, 20))..." -ForegroundColor Gray
    } else {
        Write-Host "  FAILED: $($result.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 3. Find image files
Write-Host "Step 3: Finding image files..." -ForegroundColor Yellow
$imagePath = "C:\Users\Administrator\Downloads"
$imageFiles = Get-ChildItem -Path $imagePath -Include *.jpg,*.jpeg,*.png,*.gif -File -ErrorAction SilentlyContinue | Select-Object -First 3

if ($imageFiles.Count -eq 0) {
    Write-Host "  No images found in $imagePath" -ForegroundColor Red
    Write-Host "  Creating test images instead..." -ForegroundColor Yellow

    # Create test directory
    $testPath = "C:\Users\Administrator\Downloads\test_photos"
    if (-not (Test-Path $testPath)) {
        New-Item -ItemType Directory -Path $testPath -Force | Out-Null
    }

    # Create simple test files (text files as placeholders)
    for ($i = 1; $i -le 3; $i++) {
        $filePath = Join-Path $testPath "test_photo_$i.txt"
        "Test photo $i for user $userId" | Out-File -FilePath $filePath -Encoding UTF8
    }

    Write-Host "  Created 3 test files in $testPath" -ForegroundColor Yellow
    Write-Host "  Note: Real photo upload requires actual image files" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "=========================================" -ForegroundColor Cyan
    Write-Host "User ID for photo upload: $userId" -ForegroundColor Green
    Write-Host "Phone: $Phone" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 30))..." -ForegroundColor Gray
    Write-Host "=========================================" -ForegroundColor Cyan
    exit 0
}

Write-Host "  Found $($imageFiles.Count) image files" -ForegroundColor Green
foreach ($file in $imageFiles) {
    $sizeKB = [math]::Round($file.Length / 1KB, 2)
    Write-Host "    - $($file.Name) ($sizeKB KB)" -ForegroundColor Gray
}
Write-Host ""

# 4. Upload photos
Write-Host "Step 4: Uploading photos..." -ForegroundColor Yellow
Write-Host ""

$uploadCount = 0
$failCount = 0
$firstPhoto = $true

foreach ($file in $imageFiles) {
    Write-Host "  Uploading: $($file.Name)" -ForegroundColor Cyan

    try {
        # Prepare form data
        $boundary = [System.Guid]::NewGuid().ToString()
        $LF = "`r`n"

        # Read file
        $fileBytes = [System.IO.File]::ReadAllBytes($file.FullName)
        $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

        # Content type
        $contentType = "image/jpeg"
        if ($file.Extension -eq ".png") { $contentType = "image/png" }
        elseif ($file.Extension -eq ".gif") { $contentType = "image/gif" }

        # Build multipart body
        $bodyLines = @(
            "--$boundary",
            "Content-Disposition: form-data; name=`"photo`"; filename=`"$($file.Name)`"",
            "Content-Type: $contentType",
            "",
            $fileEnc,
            "--$boundary--"
        ) -join $LF

        # Upload
        $isAvatar = if ($firstPhoto) { "true" } else { "false" }
        $uri = "$BaseUrl/users/$userId/photos?isAvatar=$isAvatar"

        Write-Host "    URL: $uri" -ForegroundColor DarkGray

        $response = Invoke-WebRequest -Uri $uri -Method Post -Body $bodyLines `
            -ContentType "multipart/form-data; boundary=$boundary" `
            -Headers @{ "Authorization" = "Bearer $token" } `
            -UseBasicParsing

        $result = $response.Content | ConvertFrom-Json

        if ($result.success) {
            $photoId = $result.data.photoId
            $isAvatarText = if ($result.data.isAvatar) { " [AVATAR]" } else { "" }
            Write-Host "    SUCCESS - Photo ID: $photoId$isAvatarText" -ForegroundColor Green
            $uploadCount++
            $firstPhoto = $false
        } else {
            Write-Host "    FAILED: $($result.message)" -ForegroundColor Red
            $failCount++
        }
    } catch {
        Write-Host "    FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
    Write-Host ""
}

# 5. Summary
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Upload Summary" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "User ID: $userId" -ForegroundColor White
Write-Host "Phone: $Phone" -ForegroundColor White
Write-Host "Success: $uploadCount photos" -ForegroundColor Green
Write-Host "Failed: $failCount photos" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""

# 6. Verify
if ($uploadCount -gt 0) {
    Write-Host "Step 5: Verifying uploaded photos..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri "$BaseUrl/users/$userId/photos" -Method Get `
            -Headers @{ "Authorization" = "Bearer $token" } -UseBasicParsing
        $result = $response.Content | ConvertFrom-Json

        if ($result.success) {
            Write-Host "  Total photos in album: $($result.data.Count)" -ForegroundColor Green
            foreach ($photo in $result.data) {
                $avatarMark = if ($photo.isAvatar) { " [AVATAR]" } else { "" }
                Write-Host "    - Photo ID: $($photo.id)$avatarMark" -ForegroundColor Gray
                Write-Host "      URL: $($photo.photoUrl)" -ForegroundColor DarkGray
            }
        }
    } catch {
        Write-Host "  Verification failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan
