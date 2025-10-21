# 为测试用户生成聊天记录的PowerShell脚本
# 用户1: 22491729 (video_receiver, 19887654321)
# 用户2: 23820512 (video_caller, 19812342076)

$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$connectionString = "-u root -proot socialmeet"

Write-Host "开始为测试用户生成聊天记录..." -ForegroundColor Green

# 清理旧数据
Write-Host "清理旧数据..." -ForegroundColor Yellow
& $mysqlPath $connectionString -e "DELETE FROM messages WHERE (sender_id = 22491729 AND receiver_id = 23820512) OR (sender_id = 23820512 AND receiver_id = 22491729);"

# 定义聊天记录数组
$messages = @(
    @{sender=23820512; receiver=22491729; content="你好，很高兴认识你！"; type="TEXT"; read=$true; time="2024-01-15 10:30:00"},
    @{sender=22491729; receiver=23820512; content="你好！我也很高兴认识你"; type="TEXT"; read=$true; time="2024-01-15 10:32:00"},
    @{sender=23820512; receiver=22491729; content="看到你的照片，觉得你很漂亮"; type="TEXT"; read=$true; time="2024-01-15 10:35:00"},
    @{sender=22491729; receiver=23820512; content="谢谢夸奖，你也很帅"; type="TEXT"; read=$true; time="2024-01-15 10:37:00"},
    @{sender=23820512; receiver=22491729; content="今天天气不错，你在做什么呢？"; type="TEXT"; read=$true; time="2024-01-15 14:20:00"},
    @{sender=22491729; receiver=23820512; content="在家休息，你呢？"; type="TEXT"; read=$true; time="2024-01-15 14:22:00"},
    @{sender=23820512; receiver=22491729; content="我在工作，不过快下班了"; type="TEXT"; read=$true; time="2024-01-15 14:25:00"},
    @{sender=22491729; receiver=23820512; content="辛苦了！下班后有什么安排吗？"; type="TEXT"; read=$true; time="2024-01-15 14:27:00"},
    @{sender=23820512; receiver=22491729; content="下班了！准备去健身房"; type="TEXT"; read=$true; time="2024-01-15 18:30:00"},
    @{sender=22491729; receiver=23820512; content="哇，好自律！我最近也想运动"; type="TEXT"; read=$true; time="2024-01-15 18:32:00"},
    @{sender=23820512; receiver=22491729; content="要不要一起？我可以教你"; type="TEXT"; read=$true; time="2024-01-15 18:35:00"},
    @{sender=22491729; receiver=23820512; content="真的吗？那太好了！"; type="TEXT"; read=$true; time="2024-01-15 18:37:00"},
    @{sender=22491729; receiver=23820512; content="早上好！昨晚睡得好吗？"; type="TEXT"; read=$true; time="2024-01-16 08:15:00"},
    @{sender=23820512; receiver=22491729; content="早上好！睡得不错，你呢？"; type="TEXT"; read=$true; time="2024-01-16 08:17:00"},
    @{sender=22491729; receiver=23820512; content="我也很好，今天心情不错"; type="TEXT"; read=$true; time="2024-01-16 08:19:00"},
    @{sender=23820512; receiver=22491729; content="工作忙吗？"; type="TEXT"; read=$true; time="2024-01-16 11:30:00"},
    @{sender=22491729; receiver=23820512; content="还好，你呢？"; type="TEXT"; read=$true; time="2024-01-16 11:32:00"},
    @{sender=23820512; receiver=22491729; content="有点忙，不过想到晚上能和你聊天就很开心"; type="TEXT"; read=$true; time="2024-01-16 11:35:00"},
    @{sender=22491729; receiver=23820512; content="哈哈，我也是！"; type="TEXT"; read=$true; time="2024-01-16 11:37:00"},
    @{sender=23820512; receiver=22491729; content="要不要视频聊天？"; type="TEXT"; read=$true; time="2024-01-16 20:00:00"},
    @{sender=22491729; receiver=23820512; content="好的！"; type="TEXT"; read=$true; time="2024-01-16 20:01:00"},
    @{sender=23820512; receiver=22491729; content="[视频通话]"; type="VIDEO"; read=$true; time="2024-01-16 20:05:00"},
    @{sender=22491729; receiver=23820512; content="视频通话结束了，聊得很开心！"; type="TEXT"; read=$true; time="2024-01-16 21:30:00"},
    @{sender=23820512; receiver=22491729; content="我也是！你比照片还漂亮"; type="TEXT"; read=$true; time="2024-01-16 21:32:00"},
    @{sender=22491729; receiver=23820512; content="早上好！今天有什么计划吗？"; type="TEXT"; read=$true; time="2024-01-17 09:00:00"},
    @{sender=23820512; receiver=22491729; content="[语音通话]"; type="VOICE"; read=$true; time="2024-01-17 09:05:00"},
    @{sender=22491729; receiver=23820512; content="语音通话结束了，你的声音很好听"; type="TEXT"; read=$true; time="2024-01-17 09:15:00"},
    @{sender=23820512; receiver=22491729; content="谢谢！你的声音也很甜"; type="TEXT"; read=$true; time="2024-01-17 09:17:00"},
    @{sender=22491729; receiver=23820512; content="今天和朋友去逛街了"; type="TEXT"; read=$true; time="2024-01-17 15:30:00"},
    @{sender=23820512; receiver=22491729; content="买了什么好东西吗？"; type="TEXT"; read=$true; time="2024-01-17 15:32:00"},
    @{sender=22491729; receiver=23820512; content="买了几件衣服，还给你看照片"; type="TEXT"; read=$true; time="2024-01-17 15:35:00"},
    @{sender=23820512; receiver=22491729; content="期待看到你的新衣服！"; type="TEXT"; read=$true; time="2024-01-17 15:37:00"},
    @{sender=23820512; receiver=22491729; content="想了解你更多，你平时有什么爱好？"; type="TEXT"; read=$true; time="2024-01-18 19:00:00"},
    @{sender=22491729; receiver=23820512; content="我喜欢看电影、听音乐，还有旅行"; type="TEXT"; read=$true; time="2024-01-18 19:02:00"},
    @{sender=23820512; receiver=22491729; content="我也喜欢旅行！你去过哪些地方？"; type="TEXT"; read=$true; time="2024-01-18 19:05:00"},
    @{sender=22491729; receiver=23820512; content="去过很多地方，最喜欢的是海边"; type="TEXT"; read=$true; time="2024-01-18 19:07:00"},
    @{sender=23820512; receiver=22491729; content="我也喜欢海边！下次可以一起去"; type="TEXT"; read=$true; time="2024-01-18 19:10:00"},
    @{sender=22491729; receiver=23820512; content="和你聊天总是很开心"; type="TEXT"; read=$true; time="2024-01-19 20:30:00"},
    @{sender=23820512; receiver=22491729; content="我也是，感觉我们很合得来"; type="TEXT"; read=$true; time="2024-01-19 20:32:00"},
    @{sender=22491729; receiver=23820512; content="是的，有种特别的感觉"; type="TEXT"; read=$true; time="2024-01-19 20:35:00"},
    @{sender=23820512; receiver=22491729; content="我也是这么觉得的"; type="TEXT"; read=$true; time="2024-01-19 20:37:00"},
    @{sender=23820512; receiver=22491729; content="今天工作怎么样？"; type="TEXT"; read=$false; time="2024-01-20 12:00:00"},
    @{sender=22491729; receiver=23820512; content="还不错，你呢？"; type="TEXT"; read=$true; time="2024-01-20 12:05:00"},
    @{sender=23820512; receiver=22491729; content="有点累，不过想到你就精神了"; type="TEXT"; read=$false; time="2024-01-20 12:10:00"},
    @{sender=22491729; receiver=23820512; content="哈哈，你真会说话"; type="TEXT"; read=$true; time="2024-01-20 12:12:00"},
    @{sender=23820512; receiver=22491729; content="晚上有空视频聊天吗？"; type="TEXT"; read=$false; time="2024-01-20 18:00:00"}
)

