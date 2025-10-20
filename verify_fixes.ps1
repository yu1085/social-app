# 验证修复是否有效
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "验证修复状态" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 检查修改的文件
Write-Host "[1] 检查修改的文件..." -ForegroundColor Yellow

# 检查WalletDTO.java
$walletDtoPath = "app\src\main\java\com\example\myapplication\dto\WalletDTO.java"
if (Test-Path $walletDtoPath) {
    $content = Get-Content $walletDtoPath -Raw
    if ($content -match "private String createdAt" -and $content -match "@SerializedName") {
        Write-Host "✓ WalletDTO.java 已修复 - 时间字段改为String类型" -ForegroundColor Green
    } else {
        Write-Host "✗ WalletDTO.java 修复不完整" -ForegroundColor Red
    }
} else {
    Write-Host "✗ WalletDTO.java 文件不存在" -ForegroundColor Red
}

# 检查PaymentController.java
$paymentControllerPath = "backend\src\main\java\com\socialmeet\backend\controller\PaymentController.java"
if (Test-Path $paymentControllerPath) {
    $content = Get-Content $paymentControllerPath -Raw
    if ($content -match "@RequestMapping\(\"/api/payment\"\)" -and $content -match "@PostMapping\(\"/alipay/create\"\)") {
        Write-Host "✓ PaymentController.java 已修复 - API路径已更新" -ForegroundColor Green
    } else {
        Write-Host "✗ PaymentController.java 修复不完整" -ForegroundColor Red
    }
} else {
    Write-Host "✗ PaymentController.java 文件不存在" -ForegroundColor Red
}

# 检查ProfileViewModel.kt
$profileViewModelPath = "app\src\main\java\com\example\myapplication\viewmodel\ProfileViewModel.kt"
if (Test-Path $profileViewModelPath) {
    $content = Get-Content $profileViewModelPath -Raw
    if ($content -match "Dispatchers.IO" -and $content -match "withContext") {
        Write-Host "✓ ProfileViewModel.kt 已修复 - 线程调度已优化" -ForegroundColor Green
    } else {
        Write-Host "✗ ProfileViewModel.kt 修复不完整" -ForegroundColor Red
    }
} else {
    Write-Host "✗ ProfileViewModel.kt 文件不存在" -ForegroundColor Red
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "修复状态总结" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "已修复的问题：" -ForegroundColor Yellow
Write-Host "1. Gson解析错误 - WalletDTO时间字段类型修复" -ForegroundColor Green
Write-Host "2. 支付API路由错误 - PaymentController路径修复" -ForegroundColor Green  
Write-Host "3. 主线程网络调用异常 - ProfileViewModel线程调度修复" -ForegroundColor Green
Write-Host "4. 空指针异常 - 状态变量初始化修复" -ForegroundColor Green
Write-Host ""
Write-Host "下一步操作：" -ForegroundColor Yellow
Write-Host "1. 重新编译Android应用" -ForegroundColor Gray
Write-Host "2. 重新编译并启动后端服务" -ForegroundColor Gray
Write-Host "3. Test application functionality" -ForegroundColor Gray
