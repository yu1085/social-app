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
import java.util.List;
import java.util.ArrayList;

public class AcquaintancesActivity extends AppCompatActivity {
    
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquaintances);
        
        initViews();
        setupClickListeners();
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
        // 这里可以添加实际的删除逻辑
        Toast.makeText(this, "删除功能待实现", Toast.LENGTH_SHORT).show();
        
        // 退出选择模式
        isSelectionMode = false;
        ivSettings.setImageResource(R.drawable.ic_settings);
        exitSelectionMode();
    }
    
    private void setupAcquaintanceItemClickListeners() {
        // 为每个知友项目设置点击事件
        for (int i = 1; i <= 2; i++) {
            LinearLayout item = findViewById(getAcquaintanceItemId(i));
            if (item != null) {
                final int index = i;
                item.setOnClickListener(v -> {
                    // 跳转到用户详情页
                    Intent intent = new Intent(AcquaintancesActivity.this, UserDetailActivity.class);

                    // ✅ 修复：添加 user_id 传递（使用测试用户ID）
                    // 使用现有测试用户：23820512, 23820513, 23820516, 23820517
                    long userId = 23820512L + (index % 4);  // 循环使用4个测试用户
                    intent.putExtra("user_id", userId);

                    intent.putExtra("user_name", "知友" + index);
                    intent.putExtra("user_status", "在线");
                    intent.putExtra("user_age", "25");
                    intent.putExtra("user_location", "北京");
                    intent.putExtra("user_description", "这是一个可爱的知友");
                    intent.putExtra("user_avatar", R.drawable.group_27);
                    startActivity(intent);
                });
            }
        }
    }
}
