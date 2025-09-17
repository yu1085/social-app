package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GenderSelectionActivity extends AppCompatActivity {
    
    private Button btnMale, btnFemale;
    private TextView tvTitle, tvSubtitle;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_selection);
        
        initViews();
        setClickListeners();
    }
    
    private void initViews() {
        btnMale = findViewById(R.id.btn_male);
        btnFemale = findViewById(R.id.btn_female);
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
    }
    
    private void setClickListeners() {
        btnMale.setOnClickListener(v -> selectGender("MALE"));
        btnFemale.setOnClickListener(v -> selectGender("FEMALE"));
    }
    
    private void selectGender(String gender) {
        // 保存性别到SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_gender", gender);
        editor.apply();
        
        // 跳转到主界面
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}