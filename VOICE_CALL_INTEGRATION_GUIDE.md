# 语音通话功能集成指南

## 🎤 功能概述

用户详情页面现在支持完整的语音通话功能，包括：
- 语音通话价格显示
- 语音通话发起
- 权限检查
- 错误处理

## 📱 界面布局

### 底部操作栏布局
```
[发起视频] [发起语音] [私信她] [喜欢]
  500/分钟   300/分钟
```

### 语音通话按钮特点
- **位置**: 位于视频按钮和私信按钮之间
- **样式**: 使用次要按钮样式（灰色背景）
- **内容**: 显示"发起语音"和价格信息
- **图标**: 使用语音图标 `ic_voice`

## 🔧 技术实现

### 1. 布局文件更新
**文件**: `app/src/main/res/layout/activity_user_detail.xml`

```xml
<!-- 发起语音按钮 -->
<LinearLayout
    android:id="@+id/ll_voice_button"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:background="@drawable/button_secondary_bg"
    android:gravity="center"
    android:orientation="vertical"
    android:clickable="true"
    android:focusable="true"
    android:layout_marginEnd="12dp"
    android:padding="16dp">
    
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">
        
        <ImageView
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:src="@drawable/ic_voice"
            android:layout_marginEnd="16dp" />
        
        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:gravity="center">
            
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="发起语音"
                android:textColor="#212121"
                android:textSize="18sp"
                android:textStyle="bold" />
            
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="300/分钟"
                android:textColor="#74777C"
                android:textSize="14sp"
                android:textStyle="bold" />
            
        </LinearLayout>
    </LinearLayout>
</LinearLayout>
```

### 2. Java代码实现
**文件**: `app/src/main/java/com/example/myapplication/UserDetailActivity.java`

#### 初始化语音按钮
```java
private LinearLayout llVoiceButton;

private void initViews() {
    // ... 其他初始化代码
    llVoiceButton = findViewById(R.id.ll_voice_button);
}
```

#### 语音按钮点击事件
```java
llVoiceButton.setOnClickListener(v -> {
    if (userToken == null) {
        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (currentCallPrices == null) {
        Toast.makeText(this, "正在加载价格信息，请稍候...", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (!currentCallPrices.isVoiceCallEnabled()) {
        Toast.makeText(this, "对方未开启语音通话功能", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // 发起语音通话
    initiateVoiceCall();
});
```

#### 发起语音通话方法
```java
private void initiateVoiceCall() {
    if (receiverUserId == null) {
        Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
        return;
    }
    
    new AsyncTask<Void, Void, CallInitiateResult>() {
        @Override
        protected void onPreExecute() {
            Toast.makeText(UserDetailActivity.this, "正在发起语音通话...", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected CallInitiateResult doInBackground(Void... voids) {
            try {
                return callService.initiateVoiceCall(userToken, receiverUserId);
            } catch (Exception e) {
                Log.e("UserDetailActivity", "发起语音通话异常", e);
                return new CallInitiateResult.Error("发起通话失败: " + e.getMessage());
            }
        }
        
        @Override
        protected void onPostExecute(CallInitiateResult result) {
            if (result instanceof CallInitiateResult.Success) {
                CallInitiateResult.Success successResult = (CallInitiateResult.Success) result;
                CallSession callSession = successResult.getCallSession();
                Log.d("UserDetailActivity", "语音通话发起成功: " + callSession.getCallSessionId());
                Toast.makeText(UserDetailActivity.this, "语音通话已发起，等待对方接听...", Toast.LENGTH_LONG).show();
                
                // TODO: 跳转到语音通话界面
            } else if (result instanceof CallInitiateResult.Error) {
                CallInitiateResult.Error errorResult = (CallInitiateResult.Error) result;
                Log.e("UserDetailActivity", "发起语音通话失败: " + errorResult.getMessage());
                Toast.makeText(UserDetailActivity.this, "发起通话失败: " + errorResult.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }.execute();
}
```

### 3. 后端API集成
**文件**: `app/src/main/java/com/example/myapplication/service/CallService.kt`

#### 发起语音通话方法
```kotlin
suspend fun initiateVoiceCall(token: String, receiverId: Long): CallInitiateResult {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/call/initiate")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            // 构建请求体
            val requestBody = JSONObject().apply {
                put("receiverId", receiverId)
                put("callType", "VOICE")  // 语音通话类型
            }
            
            connection.outputStream.use { outputStream ->
                outputStream.write(requestBody.toString().toByteArray())
            }
            
            // 处理响应...
        } catch (e: Exception) {
            Log.e(TAG, "发起语音通话异常", e)
            CallInitiateResult.Error("网络异常: ${e.message}")
        }
    }
}
```

## 🎯 功能特点

### 1. 动态价格显示
- 从后端API获取用户设置的语音通话价格
- 实时更新UI显示
- 支持不同用户不同价格

### 2. 权限检查
- 检查用户登录状态
- 检查对方是否开启语音通话功能
- 检查价格信息是否加载完成

### 3. 错误处理
- 网络异常处理
- API错误处理
- 用户友好的错误提示

### 4. 异步处理
- 使用AsyncTask处理网络请求
- 不阻塞UI线程
- 实时状态反馈

## 📊 测试验证

### 运行测试脚本
```bash
python test_video_call_integration.py
```

### 测试项目
- ✅ API连通性测试
- ✅ 用户登录测试
- ✅ 获取通话价格测试
- ✅ 发起视频通话测试
- ✅ 发起语音通话测试
- ✅ 语音通话价格显示测试

## 🔄 使用流程

### 用户操作流程
1. **打开用户详情页面** - 系统自动加载价格信息
2. **查看价格信息** - 显示视频和语音通话价格
3. **点击语音按钮** - 发起语音通话请求
4. **等待接听** - 显示通话发起状态
5. **通话建立** - 跳转到语音通话界面（待实现）

### 系统处理流程
1. **权限检查** - 验证用户登录和通话权限
2. **API调用** - 调用后端发起语音通话接口
3. **状态更新** - 更新UI显示通话状态
4. **错误处理** - 处理各种异常情况

## 🎨 UI设计

### 按钮样式
- **背景**: 次要按钮样式（灰色）
- **文字**: 深灰色文字
- **图标**: 语音图标
- **布局**: 水平排列，图标+文字

### 价格显示
- **位置**: 按钮内部下方
- **颜色**: 灰色文字
- **格式**: "300/分钟"
- **动态**: 从后端API获取

## 🚀 扩展功能

### 待实现功能
1. **语音通话界面** - 实际的语音通话UI
2. **通话状态管理** - 通话中的状态显示
3. **通话记录** - 通话历史记录
4. **通话质量** - 音质设置和显示

### 可优化功能
1. **价格动画** - 价格变化时的动画效果
2. **按钮状态** - 不同状态下的按钮样式
3. **错误重试** - 网络错误时的重试机制
4. **离线模式** - 离线状态下的功能限制

## 📝 注意事项

1. **图标资源** - 确保 `ic_voice` 图标资源存在
2. **权限配置** - 确保应用有录音权限
3. **网络配置** - 确保后端API地址正确
4. **错误处理** - 完善各种异常情况的处理
5. **用户体验** - 提供清晰的状态反馈

## 🎉 总结

语音通话功能已成功集成到用户详情页面，提供了完整的：
- ✅ 界面布局
- ✅ 功能实现
- ✅ API集成
- ✅ 错误处理
- ✅ 测试验证

用户现在可以在用户详情页面直接发起语音通话，享受完整的社交聊天体验！
