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
import cn.jpush.android.api.JPushInterface;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.LoginRequest;
import com.example.myapplication.dto.LoginResponse;
import com.example.myapplication.network.NetworkConfig;
import com.example.myapplication.auth.AuthManager;
import com.example.myapplication.utils.SafeToast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText etPhone, etVerificationCode;
    private Button btnLogin, btnSendCode;
    private ProgressDialog progressDialog;
    private CountDownTimer countDownTimer;
    private boolean isCodeSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
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
    
    private void setClickListeners() {
        btnLogin.setOnClickListener(v -> loginOrRegister());
        btnSendCode.setOnClickListener(v -> sendVerificationCode());
    }
    
    private void sendVerificationCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            SafeToast.showShort(this, "请输入手机号");
            return;
        }
        
        if (!isValidPhone(phone)) {
            SafeToast.showShort(this, "请输入正确的手机号");
            return;
        }
        
        // 开始倒计时
        startCountDown();
        
        // 发送验证码请求
        new Thread(() -> {
            try {
                Log.d(TAG, "开始发送验证码请求，手机号: " + phone);
                var call = NetworkConfig.getApiService().sendVerificationCode(phone);
                Log.d(TAG, "验证码请求已创建，开始执行");
                var response = call.execute();
                Log.d(TAG, "验证码请求执行完成，状态码: " + response.code());
                
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    
                    if (response.isSuccessful() && response.body() != null) {
                        var apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            SafeToast.showShort(LoginActivity.this, "验证码已发送");
                            isCodeSent = true;
                        } else {
                            SafeToast.showShort(LoginActivity.this, "发送失败: " + apiResponse.getMessage());
                        }
                    } else {
                        // 后端不可用时，显示固定验证码
                        SafeToast.showLong(LoginActivity.this, "后端不可用，请检查网络连接");
                        isCodeSent = true;
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    // 后端不可用时，显示固定验证码
                    Toast.makeText(LoginActivity.this, "后端不可用，请检查网络连接", Toast.LENGTH_LONG).show();
                    isCodeSent = true;
                });
            }
        }).start();
    }
    
    private void loginOrRegister() {
        if (!validateInput()) {
            return;
        }

        String phone = etPhone.getText().toString().trim();
        String code = etVerificationCode.getText().toString().trim();

        if (!isCodeSent) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "请先获取验证码", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // 检查是否是测试验证码
        if (isValidTestCode(code)) {
            // 测试模式登录 - 使用真实API调用
            Log.d(TAG, "测试模式登录，手机号: " + phone);
            progressDialog.show();
            
            // 使用验证码登录/注册
            new Thread(() -> {
                try {
                    var call = NetworkConfig.getApiService().loginWithVerificationCode(phone, code);
                    var response = call.execute();
                    
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        
                        progressDialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            var apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                LoginResponse loginResponse = apiResponse.getData();
                                Toast.makeText(LoginActivity.this, "测试登录成功！", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "测试登录成功，用户ID: " + loginResponse.getUser().getId());
                                Log.d(TAG, "用户昵称: " + loginResponse.getUser().getNickname());

                                // 保存登录信息
                                AuthManager.getInstance(this).saveToken(loginResponse.getToken());
                                AuthManager.getInstance(this).saveUserId(loginResponse.getUser().getId());

                                // 上传JPush Registration ID
                                uploadJPushRegistrationId();

                                // 登录成功后直接跳转到主界面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "测试登录失败: " + apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e(TAG, "测试登录失败: " + apiResponse.getMessage());
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "测试登录失败: 网络错误", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "测试登录失败: 网络错误");
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "测试登录失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "测试登录失败: " + e.getMessage());
                    });
                }
            }).start();
            return;
        }

        progressDialog.show();

        // 使用验证码登录/注册
        new Thread(() -> {
            try {
                var call = NetworkConfig.getApiService().loginWithVerificationCode(phone, code);
                var response = call.execute();
                
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        var apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            LoginResponse loginResponse = apiResponse.getData();
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "登录成功，用户ID: " + loginResponse.getUser().getId());

                            // 保存登录信息
                            AuthManager.getInstance(this).saveToken(loginResponse.getToken());
                            AuthManager.getInstance(this).saveUserId(loginResponse.getUser().getId());

                            // 上传JPush Registration ID
                            uploadJPushRegistrationId();

                            // 登录成功后直接跳转到主界面
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败: 网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登录失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private boolean validateInput() {
        String phone = etPhone.getText().toString().trim();
        String code = etVerificationCode.getText().toString().trim();
        
        if (TextUtils.isEmpty(phone)) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            }
            etPhone.requestFocus();
            return false;
        }
        
        if (!isValidPhone(phone)) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            }
            etPhone.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(code)) {
            if (!isFinishing() && !isDestroyed()) {
                Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            }
            etVerificationCode.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidPhone(String phone) {
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    private boolean isValidTestCode(String code) {
        // 在开发环境中允许特定的测试验证码
        return "123456".equals(code) || "000000".equals(code);
    }

    /**
     * 上传JPush Registration ID到后端
     */
    private void uploadJPushRegistrationId() {
        new Thread(() -> {
            try {
                // 获取JPush Registration ID
                String registrationId = JPushInterface.getRegistrationID(getApplicationContext());

                if (registrationId == null || registrationId.trim().isEmpty()) {
                    Log.w(TAG, "JPush Registration ID 为空，稍后将通过广播上传");
                    return;
                }

                Log.d(TAG, "准备上传 Registration ID: " + registrationId);

                // 获取Token
                String token = AuthManager.getInstance(this).getAuthHeader();
                if (token == null) {
                    Log.e(TAG, "Token为空，无法上传 Registration ID");
                    return;
                }

                // 调用后端API上传
                retrofit2.Call<ApiResponse<String>> call =
                    NetworkConfig.getApiService().updateRegistrationId(token, registrationId);

                retrofit2.Response<ApiResponse<String>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Log.i(TAG, "✅ Registration ID 上传成功: " + registrationId);
                    } else {
                        Log.e(TAG, "❌ Registration ID 上传失败: " + response.body().getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Registration ID 上传请求失败: " + response.code());
                }

            } catch (Exception e) {
                Log.e(TAG, "上传 Registration ID 异常", e);
            }
        }).start();
    }

    private void startCountDown() {
        btnSendCode.setEnabled(false);
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnSendCode.setText("重新发送(" + (millisUntilFinished / 1000) + "s)");
            }
            
            @Override
            public void onFinish() {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("发送验证码");
            }
        };
        countDownTimer.start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}