# 插入消息记录
Write-Host "插入消息记录..." -ForegroundColor Yellow
$successCount = 0
foreach ($msg in $messages) {
    $readValue = if ($msg.read) { "1" } else { "0" }
    $sql = "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES ($($msg.sender), $($msg.receiver), '$($msg.content)', '$($msg.type)', $readValue, '$($msg.time)');"
    
    try {
        & $mysqlPath $connectionString -e $sql
        $successCount++
        Write-Host "✓ 插入消息: $($msg.content)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ 插入失败: $($msg.content)" -ForegroundColor Red
    }
}

# 插入通话记录
Write-Host "插入通话记录..." -ForegroundColor Yellow
$callRecords = @(
    @{session="call_001_20240116"; caller=23820512; callee=22491729; type="VIDEO"; status="ENDED"; duration=90; price=2.00; cost=3.00; start="2024-01-16 20:05:00"; end="2024-01-16 21:30:00"},
    @{session="call_002_20240117"; caller=23820512; callee=22491729; type="VOICE"; status="ENDED"; duration=10; price=1.00; cost=0.17; start="2024-01-17 09:05:00"; end="2024-01-17 09:15:00"}
)

foreach ($call in $callRecords) {
    $sql = "INSERT INTO call_records (session_id, caller_id, callee_id, call_type, call_status, duration, price_per_min, total_cost, start_time, end_time, created_at) VALUES ('$($call.session)', $($call.caller), $($call.callee), '$($call.type)', '$($call.status)', $($call.duration), $($call.price), $($call.cost), '$($call.start)', '$($call.end)', '$($call.start)');"
    
    try {
        & $mysqlPath $connectionString -e $sql
        Write-Host "✓ 插入通话记录: $($call.session)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ 插入通话记录失败: $($call.session)" -ForegroundColor Red
    }
}

