package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class GiftsActivity extends AppCompatActivity {
    
    private TextView tvTitle;
    private ImageView ivBack;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifts);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        tvTitle = findViewById(R.id.tv_gifts_title);
        ivBack = findViewById(R.id.iv_back_gifts);
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            finish(); // 返回上一页
        });
    }
}
