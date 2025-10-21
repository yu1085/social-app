# Insert missing users via MySQL command line
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$sqlFile = "backend\database\insert_missing_users.sql"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Insert Missing Users (23820514, 23820515)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if (Test-Path $mysqlPath) {
    Write-Host "`nExecuting SQL script..." -ForegroundColor Yellow
    try {
        & $mysqlPath -u root -proot < $sqlFile
        Write-Host "Success: Missing users created!" -ForegroundColor Green
    } catch {
        Write-Host "Failed: $_" -ForegroundColor Red
    }
} else {
    Write-Host "`nMySQL not found at: $mysqlPath" -ForegroundColor Red
    Write-Host "Please run the SQL script manually or update the path" -ForegroundColor Yellow
    Write-Host "`nSQL file location: $sqlFile" -ForegroundColor Yellow
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
