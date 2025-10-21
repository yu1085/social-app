package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import com.example.myapplication.dto.UserDTO;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntimacyActivity extends AppCompatActivity {

    private static final String TAG = "IntimacyActivity";

    private ImageView ivBack;
    private TextView tvTitle;

    // API数据
    private List<UserDTO> intimacyUsers = new ArrayList<>();
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intimacy);

        // 初始化API服务
        apiService = RetrofitClient.INSTANCE.create(ApiService.class);

        initViews();
        setupClickListeners();

        // 加载亲密列表
        loadIntimacy();
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
    
    private void loadIntimacy() {
        Log.d(TAG, "开始加载亲密列表");

        apiService.getIntimate(0, 20).enqueue(new Callback<ApiResponse<List<UserDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserDTO>>> call, Response<ApiResponse<List<UserDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    intimacyUsers = response.body().getData();
                    Log.d(TAG, "成功加载 " + intimacyUsers.size() + " 个亲密用户");

                    // 重新设置点击事件
                    runOnUiThread(() -> setupIntimacyItemClickListeners());
                } else {
                    Log.e(TAG, "加载亲密列表失败: " + (response.body() != null ? response.body().getMessage() : "Unknown error"));
                    Toast.makeText(IntimacyActivity.this, "加载亲密列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserDTO>>> call, Throwable t) {
                Log.e(TAG, "加载亲密列表异常", t);
                Toast.makeText(IntimacyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupIntimacyItemClickListeners() {
        // 为每个亲密项目设置点击事件
        for (int i = 1; i <= 12; i++) {
            LinearLayout item = findViewById(getIntimacyItemId(i));
            if (item != null) {
                final int index = i - 1;  // 转换为0-based index
                item.setOnClickListener(v -> {
                    // 跳转到用户详情页
                    Intent intent = new Intent(IntimacyActivity.this, UserDetailActivity.class);

                    // ✅ 使用API返回的真实user_id和属性（不再硬编码）
                    if (index < intimacyUsers.size()) {
                        UserDTO user = intimacyUsers.get(index);
                        intent.putExtra("user_id", user.getId() != null ? user.getId() : 0L);
                        intent.putExtra("user_name", user.getNickname() != null ? user.getNickname() : user.getUsername());
                        intent.putExtra("user_status", user.getStatus() != null ? user.getStatus() : "OFFLINE");
                        intent.putExtra("user_age", user.getAge() != null ? String.valueOf(user.getAge()) : "");
                        intent.putExtra("user_location", user.getLocation() != null ? user.getLocation() : "未知");
                        intent.putExtra("user_description", user.getSignature() != null ? user.getSignature() : "这是一个可爱的亲密用户");
                        intent.putExtra("user_avatar", R.drawable.group_27); // TODO: 后续使用user.getAvatarUrl()
                    } else {
                        // 如果API数据不足，使用默认值
                        intent.putExtra("user_id", 0L);
                        intent.putExtra("user_name", "亲密用户" + (index + 1));
                        intent.putExtra("user_status", "在线");
                        intent.putExtra("user_age", "25");
                        intent.putExtra("user_location", "北京");
                        intent.putExtra("user_description", "这是一个可爱的亲密用户");
                        intent.putExtra("user_avatar", R.drawable.group_27);
                    }

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
