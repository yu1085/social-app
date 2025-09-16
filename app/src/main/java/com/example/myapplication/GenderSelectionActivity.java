package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.network.NetworkService;

public class GenderSelectionActivity extends AppCompatActivity {
    private static final String TAG = "GenderSelectionActivity";
    
    private LinearLayout maleOption, femaleOption;
    private Button btnConfirm;
    private TextView tvTitle, tvSubtitle;
    private String selectedGender = "";
    private AuthManager authManager;
    private NetworkService networkService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_selection);
        
        initViews();
        initServices();
        setClickListeners();
    }
    
    private void initViews() {
        maleOption = findViewById(R.id.male_option);
        femaleOption = findViewById(R.id.female_option);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        
        // 设置标题和副标题
        tvTitle.setText("选择您的性别");
        tvSubtitle.setText("这将帮助我们为您推荐更合适的内容");
    }
    
    private void initServices() {
        authManager = AuthManager.getInstance(this);
        networkService = NetworkService.getInstance(this);
    }
    
    private void setClickListeners() {
        // 男性选项点击
        maleOption.setOnClickListener(v -> {
            selectGender("MALE");
            maleOption.setSelected(true);
            femaleOption.setSelected(false);
            updateButtonState();
        });
        
        // 女性选项点击
        femaleOption.setOnClickListener(v -> {
            selectGender("FEMALE");
            femaleOption.setSelected(true);
            maleOption.setSelected(false);
            updateButtonState();
        });
        
        // 确认按钮点击
        btnConfirm.setOnClickListener(v -> {
            if (!selectedGender.isEmpty()) {
                updateUserGender();
            } else {
                Toast.makeText(this, "请选择您的性别", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void selectGender(String gender) {
        selectedGender = gender;
        Log.d(TAG, "选择性别: " + gender);
    }
    
    private void updateButtonState() {
        btnConfirm.setEnabled(!selectedGender.isEmpty());
        btnConfirm.setAlpha(selectedGender.isEmpty() ? 0.5f : 1.0f);
    }
    
    private void updateUserGender() {
        // 显示加载状态
        btnConfirm.setEnabled(false);
        btnConfirm.setText("处理中...");
        
        // 创建用户更新请求
        UserDTO userUpdate = new UserDTO();
        userUpdate.setGender(selectedGender);
        
        // 暂时直接跳转到主界面，后续可以添加更新用户性别的API
        Log.d(TAG, "选择性别: " + selectedGender);
        Toast.makeText(GenderSelectionActivity.this, "性别设置成功", Toast.LENGTH_SHORT).show();
        
        // 跳转到主界面（基本信息已由后端自动生成）
        Intent intent = new Intent(GenderSelectionActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void testApiCalls() {
        Log.d(TAG, "开始测试API调用...");
        
        // 测试获取用户信息
        networkService.getProfile(new NetworkService.NetworkCallback<UserDTO>() {
            @Override
            public void onSuccess(UserDTO user) {
                Log.d(TAG, "获取用户信息成功: " + user.getNickname());
                Log.d(TAG, "用户ID: " + user.getId());
                Log.d(TAG, "用户昵称: " + user.getNickname());
                Log.d(TAG, "用户性别: " + user.getGender());
                Toast.makeText(GenderSelectionActivity.this, 
                    "获取用户信息成功:\n昵称: " + user.getNickname() + 
                    "\nID: " + user.getId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "获取用户信息失败: " + error);
                Toast.makeText(GenderSelectionActivity.this, "获取用户信息失败: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        // 不允许返回，必须选择性别
        Toast.makeText(this, "请选择您的性别以继续", Toast.LENGTH_SHORT).show();
    }
}
