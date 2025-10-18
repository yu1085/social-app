package com.example.myapplication;

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

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

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
    
    // 聊天相关
    private EditText etMessageInput;
    private LinearLayout llMessageContainer;
    private ScrollView svMessages;
    private Long currentUserId;
    private Long otherUserId;
    private String otherUserName;
    private List<ChatMessage> messages = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // 获取传递的用户数据
        String userName = getIntent().getStringExtra("user_name");
        String userAvatar = getIntent().getStringExtra("user_avatar");
        
        initViews();
        setupClickListeners();
        populateUserData(userName, userAvatar);
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
            Toast.makeText(this, "选择礼物", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "发送图片", Toast.LENGTH_SHORT).show();
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
        
        new SendMessageTask().execute(content);
    }
    
    /**
     * 发送消息的异步任务
     */
    private class SendMessageTask extends AsyncTask<String, Void, Boolean> {
        private String messageContent;
        
        @Override
        protected Boolean doInBackground(String... params) {
            messageContent = params[0];
            try {
                // 获取当前用户ID
                currentUserId = AuthManager.getInstance(ChatActivity.this).getUserId();
                if (currentUserId == null) {
                    Log.e("ChatActivity", "当前用户ID为空");
                    return false;
                }
                
                // 调用发送消息API
                ApiClient.getInstance().sendMessage(ChatActivity.this, otherUserId, messageContent, "TEXT", 
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
                    "TEXT", true, String.valueOf(System.currentTimeMillis()), 
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
                
                // 调用获取聊天记录API
                ApiClient.getInstance().getChatHistory(ChatActivity.this, currentUserId, otherUserId, 
                    new ApiClient.ChatHistoryCallback() {
                        @Override
                        public void onSuccess(List<MessageDTO> messages) {
                            Log.d("ChatActivity", "获取聊天记录成功，消息数量: " + messages.size());
                            // 这里需要将MessageDTO转换为ChatMessage并更新UI
                            // 由于这是异步回调，需要在主线程更新UI
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.e("ChatActivity", "获取聊天记录失败: " + error);
                        }
                    });
                
                // 暂时返回空列表，实际数据通过回调处理
                return new ArrayList<>();
                
            } catch (Exception e) {
                Log.e("ChatActivity", "加载聊天记录失败", e);
            }
            
            return new ArrayList<>();
        }
        
        @Override
        protected void onPostExecute(List<ChatMessage> messageList) {
            messages = messageList;
            updateMessageDisplay();
        }
    }
    
    /**
     * 更新消息显示
     */
    private void updateMessageDisplay() {
        runOnUiThread(() -> {
            llMessageContainer.removeAllViews();
            
            for (ChatMessage message : messages) {
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
        // 这里需要根据你的布局文件来实现
        // 暂时用Toast显示
        String displayText = message.senderName + ": " + message.content;
        Log.d("ChatActivity", "显示消息: " + displayText);
    }
}
