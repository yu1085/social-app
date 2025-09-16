package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class GuardianActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private ImageView ivBack;
    private LinearLayout llThisWeekGuardian;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_guardian_title);
        ivBack = findViewById(R.id.iv_back_guardian);
        llThisWeekGuardian = findViewById(R.id.ll_this_week_guardian);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            finish(); // 返回上一页
        });
        
        llThisWeekGuardian.setOnClickListener(v -> {
            startLastWeekContributionActivity();
        });
    }
    
    private void startLastWeekContributionActivity() {
        // 跳转到上周贡献榜界面
        android.content.Intent intent = new android.content.Intent(this, LastWeekContributionActivity.class);
        startActivity(intent);
    }
}
