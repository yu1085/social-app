# Simple chat records insertion script
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$connectionString = "-u root -proot socialmeet"

Write-Host "Starting to insert chat records..." -ForegroundColor Green

# Clean old data
Write-Host "Cleaning old data..." -ForegroundColor Yellow
& $mysqlPath $connectionString -e "DELETE FROM messages WHERE (sender_id = 22491729 AND receiver_id = 23820512) OR (sender_id = 23820512 AND receiver_id = 22491729);"

# Insert messages one by one
$messages = @(
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Hello, nice to meet you!', 'TEXT', 1, '2024-01-15 10:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Hello! Nice to meet you too', 'TEXT', 1, '2024-01-15 10:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'You look beautiful in your photos', 'TEXT', 1, '2024-01-15 10:35:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Thank you! You are handsome too', 'TEXT', 1, '2024-01-15 10:37:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Nice weather today, what are you doing?', 'TEXT', 1, '2024-01-15 14:20:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Resting at home, how about you?', 'TEXT', 1, '2024-01-15 14:22:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Working, but almost off work', 'TEXT', 1, '2024-01-15 14:25:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Hard work! Any plans after work?', 'TEXT', 1, '2024-01-15 14:27:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Off work! Going to gym', 'TEXT', 1, '2024-01-15 18:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Wow, so disciplined! I want to exercise too', 'TEXT', 1, '2024-01-15 18:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Want to go together? I can teach you', 'TEXT', 1, '2024-01-15 18:35:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Really? That would be great!', 'TEXT', 1, '2024-01-15 18:37:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Good morning! Did you sleep well?', 'TEXT', 1, '2024-01-16 08:15:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Good morning! Slept well, how about you?', 'TEXT', 1, '2024-01-16 08:17:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'I am fine too, in a good mood today', 'TEXT', 1, '2024-01-16 08:19:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Busy at work?', 'TEXT', 1, '2024-01-16 11:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Not bad, how about you?', 'TEXT', 1, '2024-01-16 11:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'A bit busy, but happy to chat with you tonight', 'TEXT', 1, '2024-01-16 11:35:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Haha, me too!', 'TEXT', 1, '2024-01-16 11:37:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Want to video chat?', 'TEXT', 1, '2024-01-16 20:00:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Sure!', 'TEXT', 1, '2024-01-16 20:01:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, '[Video Call]', 'VIDEO', 1, '2024-01-16 20:05:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Video call ended, had a great chat!', 'TEXT', 1, '2024-01-16 21:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Me too! You are more beautiful than photos', 'TEXT', 1, '2024-01-16 21:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Good morning! Any plans today?', 'TEXT', 1, '2024-01-17 09:00:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, '[Voice Call]', 'VOICE', 1, '2024-01-17 09:05:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Voice call ended, your voice is nice', 'TEXT', 1, '2024-01-17 09:15:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Thank you! Your voice is sweet too', 'TEXT', 1, '2024-01-17 09:17:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Went shopping with friends today', 'TEXT', 1, '2024-01-17 15:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'What did you buy?', 'TEXT', 1, '2024-01-17 15:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Bought some clothes, will show you photos', 'TEXT', 1, '2024-01-17 15:35:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Looking forward to seeing your new clothes!', 'TEXT', 1, '2024-01-17 15:37:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Want to know you more, what are your hobbies?', 'TEXT', 1, '2024-01-18 19:00:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'I like watching movies, listening to music, and traveling', 'TEXT', 1, '2024-01-18 19:02:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'I like traveling too! Where have you been?', 'TEXT', 1, '2024-01-18 19:05:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Been to many places, love the beach most', 'TEXT', 1, '2024-01-18 19:07:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'I love the beach too! We can go together next time', 'TEXT', 1, '2024-01-18 19:10:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Always happy chatting with you', 'TEXT', 1, '2024-01-19 20:30:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Me too, feel we get along well', 'TEXT', 1, '2024-01-19 20:32:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Yes, there is a special feeling', 'TEXT', 1, '2024-01-19 20:35:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'I feel the same way', 'TEXT', 1, '2024-01-19 20:37:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'How was work today?', 'TEXT', 0, '2024-01-20 12:00:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Not bad, how about you?', 'TEXT', 1, '2024-01-20 12:05:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'A bit tired, but thinking of you energizes me', 'TEXT', 0, '2024-01-20 12:10:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (22491729, 23820512, 'Haha, you are so sweet', 'TEXT', 1, '2024-01-20 12:12:00');",
    "INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at) VALUES (23820512, 22491729, 'Free for video chat tonight?', 'TEXT', 0, '2024-01-20 18:00:00');"
)

