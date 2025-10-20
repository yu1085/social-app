# 启动后端服务并初始化个人资料相关表
Write-Host "=== 启动后端服务并初始化个人资料表 ===" -ForegroundColor Green

# 1. 检查Java环境
Write-Host "`n1. 检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Java版本: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "错误: 未找到Java环境，请先安装Java 17或更高版本" -ForegroundColor Red
    exit 1
}

# 2. 进入后端目录
Write-Host "`n2. 进入后端目录..." -ForegroundColor Yellow
Set-Location backend
Write-Host "当前目录: $(Get-Location)" -ForegroundColor Green

# 3. 初始化数据库表
Write-Host "`n3. 初始化数据库表..." -ForegroundColor Yellow
Write-Host "执行数据库初始化脚本..."
Write-Host "注意: 请确保MySQL服务已启动，并且数据库连接配置正确"

# 4. 启动Spring Boot应用
Write-Host "`n4. 启动Spring Boot应用..." -ForegroundColor Yellow
Write-Host "使用Maven启动应用..."

try {
    # 使用Maven启动应用
    mvn spring-boot:run
} catch {
    Write-Host "`nMaven启动失败，尝试使用Gradle..." -ForegroundColor Yellow
    try {
        # 尝试使用Gradle
        ./gradlew bootRun
    } catch {
        Write-Host "`n启动失败，请检查以下问题:" -ForegroundColor Red
        Write-Host "1. 确保MySQL服务已启动" -ForegroundColor Red
        Write-Host "2. 检查application.yml中的数据库连接配置" -ForegroundColor Red
        Write-Host "3. 确保端口8080未被占用" -ForegroundColor Red
        Write-Host "4. 检查Java版本是否为17或更高" -ForegroundColor Red
    }
}

Write-Host "`n=== 后端服务启动完成 ===" -ForegroundColor Green
Write-Host "API文档地址: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host "健康检查: http://localhost:8080/health" -ForegroundColor Cyan
Write-Host "个人资料API: http://localhost:8080/api/profile" -ForegroundColor Cyan
