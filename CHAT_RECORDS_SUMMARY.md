# 测试用户聊天记录生成总结

## 用户信息
- **用户1**: 22491729 (video_receiver, 19887654321)
- **用户2**: 23820512 (video_caller, 19812342076)

## 生成的数据统计

### 📱 消息记录
- **总消息数**: 30条
- **消息类型分布**:
  - 文本消息 (TEXT): 28条
  - 视频通话 (VIDEO): 1条
  - 语音通话 (VOICE): 1条
- **已读状态**: 部分消息标记为未读，模拟真实聊天场景

### 📞 通话记录
- **总通话数**: 4条
- **通话类型**:
  - 视频通话: 2条
  - 语音通话: 2条
- **通话状态**: 全部为已结束 (ENDED)
- **费用记录**: 包含价格和总费用信息

### 👥 用户关系
- **关系记录**: 4条
- **关系类型**: LIKE (喜欢)
- **亲密度分数**: 80-85分

## 聊天内容示例

### 初次接触
- "Hello, nice to meet you!"
- "Hello! Nice to meet you too"
- "You look beautiful in your photos"
- "Thank you! You are handsome too"

### 日常聊天
- "Nice weather today, what are you doing?"
- "Resting at home, how about you?"
- "Working, but almost off work"
- "Hard work! Any plans after work?"

### 运动话题
- "Off work! Going to gym"
- "Wow, so disciplined! I want to exercise too"
- "Want to go together? I can teach you"
- "Really? That would be great!"

### 视频通话
- "Want to video chat?"
- "Sure!"
- "[Video Call]"
- "Video call ended, had a great chat!"

### 语音通话
- "Good morning! Any plans today?"
- "[Voice Call]"
- "Voice call ended, your voice is nice"
- "Thank you! Your voice is sweet too"

### 购物分享
- "Went shopping with friends today"
- "What did you buy?"
- "Bought some clothes, will show you photos"
- "Looking forward to seeing your new clothes!"

### 未读消息（模拟最新状态）
- "How was work today?" (未读)
- "Not bad, how about you?" (已读)
- "A bit tired, but thinking of you energizes me" (未读)
- "Haha, you are so sweet" (已读)
- "Free for video chat tonight?" (未读)

## 时间线
- **2024-01-15**: 初次接触，日常聊天，运动话题
- **2024-01-16**: 早安问候，工作聊天，视频通话
- **2024-01-17**: 语音通话，购物分享
- **2024-01-20**: 工作询问，情感交流，视频邀请

## 数据特点
1. **真实性**: 模拟真实用户聊天场景
2. **多样性**: 包含文本、语音、视频多种消息类型
3. **渐进性**: 从初次接触到深入交流的情感发展
4. **未读状态**: 部分消息标记为未读，符合实际使用情况
5. **时间分布**: 跨越多天的聊天记录，体现持续交流

## 测试用途
这些聊天记录可以用于：
- 测试消息列表显示功能
- 测试聊天页面功能
- 测试未读消息计数
- 测试通话记录功能
- 测试用户关系功能
- 验证API接口的正确性

## 数据库表
数据已插入到以下表中：
- `messages`: 消息记录
- `call_records`: 通话记录  
- `user_relationships`: 用户关系

所有数据都已成功插入数据库，可以在应用中正常使用。
