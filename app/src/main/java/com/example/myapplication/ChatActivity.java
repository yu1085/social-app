package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

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
}
