package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class LastWeekContributionActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private ImageView ivBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_week_contribution);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_last_week_title);
        ivBack = findViewById(R.id.iv_back_last_week);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            finish(); // 返回上一页
        });
    }
}
