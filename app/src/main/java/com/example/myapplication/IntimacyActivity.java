package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.content.Intent;

public class IntimacyActivity extends AppCompatActivity {
    
    private ImageView ivBack;
    private TextView tvTitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intimacy);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        // 为每个亲密项目设置点击事件，跳转到用户详情页
        setupIntimacyItemClickListeners();
    }
    
    private void setupIntimacyItemClickListeners() {
        // 为每个亲密项目设置点击事件
        for (int i = 1; i <= 12; i++) {
            LinearLayout item = findViewById(getIntimacyItemId(i));
            if (item != null) {
                final int index = i;
                item.setOnClickListener(v -> {
                    // 跳转到用户详情页
                    Intent intent = new Intent(IntimacyActivity.this, UserDetailActivity.class);

                    // ✅ 修复：添加 user_id 传递（使用测试用户ID）
                    // 使用现有测试用户：23820512, 23820513, 23820516, 23820517
                    long userId = 23820512L + (index % 4);  // 循环使用4个测试用户
                    intent.putExtra("user_id", userId);

                    intent.putExtra("user_name", "亲密用户" + index);
                    intent.putExtra("user_status", index % 3 == 0 ? "离线" : "在线");
                    intent.putExtra("user_age", (22 + index) + "岁");
                    intent.putExtra("user_location", index % 3 == 0 ? "北京" : (index % 3 == 1 ? "上海" : "深圳"));
                    intent.putExtra("user_description", "这是一个可爱的亲密用户");
                    intent.putExtra("user_avatar", R.drawable.group_27);
                    startActivity(intent);
                });
            }
        }
    }
    
    private int getIntimacyItemId(int index) {
        // 根据索引返回对应的资源ID
        switch (index) {
            case 1: return R.id.intimacy_item_1;
            case 2: return R.id.intimacy_item_2;
            case 3: return R.id.intimacy_item_3;
            case 4: return R.id.intimacy_item_4;
            case 5: return R.id.intimacy_item_5;
            case 6: return R.id.intimacy_item_6;
            case 7: return R.id.intimacy_item_7;
            case 8: return R.id.intimacy_item_8;
            case 9: return R.id.intimacy_item_9;
            case 10: return R.id.intimacy_item_10;
            case 11: return R.id.intimacy_item_11;
            case 12: return R.id.intimacy_item_12;
            default: return 0;
        }
    }
}
