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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import java.util.ArrayList;

public class LikesActivity extends AppCompatActivity {
    
    private ImageView ivBack;
    private ImageView ivSettings;
    private TextView tvTitle;
    private LinearLayout llViewWhoLikesMe;
    private LinearLayout llCancelLikeButton;
    
    // 选择模式相关
    private boolean isSelectionMode = false;
    private List<LinearLayout> likeItems = new ArrayList<>();
    private List<Boolean> selectedItems = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        
        initViews();
        initLikeItems();
        setupClickListeners();
    }
    
    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivSettings = findViewById(R.id.iv_settings);
        tvTitle = findViewById(R.id.tv_title);
        llViewWhoLikesMe = findViewById(R.id.ll_view_who_likes_me);
        llCancelLikeButton = findViewById(R.id.ll_cancel_like_button);
    }
    
    private void initLikeItems() {
        // 初始化喜欢项目列表
        likeItems.clear();
        selectedItems.clear();
        
        for (int i = 1; i <= 10; i++) {
            LinearLayout item = findViewById(getLikeItemId(i));
            if (item != null) {
                likeItems.add(item);
                selectedItems.add(false);
            }
        }
    }
    
    private int getLikeItemId(int index) {
        // 根据索引返回对应的资源ID
        switch (index) {
            case 1: return R.id.like_item_1;
            case 2: return R.id.like_item_2;
            case 3: return R.id.like_item_3;
            case 4: return R.id.like_item_4;
            case 5: return R.id.like_item_5;
            case 6: return R.id.like_item_6;
            case 7: return R.id.like_item_7;
            case 8: return R.id.like_item_8;
            case 9: return R.id.like_item_9;
            case 10: return R.id.like_item_10;
            default: return 0;
        }
    }
    
    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());
        
        ivSettings.setOnClickListener(v -> {
            if (isSelectionMode) {
                exitSelectionMode();
            } else {
                // 直接进入选择模式，不显示设置弹窗
                enterSelectionMode();
            }
        });
        
        // 长按设置图标显示设置弹窗
        ivSettings.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                showSettingsBottomSheet();
                return true;
            }
            return false;
        });
        
        llViewWhoLikesMe.setOnClickListener(v -> {
            Toast.makeText(this, "查看谁喜欢我", Toast.LENGTH_SHORT).show();
        });
        
        llCancelLikeButton.setOnClickListener(v -> {
            cancelSelectedLikes();
        });
        
        // 为喜欢项目设置点击事件，跳转到用户详情页
        setupLikeItemClickListeners();
    }
    
    private void showSettingsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_likes_settings, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        
        // 设置点击事件
        TextView tvBatchManagement = bottomSheetView.findViewById(R.id.tv_batch_management);
        TextView tvClearList = bottomSheetView.findViewById(R.id.tv_clear_list);
        TextView tvCancel = bottomSheetView.findViewById(R.id.tv_cancel);
        
        tvBatchManagement.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            enterSelectionMode();
        });
        
        tvClearList.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showClearListConfirmation();
        });
        
        tvCancel.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        
        bottomSheetDialog.show();
    }
    
    private void enterSelectionMode() {
        isSelectionMode = true;
        
        // 设置图标变为勾选图标
        ivSettings.setImageResource(R.drawable.ic_check);
        
        // 显示所有选择圆圈
        for (int i = 0; i < likeItems.size(); i++) {
            showSelectionCircle(likeItems.get(i), false);
        }
        
        // 设置点击事件
        for (int i = 0; i < likeItems.size(); i++) {
            final int index = i;
            likeItems.get(i).setOnClickListener(v -> {
                toggleItemSelection(index);
            });
        }
        
        // 显示底部取消喜欢按钮
        llCancelLikeButton.setVisibility(View.VISIBLE);
    }
    
    private void exitSelectionMode() {
        isSelectionMode = false;
        
        // 设置图标恢复为设置图标
        ivSettings.setImageResource(R.drawable.ic_settings);
        
        // 隐藏所有选择圆圈
        for (LinearLayout item : likeItems) {
            hideSelectionCircle(item);
        }
        
        // 移除点击事件
        for (LinearLayout item : likeItems) {
            item.setOnClickListener(null);
        }
        
        // 隐藏底部取消喜欢按钮
        llCancelLikeButton.setVisibility(View.GONE);
        
        // 重置选择状态
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedItems.set(i, false);
        }
    }
    
    private void showSelectionCircle(LinearLayout item, boolean isSelected) {
        // 在头像左侧显示选择圆圈
        View selectionCircle = item.findViewById(getSelectionCircleId(item));
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
        View selectionCircle = item.findViewById(getSelectionCircleId(item));
        if (selectionCircle != null) {
            selectionCircle.setVisibility(View.GONE);
        }
    }
    
    private int getSelectionCircleId(LinearLayout item) {
        // 根据item的ID返回对应的选择圆圈ID
        int itemId = item.getId();
        if (itemId == R.id.like_item_1) return R.id.selection_circle_1;
        if (itemId == R.id.like_item_2) return R.id.selection_circle_2;
        if (itemId == R.id.like_item_3) return R.id.selection_circle_3;
        if (itemId == R.id.like_item_4) return R.id.selection_circle_4;
        if (itemId == R.id.like_item_5) return R.id.selection_circle_5;
        if (itemId == R.id.like_item_6) return R.id.selection_circle_6;
        if (itemId == R.id.like_item_7) return R.id.selection_circle_7;
        if (itemId == R.id.like_item_8) return R.id.selection_circle_8;
        if (itemId == R.id.like_item_9) return R.id.selection_circle_9;
        if (itemId == R.id.like_item_10) return R.id.selection_circle_10;
        return 0;
    }
    
    private void toggleItemSelection(int index) {
        if (index >= 0 && index < selectedItems.size()) {
            selectedItems.set(index, !selectedItems.get(index));
            showSelectionCircle(likeItems.get(index), selectedItems.get(index));
        }
    }
    
    private void cancelSelectedLikes() {
        // 检查是否有选中的项目
        boolean hasSelection = selectedItems.contains(true);
        
        if (!hasSelection) {
            Toast.makeText(this, "请先选择要取消喜欢的用户", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示确认对话框
        new AlertDialog.Builder(this)
            .setTitle("确认取消喜欢")
            .setMessage("确定要取消喜欢选中的用户吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                // 执行取消喜欢操作
                performCancelLike();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void performCancelLike() {
        // 这里可以添加实际的取消喜欢逻辑
        Toast.makeText(this, "取消喜欢功能待实现", Toast.LENGTH_SHORT).show();
        
        // 退出选择模式
        exitSelectionMode();
    }
    
    private void showClearListConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("确认清空")
            .setMessage("确定要清空喜欢列表吗？此操作不可恢复。")
            .setPositiveButton("清空", (dialog, which) -> {
                // 执行清空操作
                Toast.makeText(this, "列表已清空", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void setupLikeItemClickListeners() {
        // 为每个喜欢项目设置点击事件
        for (int i = 1; i <= 10; i++) {
            LinearLayout item = findViewById(getLikeItemId(i));
            if (item != null) {
                final int index = i;
                item.setOnClickListener(v -> {
                    // 跳转到用户详情页
                    Intent intent = new Intent(LikesActivity.this, UserDetailActivity.class);
                    intent.putExtra("user_name", "喜欢用户" + index);
                    intent.putExtra("user_status", "在线");
                    intent.putExtra("user_age", "25");
                    intent.putExtra("user_location", "北京");
                    intent.putExtra("user_description", "这是一个可爱的用户");
                    intent.putExtra("user_avatar", R.drawable.group_28);
                    startActivity(intent);
                });
            }
        }
    }
}