# Insert messages
Write-Host "Inserting messages..." -ForegroundColor Yellow
$successCount = 0
foreach ($sql in $messages) {
    try {
        & $mysqlPath $connectionString -e $sql
        $successCount++
        Write-Host "✓ Message inserted successfully" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Failed to insert message" -ForegroundColor Red
    }
}

# Insert call records
Write-Host "Inserting call records..." -ForegroundColor Yellow
$callSqls = @(
    "INSERT INTO call_records (session_id, caller_id, callee_id, call_type, call_status, duration, price_per_min, total_cost, start_time, end_time, created_at) VALUES ('call_001_20240116', 23820512, 22491729, 'VIDEO', 'ENDED', 90, 2.00, 3.00, '2024-01-16 20:05:00', '2024-01-16 21:30:00', '2024-01-16 20:05:00');",
    "INSERT INTO call_records (session_id, caller_id, callee_id, call_type, call_status, duration, price_per_min, total_cost, start_time, end_time, created_at) VALUES ('call_002_20240117', 23820512, 22491729, 'VOICE', 'ENDED', 10, 1.00, 0.17, '2024-01-17 09:05:00', '2024-01-17 09:15:00', '2024-01-17 09:05:00');"
)

foreach ($sql in $callSqls) {
    try {
        & $mysqlPath $connectionString -e $sql
        Write-Host "✓ Call record inserted successfully" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Failed to insert call record" -ForegroundColor Red
    }
}

# Insert user relationships
Write-Host "Inserting user relationships..." -ForegroundColor Yellow
$relSqls = @(
    "INSERT INTO user_relationships (user_id, target_user_id, relationship_type, intimacy_score, created_at) VALUES (23820512, 22491729, 'LIKE', 85, '2024-01-15 10:30:00');",
    "INSERT INTO user_relationships (user_id, target_user_id, relationship_type, intimacy_score, created_at) VALUES (22491729, 23820512, 'LIKE', 80, '2024-01-15 10:32:00');"
)

foreach ($sql in $relSqls) {
    try {
        & $mysqlPath $connectionString -e $sql
        Write-Host "✓ User relationship inserted successfully" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Failed to insert user relationship" -ForegroundColor Red
    }
}

# Show statistics
Write-Host "`n=== Insertion Statistics ===" -ForegroundColor Cyan
& $mysqlPath $connectionString -e "SELECT COUNT(*) as message_count FROM messages WHERE (sender_id = 22491729 AND receiver_id = 23820512) OR (sender_id = 23820512 AND receiver_id = 22491729);"
& $mysqlPath $connectionString -e "SELECT COUNT(*) as call_record_count FROM call_records WHERE (caller_id = 22491729 AND callee_id = 23820512) OR (caller_id = 23820512 AND callee_id = 22491729);"
& $mysqlPath $connectionString -e "SELECT COUNT(*) as relationship_count FROM user_relationships WHERE (user_id = 22491729 AND target_user_id = 23820512) OR (user_id = 23820512 AND target_user_id = 22491729);"

Write-Host "`nChat records generation completed!" -ForegroundColor Green
Write-Host "User1: 22491729 (video_receiver)" -ForegroundColor Yellow
Write-Host "User2: 23820512 (video_caller)" -ForegroundColor Yellow
