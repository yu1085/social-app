# 创建测试图片
param(
    [string]$OutputPath = "C:\Users\Administrator\Downloads\test_photos"
)

Write-Host "创建测试图片..." -ForegroundColor Cyan

# 创建输出目录
if (-not (Test-Path $OutputPath)) {
    New-Item -ItemType Directory -Path $OutputPath -Force | Out-Null
    Write-Host "已创建目录: $OutputPath" -ForegroundColor Green
}

# 使用.NET创建简单的测试图片
Add-Type -AssemblyName System.Drawing

# 创建3张不同颜色的测试图片
$colors = @(
    @{Name="test_photo_1_red.jpg"; Color=[System.Drawing.Color]::FromArgb(255, 100, 100)},
    @{Name="test_photo_2_green.jpg"; Color=[System.Drawing.Color]::FromArgb(100, 255, 100)},
    @{Name="test_photo_3_blue.jpg"; Color=[System.Drawing.Color]::FromArgb(100, 100, 255)}
)

foreach ($colorInfo in $colors) {
    $bitmap = New-Object System.Drawing.Bitmap(400, 400)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)

    # 填充背景色
    $brush = New-Object System.Drawing.SolidBrush($colorInfo.Color)
    $graphics.FillRectangle($brush, 0, 0, 400, 400)

    # 添加文字
    $font = New-Object System.Drawing.Font("Arial", 24)
    $textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    $graphics.DrawString("Test Photo", $font, $textBrush, 100, 180)

    # 保存图片
    $filePath = Join-Path $OutputPath $colorInfo.Name
    $bitmap.Save($filePath, [System.Drawing.Imaging.ImageFormat]::Jpeg)

    $graphics.Dispose()
    $bitmap.Dispose()
    $brush.Dispose()
    $textBrush.Dispose()
    $font.Dispose()

    Write-Host "已创建: $($colorInfo.Name)" -ForegroundColor Green
}

Write-Host ""
Write-Host "Test images created in: $OutputPath" -ForegroundColor Cyan
Write-Host "Total: $($colors.Count) images" -ForegroundColor Green
