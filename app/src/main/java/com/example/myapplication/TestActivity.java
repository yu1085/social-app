package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.network.NetworkConfig;
import com.example.myapplication.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private TextView resultText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        resultText = findViewById(R.id.result_text);
        Button testButton = findViewById(R.id.test_button);
        
        // 初始化API服务
        apiService = NetworkConfig.getApiService();
        
        testButton.setOnClickListener(v -> testBackendConnection());
    }
    
    private void testBackendConnection() {
        resultText.setText("正在测试后端连接...");
        
        // 测试健康检查接口
        Call<Object> healthCall = apiService.getHealth();
        healthCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    resultText.setText("✅ 后端连接成功！\n状态码: " + response.code());
                    Log.d(TAG, "Backend connection successful: " + response.code());
                } else {
                    resultText.setText("❌ 后端连接失败\n状态码: " + response.code());
                    Log.e(TAG, "Backend connection failed: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                resultText.setText("❌ 网络连接失败\n错误: " + t.getMessage());
                Log.e(TAG, "Network connection failed", t);
            }
        });
    }
}