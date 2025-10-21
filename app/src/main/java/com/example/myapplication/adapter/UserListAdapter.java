package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.UserDetailActivity;
import com.example.myapplication.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户列表适配器 - 实现RecyclerView复用机制
 *
 * 关键特性：
 * - 即使有10000个用户数据，也只创建约12-15个ViewHolder
 * - 滚动时自动复用ViewHolder，只更新数据
 * - 高效内存使用，性能优秀
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private static final String TAG = "UserListAdapter";
    private final Context context;
    private List<UserDTO> userList = new ArrayList<>();

    public UserListAdapter(Context context) {
        this.context = context;
    }

    /**
     * 更新用户列表数据
     */
    public void updateData(List<UserDTO> newList) {
        this.userList.clear();
        if (newList != null) {
            this.userList.addAll(newList);
        }
        notifyDataSetChanged();
        Log.d(TAG, "数据已更新，共 " + userList.size() + " 个用户");
    }

    /**
     * 创建ViewHolder - 只会调用屏幕可见数量+2次
     * 例如：屏幕显示4个卡片，这个方法只会调用约6次
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_card, parent, false);
        Log.d(TAG, "创建新ViewHolder（屏幕可见+缓存）");
        return new UserViewHolder(view);
    }

    /**
     * 绑定数据到ViewHolder - 滚动时会频繁调用，复用已创建的ViewHolder
     * 例如：有100个用户，只有6个ViewHolder，滚动时不断复用这6个
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDTO user = userList.get(position);
        holder.bind(user);
        Log.d(TAG, "绑定数据 position=" + position + ", 用户: " + user.getNickname());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder - 持有单个用户卡片的所有View引用
     */
    class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView statusView;
        private final TextView priceView;
        private final TextView locationView;
        private final View statusIndicator;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.user_name);
            statusView = itemView.findViewById(R.id.user_status);
            priceView = itemView.findViewById(R.id.user_price);
            locationView = itemView.findViewById(R.id.user_location);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        /**
         * 将用户数据绑定到UI
         */
        public void bind(UserDTO user) {
            // 更新用户名
            String displayName = user.getNickname() != null ? user.getNickname() : user.getUsername();
            nameView.setText(displayName);

            // ✅ 更新在线状态 - 优先使用status字段，兜底使用isOnline
            String statusText;
            boolean isOnline;
            if (user.getStatus() != null) {
                // 使用新的status字段
                statusText = "ONLINE".equals(user.getStatus()) ? "在线" : "离线";
                isOnline = "ONLINE".equals(user.getStatus());
            } else if (user.getIsOnline() != null) {
                // 兜底：使用旧的isOnline字段
                statusText = user.getIsOnline() ? "在线" : "离线";
                isOnline = user.getIsOnline();
            } else {
                statusText = "离线";
                isOnline = false;
            }
            statusView.setText(statusText);
            statusIndicator.setBackgroundResource(
                isOnline ? R.drawable.status_indicator_green : R.drawable.status_indicator_red
            );

            // 更新价格
            Double videoPrice = user.getVideoCallPrice();
            if (videoPrice != null) {
                int priceInt = videoPrice.intValue();
                priceView.setText(priceInt + "/分钟");
            } else {
                priceView.setText("0/分钟");
            }

            // 更新位置
            String location = user.getLocation() != null ? user.getLocation() : "未知";
            locationView.setText(location);

            // 设置点击事件
            itemView.setOnClickListener(v -> {
                Log.d(TAG, "点击用户: " + user.getNickname() + " (ID: " + user.getId() + ")");

                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.putExtra("user_id", user.getId());
                intent.putExtra("user_name", displayName);

                // ✅ 优先使用status字段，兜底使用isOnline字段
                String status = user.getStatus() != null ? user.getStatus() :
                                (user.getIsOnline() != null && user.getIsOnline() ? "ONLINE" : "OFFLINE");
                intent.putExtra("user_status", status);

                if (user.getLocation() != null) {
                    intent.putExtra("user_location", user.getLocation());
                }
                if (user.getAge() != null) {
                    intent.putExtra("user_age", String.valueOf(user.getAge()));
                }
                if (user.getSignature() != null) {
                    intent.putExtra("user_description", user.getSignature());
                }
                intent.putExtra("user_avatar", R.drawable.rectangle_411_1);

                context.startActivity(intent);
            });
        }
    }
}
