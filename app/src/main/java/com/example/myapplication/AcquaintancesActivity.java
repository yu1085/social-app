package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
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

public class AcquaintancesActivity extends AppCompatActivity {

    private static final String TAG = "AcquaintancesActivity";

    private ImageView ivBack;
    private ImageView ivSettings;
    private TextView tvDistanceFilter;
    private LinearLayout llCityFilter;
    private LinearLayout llLetterFilter;
    private LinearLayout llWhoAddedMe;
    private LinearLayout llViewWhoAddedMe;

    // 选择模式相关
    private boolean isSelectionMode = false;
    private List<LinearLayout> acquaintanceItems = new ArrayList<>();
    private List<Boolean> selectedItems = new ArrayList<>();

    // API数据
    private List<UserDTO> acquaintanceUsers = new ArrayList<>();
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquaintances);

        // 初始化API服务
        apiService = RetrofitClient.INSTANCE.create(ApiService.class);

        initViews();
        setupClickListeners();

        // 加载知友列表
        loadAcquaintances();
    }
    
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivSettings = findViewById(R.id.iv_settings);
        tvDistanceFilter = findViewById(R.id.tv_distance_filter);
        llCityFilter = findViewById(R.id.ll_city_filter);
        llLetterFilter = findViewById(R.id.ll_letter_filter);
        llWhoAddedMe = findViewById(R.id.ll_who_added_me);
        llViewWhoAddedMe = findViewById(R.id.ll_view_who_added_me);
        
        // 初始化知友项目列表
        initAcquaintanceItems();
        
        // 设置删除按钮点击事件
        setupDeleteButton();
    }
    
    private void initAcquaintanceItems() {
        // 查找所有知友项目
        acquaintanceItems.clear();
        
        // 根据布局中的ID查找知友项目
        for (int i = 1; i <= 2; i++) {
            LinearLayout item = findViewById(getAcquaintanceItemId(i));
            if (item != null) {
                acquaintanceItems.add(item);
                selectedItems.add(false);
            }
        }
    }
    
    private int getAcquaintanceItemId(int index) {
        // 根据索引返回对应的资源ID
        switch (index) {
            case 1: return R.id.acquaintance_item_1;
            case 2: return R.id.acquaintance_item_2;
            default: return 0;
        }
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        ivSettings.setOnClickListener(v -> {
            toggleSelectionMode();
        });
        
        tvDistanceFilter.setOnClickListener(v -> {
            Toast.makeText(this, "距离筛选", Toast.LENGTH_SHORT).show();
        });
        
        llCityFilter.setOnClickListener(v -> {
            Toast.makeText(this, "城市筛选", Toast.LENGTH_SHORT).show();
        });
        
        llLetterFilter.setOnClickListener(v -> {
            Toast.makeText(this, "首字母筛选", Toast.LENGTH_SHORT).show();
        });
        
        llViewWhoAddedMe.setOnClickListener(v -> {
            Toast.makeText(this, "查看谁加我为知友", Toast.LENGTH_SHORT).show();
        });
        
        // 为知友项目设置点击事件，跳转到用户详情页
        setupAcquaintanceItemClickListeners();
    }
    
    private void toggleSelectionMode() {
        isSelectionMode = !isSelectionMode;
        
        if (isSelectionMode) {
            // 进入选择模式
            ivSettings.setImageResource(R.drawable.ic_check);
            setupSelectionMode();
        } else {
            // 退出选择模式
            ivSettings.setImageResource(R.drawable.ic_settings);
            exitSelectionMode();
        }
    }
    
    private void setupSelectionMode() {
        // 初始化选择状态
        selectedItems.clear();
        for (int i = 0; i < acquaintanceItems.size(); i++) {
            selectedItems.add(false);
        }
        
        // 显示选择圆圈
        for (LinearLayout item : acquaintanceItems) {
            showSelectionCircle(item, false);
        }
        
        // 设置点击事件
        for (int i = 0; i < acquaintanceItems.size(); i++) {
            final int index = i;
            acquaintanceItems.get(i).setOnClickListener(v -> {
                toggleItemSelection(index);
            });
        }
        
        // 进入选择模式时立即显示删除按钮
        showDeleteButton();
    }
    
    private void exitSelectionMode() {
        // 隐藏选择圆圈
        for (LinearLayout item : acquaintanceItems) {
            hideSelectionCircle(item);
        }
        
        // 恢复原来的点击事件
        for (LinearLayout item : acquaintanceItems) {
            item.setOnClickListener(null);
        }
        
        // 隐藏删除按钮
        hideDeleteButton();
    }
    
    private void showSelectionCircle(LinearLayout item, boolean isSelected) {
        // 在头像左侧显示选择圆圈
        View selectionCircle = item.findViewById(R.id.selection_circle);
        if (selectionCircle == null) {
            // 尝试查找第二个选择圆圈
            selectionCircle = item.findViewById(R.id.selection_circle_2);
        }
        
        if (selectionCircle != null) {
            selectionCircle.setVisibility(View.VISIBLE);
            if (isSelected) {
                selectionCircle.setBackgroundResource(R.drawable.selection_circle_selected);
            } else {
                selectionCircle.setBackgroundResource(R.drawable.selection_circle_unselected);
            }
        }
    }
    
    private void hideSelectionCircle(LinearLayout item) {
        View selectionCircle = item.findViewById(R.id.selection_circle);
        if (selectionCircle == null) {
            // 尝试查找第二个选择圆圈
            selectionCircle = item.findViewById(R.id.selection_circle_2);
        }
        
        if (selectionCircle != null) {
            selectionCircle.setVisibility(View.GONE);
        }
    }
    
    private void toggleItemSelection(int index) {
        if (index >= 0 && index < selectedItems.size()) {
            selectedItems.set(index, !selectedItems.get(index));
            showSelectionCircle(acquaintanceItems.get(index), selectedItems.get(index));
        }
    }
    
    private void showDeleteButton() {
        // 显示删除按钮
        View deleteButton = findViewById(R.id.delete_button);
        if (deleteButton != null) {
            deleteButton.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideDeleteButton() {
        // 隐藏删除按钮
        View deleteButton = findViewById(R.id.delete_button);
        if (deleteButton != null) {
            deleteButton.setVisibility(View.GONE);
        }
    }
    
    private void setupDeleteButton() {
        LinearLayout deleteButton = findViewById(R.id.delete_button);
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                deleteSelectedItems();
            });
        }
    }
    
    private void deleteSelectedItems() {
        // 检查是否有选中的知友
        boolean hasSelection = selectedItems.contains(true);
        
        if (!hasSelection) {
            Toast.makeText(this, "请先选择要删除的知友", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示确认对话框
        new AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除选中的知友吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                // 执行删除操作
                performDelete();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void performDelete() {
        // 收集选中的用户ID
        List<Long> selectedUserIds = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.get(i) && i < acquaintanceUsers.size()) {
                selectedUserIds.add(acquaintanceUsers.get(i).getId());
            }
        }

        if (selectedUserIds.isEmpty()) {
            Toast.makeText(this, "未选择任何知友", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "准备批量删除知友，数量: " + selectedUserIds.size());

        // 获取认证token
        String token = com.example.myapplication.auth.AuthManager.getInstance(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "未登录，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用批量删除API
        apiService.removeFriendsBatch("Bearer " + token, selectedUserIds)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Log.d(TAG, "批量删除知友成功: " + response.body().getMessage());
                            Toast.makeText(AcquaintancesActivity.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            // 重新加载列表
                            loadAcquaintances();

                            // 退出选择模式
                            isSelectionMode = false;
                            ivSettings.setImageResource(R.drawable.ic_settings);
                            exitSelectionMode();
                        } else {
                            Log.e(TAG, "批量删除知友失败: " +
                                    (response.body() != null ? response.body().getMessage() : "Unknown error"));
                            Toast.makeText(AcquaintancesActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        Log.e(TAG, "批量删除知友网络异常", t);
                        Toast.makeText(AcquaintancesActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void loadAcquaintances() {
        Log.d(TAG, "开始加载知友列表");

        apiService.getAcquaintances(0, 20).enqueue(new Callback<ApiResponse<List<UserDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserDTO>>> call, Response<ApiResponse<List<UserDTO>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    acquaintanceUsers = response.body().getData();
                    Log.d(TAG, "成功加载 " + acquaintanceUsers.size() + " 个知友");

                    // 重新设置点击事件
                    runOnUiThread(() -> setupAcquaintanceItemClickListeners());
                } else {
                    Log.e(TAG, "加载知友列表失败: " + (response.body() != null ? response.body().getMessage() : "Unknown error"));
                    Toast.makeText(AcquaintancesActivity.this, "加载知友列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserDTO>>> call, Throwable t) {
                Log.e(TAG, "加载知友列表异常", t);
                Toast.makeText(AcquaintancesActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAcquaintanceItemClickListeners() {
        // 为每个知友项目设置点击事件
        for (int i = 1; i <= 2; i++) {
            LinearLayout item = findViewById(getAcquaintanceItemId(i));
            if (item != null) {
                final int index = i - 1;  // 转换为0-based index
                item.setOnClickListener(v -> {
                    // 跳转到用户详情页
                    Intent intent = new Intent(AcquaintancesActivity.this, UserDetailActivity.class);

                    // ✅ 使用API返回的真实user_id和属性（不再硬编码）
                    if (index < acquaintanceUsers.size()) {
                        UserDTO user = acquaintanceUsers.get(index);
                        intent.putExtra("user_id", user.getId() != null ? user.getId() : 0L);
                        intent.putExtra("user_name", user.getNickname() != null ? user.getNickname() : user.getUsername());
                        intent.putExtra("user_status", user.getStatus() != null ? user.getStatus() : "OFFLINE");
                        intent.putExtra("user_age", user.getAge() != null ? String.valueOf(user.getAge()) : "");
                        intent.putExtra("user_location", user.getLocation() != null ? user.getLocation() : "未知");
                        intent.putExtra("user_description", user.getSignature() != null ? user.getSignature() : "这是一个可爱的知友");
                        intent.putExtra("user_avatar", R.drawable.group_27); // TODO: 后续使用user.getAvatarUrl()
                    } else {
                        // 如果API数据不足，使用默认值
                        intent.putExtra("user_id", 0L);
                        intent.putExtra("user_name", "知友" + (index + 1));
                        intent.putExtra("user_status", "在线");
                        intent.putExtra("user_age", "25");
                        intent.putExtra("user_location", "北京");
                        intent.putExtra("user_description", "这是一个可爱的知友");
                        intent.putExtra("user_avatar", R.drawable.group_27);
                    }

                    startActivity(intent);
                });
            }
        }
    }
}
