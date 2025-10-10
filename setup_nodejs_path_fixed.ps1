# Node.js Environment Variable Setup Script
# Run as Administrator

Write-Host "Setting up Node.js environment variables..." -ForegroundColor Green

# Check if Node.js is installed
$nodePath = "C:\Program Files\nodejs\"
if (Test-Path $nodePath) {
    Write-Host "Found Node.js installation path: $nodePath" -ForegroundColor Yellow
    
    # Get current system PATH
    $currentPath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
    
    # Check if Node.js path is already included
    if ($currentPath -notlike "*$nodePath*") {
        Write-Host "Adding Node.js to system PATH..." -ForegroundColor Yellow
        $newPath = $currentPath + ";" + $nodePath
        [Environment]::SetEnvironmentVariable("PATH", $newPath, "Machine")
        Write-Host "Node.js successfully added to system PATH!" -ForegroundColor Green
        Write-Host "Please restart your command prompt for changes to take effect." -ForegroundColor Cyan
    } else {
        Write-Host "Node.js path already exists in system PATH." -ForegroundColor Green
    }
} else {
    Write-Host "Node.js installation path not found. Please check your installation." -ForegroundColor Red
}

# Verify setup
Write-Host "`nVerifying Node.js and npm versions:" -ForegroundColor Cyan
try {
    $env:PATH += ";$nodePath"
    $nodeVersion = & node --version 2>$null
    $npmVersion = & npm --version 2>$null
    
    if ($nodeVersion) {
        Write-Host "Node.js version: $nodeVersion" -ForegroundColor Green
    }
    if ($npmVersion) {
        Write-Host "npm version: $npmVersion" -ForegroundColor Green
    }
} catch {
    Write-Host "Could not verify Node.js and npm versions. You may need to restart your command prompt." -ForegroundColor Yellow
}

Write-Host "`nSetup completed!" -ForegroundColor Green