# 插入用户关系
Write-Host "插入用户关系..." -ForegroundColor Yellow
$relationships = @(
    @{user=23820512; target=22491729; type="LIKE"; score=85; time="2024-01-15 10:30:00"},
    @{user=22491729; target=23820512; type="LIKE"; score=80; time="2024-01-15 10:32:00"}
)

foreach ($rel in $relationships) {
    $sql = "INSERT INTO user_relationships (user_id, target_user_id, relationship_type, intimacy_score, created_at) VALUES ($($rel.user), $($rel.target), '$($rel.type)', $($rel.score), '$($rel.time)');"
    
    try {
        & $mysqlPath $connectionString -e $sql
        Write-Host "✓ 插入用户关系: $($rel.user) -> $($rel.target)" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ 插入用户关系失败: $($rel.user) -> $($rel.target)" -ForegroundColor Red
    }
}

# 显示统计结果
Write-Host "`n=== 插入完成统计 ===" -ForegroundColor Cyan
& $mysqlPath $connectionString -e "SELECT COUNT(*) as message_count FROM messages WHERE (sender_id = 22491729 AND receiver_id = 23820512) OR (sender_id = 23820512 AND receiver_id = 22491729);"
& $mysqlPath $connectionString -e "SELECT COUNT(*) as call_record_count FROM call_records WHERE (caller_id = 22491729 AND callee_id = 23820512) OR (caller_id = 23820512 AND callee_id = 22491729);"
& $mysqlPath $connectionString -e "SELECT COUNT(*) as relationship_count FROM user_relationships WHERE (user_id = 22491729 AND target_user_id = 23820512) OR (user_id = 23820512 AND target_user_id = 22491729);"

Write-Host "`n聊天记录生成完成！" -ForegroundColor Green
Write-Host "用户1: 22491729 (video_receiver)" -ForegroundColor Yellow
Write-Host "用户2: 23820512 (video_caller)" -ForegroundColor Yellow
