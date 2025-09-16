package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.PostDTO;
import com.example.myapplication.network.NetworkService;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    
    private EditText etUsername, etPassword, etNickname;
    private Button btnLogin, btnGetProfile, btnSearchUsers, btnGetPosts;
    private TextView tvResult;
    
    private NetworkService networkService;
    private AuthManager authManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        initViews();
        initServices();
        setupListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etNickname = findViewById(R.id.et_nickname);
        btnLogin = findViewById(R.id.btn_login);
        btnGetProfile = findViewById(R.id.btn_get_profile);
        btnSearchUsers = findViewById(R.id.btn_search_users);
        btnGetPosts = findViewById(R.id.btn_get_posts);
        tvResult = findViewById(R.id.tv_result);
    }
    
    private void initServices() {
        networkService = NetworkService.getInstance(this);
        authManager = AuthManager.getInstance(this);
    }
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> testLogin());
        btnGetProfile.setOnClickListener(v -> testGetProfile());
        btnSearchUsers.setOnClickListener(v -> testSearchUsers());
        btnGetPosts.setOnClickListener(v -> testGetPosts());
    }
    
    private void testLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        LoginRequest request = new LoginRequest(username, password);
        networkService.login(request, new NetworkService.NetworkCallback<com.example.myapplication.dto.LoginResponse>() {
            @Override
            public void onSuccess(com.example.myapplication.dto.LoginResponse data) {
                runOnUiThread(() -> {
                    tvResult.setText("登录成功!\n" +
                            "用户名: " + data.getUser().getUsername() + "\n" +
                            "昵称: " + data.getUser().getNickname() + "\n" +
                            "性别: " + data.getUser().getGender() + "\n" +
                            "余额: " + data.getUser().getBalance());
                    Toast.makeText(TestActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvResult.setText("登录失败: " + error);
                    Toast.makeText(TestActivity.this, "登录失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    
    private void testGetProfile() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        
        networkService.getProfile(new NetworkService.NetworkCallback<UserDTO>() {
            @Override
            public void onSuccess(UserDTO data) {
                runOnUiThread(() -> {
                    tvResult.setText("获取用户信息成功!\n" +
                            "用户ID: " + data.getId() + "\n" +
                            "用户名: " + data.getUsername() + "\n" +
                            "昵称: " + data.getNickname() + "\n" +
                            "性别: " + data.getGender() + "\n" +
                            "位置: " + data.getLocation() + "\n" +
                            "余额: " + data.getBalance());
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvResult.setText("获取用户信息失败: " + error);
                    Toast.makeText(TestActivity.this, "获取用户信息失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void testSearchUsers() {
        networkService.searchUsers("", "FEMALE", "", null, null, 0, 10, 
                new NetworkService.NetworkCallback<List<UserDTO>>() {
                    @Override
                    public void onSuccess(List<UserDTO> data) {
                        runOnUiThread(() -> {
                            StringBuilder result = new StringBuilder("搜索用户成功!\n找到 " + data.size() + " 个用户:\n");
                            for (UserDTO user : data) {
                                result.append("- ").append(user.getNickname())
                                        .append(" (").append(user.getGender()).append(")\n");
                            }
                            tvResult.setText(result.toString());
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            tvResult.setText("搜索用户失败: " + error);
                            Toast.makeText(TestActivity.this, "搜索用户失败: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
    
    private void testGetPosts() {
        networkService.getPosts(0, 10, new NetworkService.NetworkCallback<List<PostDTO>>() {
            @Override
            public void onSuccess(List<PostDTO> data) {
                runOnUiThread(() -> {
                    StringBuilder result = new StringBuilder("获取动态列表成功!\n找到 " + data.size() + " 条动态:\n");
                    for (PostDTO post : data) {
                        result.append("- ").append(post.getUserNickname())
                                .append(": ").append(post.getContent().substring(0, 
                                        Math.min(30, post.getContent().length()))).append("...\n");
                    }
                    tvResult.setText(result.toString());
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvResult.setText("获取动态列表失败: " + error);
                    Toast.makeText(TestActivity.this, "获取动态列表失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
