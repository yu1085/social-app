# Check recent messages in database
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

if (Test-Path $mysqlPath) {
    & $mysqlPath -u root -proot socialmeet -e "SELECT id, sender_id, receiver_id, content, created_at FROM messages WHERE id >= 240 ORDER BY id;" 2>&1
} else {
    Write-Host "MySQL not found at default path, trying system PATH"
    mysql -u root -proot socialmeet -e "SELECT id, sender_id, receiver_id, content, created_at FROM messages WHERE id >= 240 ORDER BY id;" 2>&1
}
