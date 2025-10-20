# Download images and upload to database
$downloadPath = "C:\Users\Administrator\Downloads"
$baseUrl = "http://localhost:8080/api"
$phone = "13800138000"
$code = "123456"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Download & Upload Photos to Database" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Download test images
Write-Host "Step 1: Downloading test images..." -ForegroundColor Yellow

$imageUrls = @(
    "https://picsum.photos/400/600?random=1",
    "https://picsum.photos/400/600?random=2",
    "https://picsum.photos/400/600?random=3"
)

$downloadedFiles = @()
$imageIndex = 1

foreach ($url in $imageUrls) {
    $fileName = "test_photo_$imageIndex.jpg"
    $filePath = Join-Path $downloadPath $fileName

    try {
        Write-Host "  Downloading: $fileName..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $url -OutFile $filePath -UseBasicParsing -TimeoutSec 30

        if (Test-Path $filePath) {
            $sizeKB = [math]::Round((Get-Item $filePath).Length / 1KB, 2)
            Write-Host "    SUCCESS - $sizeKB KB" -ForegroundColor Green
            $downloadedFiles += Get-Item $filePath
        }
    } catch {
        Write-Host "    FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }

    $imageIndex++
}

Write-Host ""
Write-Host "Downloaded $($downloadedFiles.Count) images" -ForegroundColor Green
Write-Host ""

if ($downloadedFiles.Count -eq 0) {
    Write-Host "No images downloaded. Exiting." -ForegroundColor Red
    exit 1
}

# Step 2: Send verification code
Write-Host "Step 2: Sending verification code..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/send-code?phone=$phone" -Method Post
    if ($response.success) {
        Write-Host "  SUCCESS" -ForegroundColor Green
    }
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 3: Login
Write-Host "Step 3: Logging in..." -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login-with-code?phone=$phone&code=$code" -Method Post

    if ($loginResponse.success) {
        $token = $loginResponse.data.token
        $userId = $loginResponse.data.user.id
        $username = $loginResponse.data.user.username

        Write-Host "  SUCCESS" -ForegroundColor Green
        Write-Host "  User ID: $userId" -ForegroundColor White
        Write-Host "  Username: $username" -ForegroundColor White
    } else {
        Write-Host "  FAILED: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 4: Upload photos to database
Write-Host "Step 4: Uploading photos to database..." -ForegroundColor Yellow
Write-Host ""

$uploadCount = 0
$failCount = 0
$isFirstPhoto = $true

foreach ($file in $downloadedFiles) {
    Write-Host "  Uploading: $($file.Name)" -ForegroundColor Cyan

    try {
        # Prepare multipart form data
        $boundary = [System.Guid]::NewGuid().ToString()
        $LF = "`r`n"

        $fileBytes = [System.IO.File]::ReadAllBytes($file.FullName)
        $fileEnc = [System.Text.Encoding]::GetEncoding('iso-8859-1').GetString($fileBytes)

        $bodyLines = (
            "--$boundary",
            "Content-Disposition: form-data; name=`"photo`"; filename=`"$($file.Name)`"",
            "Content-Type: image/jpeg",
            "",
            $fileEnc,
            "--$boundary--$LF"
        ) -join $LF

        $isAvatar = if ($isFirstPhoto) { "true" } else { "false" }
        $uri = "$baseUrl/users/$userId/photos?isAvatar=$isAvatar"

        $uploadResponse = Invoke-RestMethod -Uri $uri -Method Post -Body $bodyLines `
            -ContentType "multipart/form-data; boundary=$boundary" `
            -Headers @{ "Authorization" = "Bearer $token" }

        if ($uploadResponse.success) {
            $photoId = $uploadResponse.data.photoId
            $avatarTag = if ($uploadResponse.data.isAvatar) { " [AVATAR]" } else { "" }
            Write-Host "    SUCCESS - Photo ID: $photoId$avatarTag" -ForegroundColor Green
            $uploadCount++
            $isFirstPhoto = $false
        } else {
            Write-Host "    FAILED: $($uploadResponse.message)" -ForegroundColor Red
            $failCount++
        }
    } catch {
        Write-Host "    FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $failCount++
    }
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Upload Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "User: $username (ID: $userId)" -ForegroundColor White
Write-Host "Success: $uploadCount photos" -ForegroundColor Green
Write-Host "Failed: $failCount photos" -ForegroundColor $(if ($failCount -gt 0) { "Red" } else { "Gray" })
Write-Host ""

# Step 5: Verify photos in database
if ($uploadCount -gt 0) {
    Write-Host "Step 5: Verifying photos in database..." -ForegroundColor Yellow
    try {
        $verifyResponse = Invoke-RestMethod -Uri "$baseUrl/users/$userId/photos" -Method Get `
            -Headers @{ "Authorization" = "Bearer $token" }

        if ($verifyResponse.success) {
            Write-Host "  Total photos in database: $($verifyResponse.data.Count)" -ForegroundColor Green
            Write-Host ""
            foreach ($photo in $verifyResponse.data) {
                $avatarMark = if ($photo.isAvatar) { " [AVATAR]" } else { "" }
                Write-Host "  Photo ID: $($photo.id)$avatarMark" -ForegroundColor White
                Write-Host "    URL: $($photo.photoUrl)" -ForegroundColor Gray
                Write-Host "    Uploaded: $($photo.uploadTime)" -ForegroundColor DarkGray
                Write-Host ""
            }
        }
    } catch {
        Write-Host "  Verification failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Complete!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Cyan
