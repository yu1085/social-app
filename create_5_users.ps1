# Create 5 test users via MySQL
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$mysqlUser = "root"
$mysqlPassword = "root"
$database = "socialmeet"

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Creating 5 Test Users" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# SQL to create users
$sql = @"
-- User 1: Female, Beijing, online
INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES ('user_20250001', '北京小姐姐', '13900001111', 'FEMALE', 25, '北京', '喜欢旅游和摄影，期待遇见有趣的人', 'ONLINE', 500.00, NOW(), NOW());

-- User 2: Male, Shanghai, online
INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES ('user_20250002', '上海阳光男孩', '13900002222', 'MALE', 28, '上海', '热爱运动和音乐，交个朋友吧', 'ONLINE', 300.00, NOW(), NOW());

-- User 3: Female, Guangzhou, busy
INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES ('user_20250003', '广州甜心', '13900003333', 'FEMALE', 23, '广州', '爱笑的女孩运气不会太差', 'BUSY', 800.00, NOW(), NOW());

-- User 4: Female, Shenzhen, online
INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES ('user_20250004', '深圳小仙女', '13900004444', 'FEMALE', 26, '深圳', '工作之余想认识更多朋友', 'ONLINE', 1200.00, NOW(), NOW());

-- User 5: Male, Hangzhou, offline
INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES ('user_20250005', '杭州暖男', '13900005555', 'MALE', 29, '杭州', '性格温和，喜欢美食和电影', 'OFFLINE', 600.00, NOW(), NOW());
"@

Write-Host "Step 1: Creating 5 users..." -ForegroundColor Yellow

try {
    & $mysqlPath -u $mysqlUser -p$mysqlPassword --default-character-set=utf8mb4 $database -e $sql
    Write-Host "  SUCCESS: Users created" -ForegroundColor Green
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Create user settings
$settingsSql = @"
INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id, 3.00, 6.00, NOW(), NOW() FROM users WHERE phone = '13900001111';

INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id, 2.50, 5.00, NOW(), NOW() FROM users WHERE phone = '13900002222';

INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id, 4.00, 8.00, NOW(), NOW() FROM users WHERE phone = '13900003333';

INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id, 5.00, 10.00, NOW(), NOW() FROM users WHERE phone = '13900004444';

INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id, 2.00, 4.00, NOW(), NOW() FROM users WHERE phone = '13900005555';
"@

Write-Host "Step 2: Creating user settings (prices)..." -ForegroundColor Yellow

try {
    & $mysqlPath -u $mysqlUser -p$mysqlPassword --default-character-set=utf8mb4 $database -e $settingsSql
    Write-Host "  SUCCESS: Settings created" -ForegroundColor Green
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Create wallets
$walletsSql = @"
INSERT INTO wallets (user_id, balance, total_recharged, total_consumed, created_at, updated_at)
SELECT id, balance, 0.00, 0.00, NOW(), NOW()
FROM users
WHERE phone IN ('13900001111', '13900002222', '13900003333', '13900004444', '13900005555');
"@

Write-Host "Step 3: Creating wallets..." -ForegroundColor Yellow

try {
    & $mysqlPath -u $mysqlUser -p$mysqlPassword --default-character-set=utf8mb4 $database -e $walletsSql
    Write-Host "  SUCCESS: Wallets created" -ForegroundColor Green
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Verify users
Write-Host "Step 4: Verifying new users..." -ForegroundColor Yellow

$verifySql = @"
SELECT
    u.id,
    u.username,
    u.nickname,
    u.phone,
    u.gender,
    u.location,
    u.online_status,
    us.video_call_price
FROM users u
LEFT JOIN user_settings us ON u.id = us.user_id
WHERE u.phone IN ('13900001111', '13900002222', '13900003333', '13900004444', '13900005555')
ORDER BY u.id;
"@

Write-Host ""
& $mysqlPath -u $mysqlUser -p$mysqlPassword --default-character-set=utf8mb4 -t $database -e $verifySql

Write-Host ""
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "Complete! 5 users created" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Cyan
