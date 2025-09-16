package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.network.NetworkService;
import com.example.myapplication.auth.AuthManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText etPhone, etVerificationCode;
    private Button btnLogin, btnSendCode;
    private ProgressDialog progressDialog;
    private NetworkService networkService;
    private CountDownTimer countDownTimer;
    private boolean isCodeSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        initNetworkService();
        setClickListeners();
    }
    
    private void initViews() {
        etPhone = findViewById(R.id.et_phone);
        etVerificationCode = findViewById(R.id.et_verification_code);
        btnLogin = findViewById(R.id.btn_login);
        btnSendCode = findViewById(R.id.btn_send_code);
        
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("处理中...");
        progressDialog.setCancelable(false);
    }
    
    private void initNetworkService() {
        networkService = NetworkService.getInstance(this);
    }
    
    private void setClickListeners() {
        btnLogin.setOnClickListener(v -> loginOrRegister());
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
    }
    
    private void sendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("请输入手机号");
            etPhone.requestFocus();
            return;
        }
        
        if (!isValidPhone(phone)) {
            etPhone.setError("请输入正确的手机号");
            etPhone.requestFocus();
            return;
        }
        
        // 开始倒计时
        startCountDown();
        
        // 发送验证码请求
        networkService.sendVerificationCode(phone, new NetworkService.NetworkCallback<String>() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                    isCodeSent = true;
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // 后端不可用时，显示固定验证码
                    Toast.makeText(LoginActivity.this, "后端不可用，使用测试验证码：123456", Toast.LENGTH_LONG).show();
                    isCodeSent = true;
                });
            }
        });
    }
    
    private void loginOrRegister() {
        if (!validateInput()) {
            return;
        }

        String phone = etPhone.getText().toString().trim();
        String code = etVerificationCode.getText().toString().trim();


        if (!isCodeSent) {
            Toast.makeText(this, "请先获取验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否是测试验证码
        if ("123456".equals(code)) {
            // 测试模式登录 - 使用真实API调用
            Log.d(TAG, "测试模式登录，手机号: " + phone);
            progressDialog.show();
            
            // 使用验证码登录/注册
            networkService.loginWithVerificationCode(phone, code, new NetworkService.NetworkCallback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse loginResponse) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "测试登录成功！", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "测试登录成功，用户ID: " + loginResponse.getUser().getId());
                        Log.d(TAG, "用户昵称: " + loginResponse.getUser().getNickname());

                        // 登录成功后跳转到性别选择界面
                        Intent intent = new Intent(LoginActivity.this, GenderSelectionActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "测试登录失败: " + error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "测试登录失败: " + error);
                    });
                }
            });
            return;
        }

        progressDialog.show();

        // 使用验证码登录/注册
        networkService.loginWithVerificationCode(phone, code, new NetworkService.NetworkCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "登录成功，用户ID: " + loginResponse.getUser().getId());

                    // 登录成功后跳转到性别选择界面
                    Intent intent = new Intent(LoginActivity.this, GenderSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登录失败: " + error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "登录失败: " + error);
                });
            }
        });
    }
    
    private boolean validateInput() {
        String phone = etPhone.getText().toString().trim();
        String code = etVerificationCode.getText().toString().trim();
        
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("请输入手机号");
            etPhone.requestFocus();
            return false;
        }
        
        if (!isValidPhone(phone)) {
            etPhone.setError("请输入正确的手机号");
            etPhone.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(code)) {
            etVerificationCode.setError("请输入验证码");
            etVerificationCode.requestFocus();
            return false;
        }
        
        if (code.length() != 6) {
            etVerificationCode.setError("请输入6位验证码");
            etVerificationCode.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidPhone(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    private void startCountDown() {
        btnSendCode.setEnabled(false);
        btnSendCode.setText("60s后重发");
        
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnSendCode.setText((millisUntilFinished / 1000) + "s后重发");
            }
            
            @Override
            public void onFinish() {
                resetSendCodeButton();
            }
        };
        countDownTimer.start();
    }
    
    private void resetSendCodeButton() {
        btnSendCode.setEnabled(true);
        btnSendCode.setText("获取验证码");
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
