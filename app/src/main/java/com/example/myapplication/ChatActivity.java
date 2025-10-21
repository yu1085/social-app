package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.MessageDTO;
import com.example.myapplication.websocket.MessageWebSocketManager;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class ChatActivity extends AppCompatActivity {
    
    private ImageView ivBack;
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvIntimacyScore;
    private TextView tvAddFriend;
    private ImageView ivMoreOptions;
    private LinearLayout llVideoCall;
    private LinearLayout llVoiceCall;
    private LinearLayout llQuickReply1;
    private LinearLayout llQuickReply2;
    private LinearLayout llQuickReply3;
    private LinearLayout llVoiceInput;
    private LinearLayout llGiftButton;
    private LinearLayout llEmojiButton;
    private LinearLayout llVoiceChat;
    private LinearLayout llVideoChat;
    private LinearLayout llSendImage;
    private LinearLayout llSendButton;

    // 聊天相关
    private EditText etMessageInput;
    private LinearLayout llMessageContainer;
    private ScrollView svMessages;
    private Long currentUserId;
    private Long otherUserId;
    private String otherUserName;
    private List<ChatMessage> messages = new ArrayList<>();

    // 新消息广播接收器
    private BroadcastReceiver newMessageReceiver;

    // WebSocket消息监听器
    private MessageWebSocketManager.MessageListener webSocketMessageListener;

    // 图片选择器
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // ✅ 获取传递的用户数据，包括用户ID
        otherUserId = getIntent().getLongExtra("user_id", -1L);
        otherUserName = getIntent().getStringExtra("user_name");
        String userAvatar = getIntent().getStringExtra("user_avatar");

        Log.d("ChatActivity", "═══════════════════════════════════════");
        Log.d("ChatActivity", "onCreate - 接收到的对方用户信息:");
        Log.d("ChatActivity", "  otherUserId: " + otherUserId);
        Log.d("ChatActivity", "  otherUserName: " + otherUserName);
        Log.d("ChatActivity", "  userAvatar: " + userAvatar);
        Log.d("ChatActivity", "═══════════════════════════════════════");

        // 获取当前登录用户ID
        currentUserId = AuthManager.getInstance(this).getUserId();
        Log.d("ChatActivity", "当前登录用户ID: " + currentUserId);

        // ✅ 修复：如果otherUserId等于currentUserId，说明数据错误
        if (otherUserId != null && otherUserId.equals(currentUserId)) {
            Log.e("ChatActivity", "⚠️ 检测到异常：otherUserId等于currentUserId！这是数据错误，无法与自己聊天");
            Toast.makeText(this, "数据错误：无法与自己聊天", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (otherUserId == null || otherUserId == -1L) {
            Log.e("ChatActivity", "⚠️ 未传递对方用户ID，聊天功能将不可用");
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
        }

        initViews();
        setupImagePicker();
        setupClickListeners();
        populateUserData(otherUserName, userAvatar);

        // ✅ 加载聊天记录
        if (otherUserId != null && otherUserId != -1L && currentUserId != null) {
            loadChatHistory();
        }

        // ✅ 注册新消息广播接收器
        setupNewMessageReceiver();

        // ✅ 注册WebSocket消息监听器
        setupWebSocketMessageListener();
    }

    /**
     * 设置WebSocket消息监听器
     */
    private void setupWebSocketMessageListener() {
        MessageWebSocketManager wsManager = MyApplication.getWebSocketManager();
        if (wsManager == null) {
            Log.w("ChatActivity", "WebSocket管理器未初始化");
            return;
        }

        webSocketMessageListener = new MessageWebSocketManager.MessageListener() {
            @Override
            public void onNewMessage(JSONObject message) {
                try {
                    String type = message.optString("type");
                    if ("CHAT_MESSAGE".equals(type)) {
                        Long senderId = message.optLong("senderId");
                        String senderName = message.optString("senderName");
                        String content = message.optString("content");
                        String messageType = message.optString("messageType");
                        Long messageId = message.optLong("messageId");
                        String timestamp = message.optString("timestamp");

                        Log.d("ChatActivity", "收到WebSocket消息 - senderId: " + senderId + ", content: " + content);
                        Log.d("ChatActivity", "  当前聊天对象otherUserId: " + otherUserId);
                        Log.d("ChatActivity", "  当前登录用户currentUserId: " + currentUserId);
                        Log.d("ChatActivity", "  消息receiverId: " + message.optLong("receiverId"));

                        // 检查消息是否来自当前聊天对象，或者是发送给当前聊天对象的
                        boolean isFromChatPartner = otherUserId != null && senderId.equals(otherUserId);
                        boolean isToChatPartner = otherUserId != null && message.optLong("receiverId") == otherUserId.longValue() && senderId.equals(currentUserId);

                        if (isFromChatPartner) {
                            Log.i("ChatActivity", "✅ 收到来自聊天对象的消息: " + content);

                            // 检查是否已存在（去重）
                            boolean exists = false;
                            for (ChatMessage msg : messages) {
                                if (msg.id != null && msg.id.equals(messageId)) {
                                    exists = true;
                                    Log.d("ChatActivity", "消息已存在，跳过: messageId=" + messageId);
                                    break;
                                }
                            }

                            if (!exists) {
                                // 添加新消息到列表
                                ChatMessage newMessage = new ChatMessage(
                                    messageId,
                                    senderId,
                                    currentUserId,
                                    content,
                                    messageType != null ? messageType : "TEXT",
                                    false,
                                    timestamp != null ? timestamp : String.valueOf(System.currentTimeMillis()),
                                    senderName != null ? senderName : otherUserName
                                );
                                messages.add(newMessage);
                                Log.i("ChatActivity", "✅ 新消息已添加到列表，总消息数: " + messages.size());

                                // 更新UI（在主线程）- 只添加新消息，不重新渲染所有消息
                                runOnUiThread(() -> {
                                    addMessageToView(newMessage);
                                    svMessages.post(() -> svMessages.fullScroll(View.FOCUS_DOWN));
                                    Log.d("ChatActivity", "✅ UI已更新，新消息已显示");
                                });
                            }
                        } else if (isToChatPartner) {
                            Log.d("ChatActivity", "这是我发送给聊天对象的消息，已在发送时添加，跳过");
                        } else {
                            Log.d("ChatActivity", "消息不属于当前聊天，忽略");
                        }
                    }
                } catch (Exception e) {
                    Log.e("ChatActivity", "处理WebSocket消息失败", e);
                }
            }

            @Override
            public void onConnected() {
                Log.i("ChatActivity", "WebSocket已连接");
            }

            @Override
            public void onDisconnected() {
                Log.w("ChatActivity", "WebSocket已断开");
            }
        };

        wsManager.addMessageListener(webSocketMessageListener);
        Log.i("ChatActivity", "✅ WebSocket消息监听器已注册");
    }

    /**
     * 设置新消息广播接收器
     */
    private void setupNewMessageReceiver() {
        newMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.example.myapplication.NEW_MESSAGE".equals(intent.getAction())) {
                    String senderId = intent.getStringExtra("senderId");
                    String senderName = intent.getStringExtra("senderName");
                    String content = intent.getStringExtra("content");
                    String messageType = intent.getStringExtra("messageType");
                    String timestamp = intent.getStringExtra("timestamp");

                    // 检查所有必要的数据是否存在
                    if (senderId == null) {
                        Log.e("ChatActivity", "senderId 为 null");
                        return;
                    }
                    if (content == null) {
                        Log.e("ChatActivity", "content 为 null");
                        return;
                    }

                    // 检查消息是否来自当前聊天对象
                    boolean isFromChatTarget = otherUserId != null && senderId.equals(String.valueOf(otherUserId));

                    if (isFromChatTarget) {
                        Log.d("ChatActivity", "收到新消息: " + content);

                        // 添加新消息到列表
                        ChatMessage newMessage = new ChatMessage(
                            null,
                            Long.parseLong(senderId),
                            currentUserId,
                            content,
                            messageType != null ? messageType : "TEXT",
                            false,
                            timestamp != null ? timestamp : String.valueOf(System.currentTimeMillis()),
                            senderName != null ? senderName : "未知用户"
                        );
                        messages.add(newMessage);

                        // 更新UI - 只添加新消息，不重新渲染所有消息
                        addMessageToView(newMessage);
                        svMessages.post(() -> svMessages.fullScroll(View.FOCUS_DOWN));
                        Log.d("ChatActivity", "✅ UI已更新（广播接收），新消息已显示");
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("com.example.myapplication.NEW_MESSAGE");
        registerReceiver(newMessageReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        Log.d("ChatActivity", "已注册新消息广播接收器");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        if (newMessageReceiver != null) {
            unregisterReceiver(newMessageReceiver);
            Log.d("ChatActivity", "已注销新消息广播接收器");
        }

        // 移除WebSocket监听器
        if (webSocketMessageListener != null) {
            MessageWebSocketManager wsManager = MyApplication.getWebSocketManager();
            if (wsManager != null) {
                wsManager.removeMessageListener(webSocketMessageListener);
                Log.d("ChatActivity", "✅ 已移除WebSocket消息监听器");
            }
        }
    }
    
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvIntimacyScore = findViewById(R.id.tv_intimacy_score);
        tvAddFriend = findViewById(R.id.tv_add_friend);
        ivMoreOptions = findViewById(R.id.iv_more_options);
        llVideoCall = findViewById(R.id.ll_video_call);
        llVoiceCall = findViewById(R.id.ll_voice_call);
        llQuickReply1 = findViewById(R.id.ll_quick_reply_1);
        llQuickReply2 = findViewById(R.id.ll_quick_reply_2);
        llQuickReply3 = findViewById(R.id.ll_quick_reply_3);
        llVoiceInput = findViewById(R.id.ll_voice_input);
        llGiftButton = findViewById(R.id.ll_gift_button);
        llEmojiButton = findViewById(R.id.ll_emoji_button);
        llVoiceChat = findViewById(R.id.ll_voice_chat);
        llVideoChat = findViewById(R.id.ll_video_chat);
        llSendImage = findViewById(R.id.ll_send_image);
        llSendButton = findViewById(R.id.ll_send_button);

        // ✅ 初始化聊天相关UI元素
        etMessageInput = findViewById(R.id.et_message_input);
        llMessageContainer = findViewById(R.id.ll_message_container);
        svMessages = findViewById(R.id.sv_messages);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        tvAddFriend.setOnClickListener(v -> {
            Toast.makeText(this, "已发送好友请求", Toast.LENGTH_SHORT).show();
        });
        
        ivMoreOptions.setOnClickListener(v -> {
            Toast.makeText(this, "更多选项", Toast.LENGTH_SHORT).show();
        });
        
        llVideoCall.setOnClickListener(v -> {
            Toast.makeText(this, "发起视频通话", Toast.LENGTH_SHORT).show();
        });
        
        llVoiceCall.setOnClickListener(v -> {
            Toast.makeText(this, "发起语音通话", Toast.LENGTH_SHORT).show();
        });
        
        llQuickReply1.setOnClickListener(v -> {
            Toast.makeText(this, "发送: 你在哪里呀?", Toast.LENGTH_SHORT).show();
        });
        
        llQuickReply2.setOnClickListener(v -> {
            Toast.makeText(this, "发送: 你平时什么时候比较空闲", Toast.LENGTH_SHORT).show();
        });
        
        llQuickReply3.setOnClickListener(v -> {
            Toast.makeText(this, "发送: 吃饭了", Toast.LENGTH_SHORT).show();
        });
        
        llVoiceInput.setOnClickListener(v -> {
            Toast.makeText(this, "语音输入", Toast.LENGTH_SHORT).show();
        });
        
        llGiftButton.setOnClickListener(v -> {
            showGiftDialog();
        });
        
        llEmojiButton.setOnClickListener(v -> {
            Toast.makeText(this, "选择表情", Toast.LENGTH_SHORT).show();
        });

        // 底部图标栏的点击事件
        llVoiceChat.setOnClickListener(v -> {
            Toast.makeText(this, "语音聊天", Toast.LENGTH_SHORT).show();
        });

        llVideoChat.setOnClickListener(v -> {
            Toast.makeText(this, "视频聊天", Toast.LENGTH_SHORT).show();
        });

        llSendImage.setOnClickListener(v -> {
            openImagePicker();
        });

        // ✅ 发送按钮点击事件
        llSendButton.setOnClickListener(v -> {
            String content = etMessageInput.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
            } else {
                Toast.makeText(this, "请输入消息内容", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void populateUserData(String userName, String userAvatar) {
        if (userName != null) {
            tvUserName.setText(userName);
        }
        // 这里可以设置用户头像
        // ivUserAvatar.setImageResource(getImageResourceId(userAvatar));
    }
    
    /**
     * 聊天消息数据类
     */
    public static class ChatMessage {
        public Long id;
        public Long senderId;
        public Long receiverId;
        public String content;
        public String messageType;
        public boolean isRead;
        public String timestamp;
        public String senderName;
        
        public ChatMessage(Long id, Long senderId, Long receiverId, String content, 
                          String messageType, boolean isRead, String timestamp, String senderName) {
            this.id = id;
            this.senderId = senderId;
            this.receiverId = receiverId;
            this.content = content;
            this.messageType = messageType;
            this.isRead = isRead;
            this.timestamp = timestamp;
            this.senderName = senderName;
        }
        
        public boolean isFromCurrentUser(Long currentUserId) {
            return this.senderId.equals(currentUserId);
        }
    }
    
    /**
     * 发送消息
     */
    private void sendMessage(String content) {
        if (content.trim().isEmpty()) {
            Toast.makeText(this, "消息内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        new SendMessageTask().execute(content, "TEXT");
    }

    /**
     * 发送消息的异步任务
     */
    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        private String messageContent;
        private String messageType;

        @Override
        protected Boolean doInBackground(String... params) {
            messageContent = params[0];
            messageType = params.length > 1 ? params[1] : "TEXT";
            try {
                // 获取当前用户ID
                currentUserId = AuthManager.getInstance(ChatActivity.this).getUserId();
                if (currentUserId == null) {
                    Log.e("ChatActivity", "当前用户ID为空");
                    return false;
                }
                
                // 调用发送消息API
                ApiClient.getInstance().sendMessage(ChatActivity.this, otherUserId, messageContent, messageType, 
                    new ApiClient.MessageCallback() {
                        @Override
                        public void onSuccess(MessageDTO message) {
                            Log.d("ChatActivity", "发送消息成功");
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e("ChatActivity", "发送消息失败: " + error);
                        }
                    });
                return true; // 暂时返回true，实际结果通过回调处理
                
            } catch (Exception e) {
                Log.e("ChatActivity", "发送消息失败", e);
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // 发送成功，添加到本地消息列表
                ChatMessage message = new ChatMessage(
                    null, currentUserId, otherUserId, messageContent,
                    messageType, true, String.valueOf(System.currentTimeMillis()),
                    "我"
                );
                messages.add(message);
                updateMessageDisplay();
                etMessageInput.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "发送消息失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 加载聊天记录
     */
    private void loadChatHistory() {
        new LoadChatHistoryTask().execute();
    }
    
    /**
     * 加载聊天记录的异步任务
     */
    private class LoadChatHistoryTask extends AsyncTask<Void, Void, List<ChatMessage>> {
        @Override
        protected List<ChatMessage> doInBackground(Void... params) {
            try {
                currentUserId = AuthManager.getInstance(ChatActivity.this).getUserId();
                if (currentUserId == null) {
                    Log.e("ChatActivity", "当前用户ID为空");
                    return new ArrayList<>();
                }

                final List<ChatMessage> loadedMessages = new ArrayList<>();

                // 调用获取聊天记录API
                ApiClient.getInstance().getChatHistory(ChatActivity.this, currentUserId, otherUserId,
                    new ApiClient.ChatHistoryCallback() {
                        @Override
                        public void onSuccess(List<MessageDTO> messageDTOs) {
                            Log.d("ChatActivity", "获取聊天记录成功，消息数量: " + messageDTOs.size());

                            // ✅ 将MessageDTO转换为ChatMessage
                            for (MessageDTO dto : messageDTOs) {
                                ChatMessage chatMessage = new ChatMessage(
                                    dto.getId(),
                                    dto.getSenderId(),
                                    dto.getReceiverId(),
                                    dto.getContent(),
                                    dto.getMessageType() != null ? dto.getMessageType() : "TEXT",
                                    dto.getIsRead() != null ? dto.getIsRead() : false,
                                    dto.getCreatedAt() != null ? dto.getCreatedAt() : String.valueOf(System.currentTimeMillis()),
                                    dto.getSenderNickname() != null ? dto.getSenderNickname() : "未知"
                                );
                                loadedMessages.add(chatMessage);
                            }

                            // ✅ 在主线程更新UI
                            runOnUiThread(() -> {
                                messages.clear();
                                messages.addAll(loadedMessages);
                                updateMessageDisplay();
                            });
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("ChatActivity", "获取聊天记录失败: " + error);
                            runOnUiThread(() -> {
                                Toast.makeText(ChatActivity.this, "加载聊天记录失败", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });

                // AsyncTask要求返回值，但实际数据通过回调处理
                return new ArrayList<>();

            } catch (Exception e) {
                Log.e("ChatActivity", "加载聊天记录失败", e);
            }

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<ChatMessage> messageList) {
            // 数据已在回调中处理，这里不需要再做什么
            Log.d("ChatActivity", "LoadChatHistoryTask onPostExecute 完成");
        }
    }
    
    /**
     * 更新消息显示
     */
    private void updateMessageDisplay() {
        Log.d("ChatActivity", "updateMessageDisplay 被调用，消息数量: " + messages.size());
        runOnUiThread(() -> {
            llMessageContainer.removeAllViews();

            for (int i = 0; i < messages.size(); i++) {
                ChatMessage message = messages.get(i);
                addMessageToView(message);
            }

            // 滚动到底部
            svMessages.post(() -> svMessages.fullScroll(View.FOCUS_DOWN));
        });
    }
    
    /**
     * 添加消息到视图
     */
    private void addMessageToView(ChatMessage message) {
        try {
            // 判断是否是当前用户发送的消息
            boolean isFromCurrentUser = message.isFromCurrentUser(currentUserId);

            // 根据消息来源选择不同的布局
            int layoutId = isFromCurrentUser ? R.layout.item_message_sent : R.layout.item_message_received;

            View messageView = getLayoutInflater().inflate(layoutId, llMessageContainer, false);

            // 设置消息内容
            TextView tvContent = messageView.findViewById(R.id.tv_message_content);
            if (tvContent != null) {
                tvContent.setText(message.content);
            } else {
                Log.e("ChatActivity", "tv_message_content 未找到!");
            }

            // 设置时间戳
            TextView tvTimestamp = messageView.findViewById(R.id.tv_timestamp);
            if (tvTimestamp != null && message.timestamp != null) {
                String timeStr = formatTimestamp(message.timestamp);
                tvTimestamp.setText(timeStr);
            }

            // 如果是接收的消息，设置发送者姓名
            if (!isFromCurrentUser) {
                TextView tvSenderName = messageView.findViewById(R.id.tv_sender_name);
                if (tvSenderName != null && message.senderName != null) {
                    tvSenderName.setText(message.senderName);
                }
            }

            // 添加到消息容器
            llMessageContainer.addView(messageView);

        } catch (Exception e) {
            Log.e("ChatActivity", "渲染消息失败", e);
        }
    }

    /**
     * 格式化时间戳为可读格式
     */
    private String formatTimestamp(String timestamp) {
        try {
            long time = Long.parseLong(timestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(time));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 礼物数据模型
     */
    public static class Gift {
        public String id;
        public String name;
        public String icon;
        public int price;

        public Gift(String id, String name, String icon, int price) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.price = price;
        }
    }

    /**
     * 获取礼物列表
     */
    private List<Gift> getGiftList() {
        List<Gift> gifts = new ArrayList<>();
        gifts.add(new Gift("rose", "玫瑰", "🌹", 10));
        gifts.add(new Gift("heart", "爱心", "❤️", 20));
        gifts.add(new Gift("cake", "蛋糕", "🎂", 50));
        gifts.add(new Gift("diamond", "钻石", "💎", 100));
        gifts.add(new Gift("crown", "皇冠", "👑", 200));
        gifts.add(new Gift("rocket", "火箭", "🚀", 500));
        gifts.add(new Gift("car", "跑车", "🏎️", 1000));
        gifts.add(new Gift("mansion", "豪宅", "🏰", 5000));
        return gifts;
    }

    /**
     * 设置图片选择器
     */
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        sendImageMessage(imageUri);
                    }
                }
            }
        );
    }

    /**
     * 显示礼物选择对话框
     */
    private void showGiftDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gift_selection);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView rvGifts = dialog.findViewById(R.id.rv_gifts);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        // 设置礼物列表
        List<Gift> gifts = getGiftList();
        rvGifts.setLayoutManager(new GridLayoutManager(this, 4));
        rvGifts.setAdapter(new GiftAdapter(gifts, gift -> {
            sendGiftMessage(gift);
            dialog.dismiss();
        }));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * 打开图片选择器
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    /**
     * 发送礼物消息
     */
    private void sendGiftMessage(Gift gift) {
        String giftContent = "[礼物]" + gift.icon + " " + gift.name + " (价值" + gift.price + "金币)";
        new SendMessageTask().execute(giftContent, "GIFT");
        Toast.makeText(this, "已发送礼物：" + gift.name, Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送图片消息
     */
    private void sendImageMessage(Uri imageUri) {
        Toast.makeText(this, "正在上传图片...", Toast.LENGTH_SHORT).show();
        new UploadImageTask().execute(imageUri);
    }

    /**
     * 上传图片的异步任务
     */
    private class UploadImageTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... uris) {
            try {
                Uri imageUri = uris[0];

                // 创建MultipartBody.Part
                java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    return null;
                }

                // 读取图片数据
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                inputStream.close();

                // 创建RequestBody
                okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse("image/*"),
                    bytes
                );

                // 获取文件名
                String fileName = "image_" + System.currentTimeMillis() + ".jpg";
                okhttp3.MultipartBody.Part filePart = okhttp3.MultipartBody.Part.createFormData(
                    "file",
                    fileName,
                    requestBody
                );

                // 调用上传接口
                ApiService apiService = com.example.myapplication.network.NetworkConfig.getRetrofit().create(ApiService.class);
                retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response =
                    apiService.uploadChatImage(filePart).execute();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    return response.body().getData();
                } else {
                    Log.e("ChatActivity", "图片上传失败: " + (response.body() != null ? response.body().getMessage() : "未知错误"));
                    return null;
                }

            } catch (Exception e) {
                Log.e("ChatActivity", "图片上传异常", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            if (imageUrl != null) {
                Log.d("ChatActivity", "图片上传成功: " + imageUrl);
                // 发送包含图片URL的消息
                String imageContent = "[图片]" + imageUrl;
                new SendMessageTask().execute(imageContent, "IMAGE");
                Toast.makeText(ChatActivity.this, "图片已发送", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ChatActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 礼物适配器
     */
    private class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        private List<Gift> gifts;
        private OnGiftClickListener listener;

        interface OnGiftClickListener {
            void onGiftClick(Gift gift);
        }

        public GiftAdapter(List<Gift> gifts, OnGiftClickListener listener) {
            this.gifts = gifts;
            this.listener = listener;
        }

        @Override
        public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gift, parent, false);
            return new GiftViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GiftViewHolder holder, int position) {
            Gift gift = gifts.get(position);
            holder.tvIcon.setText(gift.icon);
            holder.tvName.setText(gift.name);
            holder.tvPrice.setText(gift.price + "金币");
            holder.itemView.setOnClickListener(v -> listener.onGiftClick(gift));
        }

        @Override
        public int getItemCount() {
            return gifts.size();
        }

        class GiftViewHolder extends RecyclerView.ViewHolder {
            TextView tvIcon, tvName, tvPrice;

            public GiftViewHolder(View itemView) {
                super(itemView);
                tvIcon = itemView.findViewById(R.id.tv_gift_icon);
                tvName = itemView.findViewById(R.id.tv_gift_name);
                tvPrice = itemView.findViewById(R.id.tv_gift_price);
            }
        }
    }
}
