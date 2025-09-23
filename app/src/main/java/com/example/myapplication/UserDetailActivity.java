package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.os.AsyncTask;
import com.example.myapplication.service.CallService;
import com.example.myapplication.service.CallPrices;
import com.example.myapplication.service.CallPricesResult;
import com.example.myapplication.service.CallInitiateResult;
import com.example.myapplication.service.CallSession;
import com.example.myapplication.auth.AuthManager;

public class UserDetailActivity extends AppCompatActivity {
    
    private ViewPager2 viewPagerImages;
    private LinearLayout indicatorContainer;
    private View indicator1, indicator2, indicator3;
    private TextView tvUserName;
    private TextView tvUserStatus;
    private TextView tvUserAge;
    private TextView tvUserLocation;
    private TextView tvUserDescription;
    private LinearLayout llVideoButton;
    private LinearLayout llVoiceButton;
    private LinearLayout llMessageButton;
    private LinearLayout llLikeButton;
    private LinearLayout llCopyIdButton;
    private TextView tvUserId;
    private LinearLayout llMenuButton; // Added for menu button
    private LinearLayout llGiftsSection; // Added for gifts section
    private LinearLayout llGiftHeart, llGiftRose, llGiftCake; // Added for individual gift items
    private LinearLayout llGuardianSection; // Added for guardian section
    
    // 通话相关
    private CallService callService;
    private AuthManager authManager;
    private String userToken;
    private Long receiverUserId; // 接收方用户ID
    private CallPrices currentCallPrices; // 当前通话价格信息
    
    // 用户图片资源数组
    private int[] userImages = {
        R.drawable.rectangle_411_1,
        R.drawable.rectangle_412_1,
        R.drawable.rectangle_411_1  // 可以添加更多图片
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        
        // 获取传递的用户数据
        String userName = getIntent().getStringExtra("user_name");
        String userStatus = getIntent().getStringExtra("user_status");
        String userAge = getIntent().getStringExtra("user_age");
        String userLocation = getIntent().getStringExtra("user_location");
        String userDescription = getIntent().getStringExtra("user_description");
        int userAvatarResId = getIntent().getIntExtra("user_avatar", R.drawable.rectangle_411_1);
        
        // 初始化通话相关服务
        initCallServices();
        
        initViews();
        setupViewPager();
        populateUserData(userName, userStatus, userAge, userLocation, userDescription, userAvatarResId);
        setupClickListeners();
        
        // 加载用户通话价格信息
        loadUserCallPrices();
    }
    
    private void initCallServices() {
        callService = CallService.Companion.getInstance();
        authManager = AuthManager.getInstance(this);
        userToken = authManager.getToken();
        
        // 模拟接收方用户ID（实际应该从Intent传递）
        receiverUserId = 2L; // 这里应该从Intent获取真实的用户ID
        
        Log.d("UserDetailActivity", "初始化通话服务，Token: " + (userToken != null ? "已获取" : "未获取"));
    }
    
    private void initViews() {
        viewPagerImages = findViewById(R.id.view_pager_images);
        indicatorContainer = findViewById(R.id.indicator_container);
        indicator1 = findViewById(R.id.indicator_1);
        indicator2 = findViewById(R.id.indicator_2);
        indicator3 = findViewById(R.id.indicator_3);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserStatus = findViewById(R.id.tv_user_status);
        tvUserAge = findViewById(R.id.tv_user_age);
        tvUserLocation = findViewById(R.id.tv_user_location);
        tvUserDescription = findViewById(R.id.tv_user_description);
        llVideoButton = findViewById(R.id.ll_video_button);
        llVoiceButton = findViewById(R.id.ll_voice_button);
        llMessageButton = findViewById(R.id.ll_message_button);
        llLikeButton = findViewById(R.id.ll_like_button);
        llCopyIdButton = findViewById(R.id.ll_copy_id_button);
        tvUserId = findViewById(R.id.tv_user_id);
        llMenuButton = findViewById(R.id.ll_menu_button); // Initialize menu button
        llGiftsSection = findViewById(R.id.ll_gifts_section); // Initialize gifts section
        llGiftHeart = findViewById(R.id.ll_gift_heart); // Initialize heart gift
        llGiftRose = findViewById(R.id.ll_gift_rose); // Initialize rose gift
        llGiftCake = findViewById(R.id.ll_gift_cake); // Initialize cake gift
        llGuardianSection = findViewById(R.id.ll_guardian_section); // Initialize guardian section
    }
    

    
    private void setupViewPager() {
        // 创建图片适配器
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPagerImages.setAdapter(adapter);
        
        // 设置页面变化监听器
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }
        });
    }
    
    private void updateIndicators(int position) {
        // 重置所有指示器
        indicator1.setBackgroundResource(R.drawable.indicator_inactive);
        indicator2.setBackgroundResource(R.drawable.indicator_inactive);
        indicator3.setBackgroundResource(R.drawable.indicator_inactive);
        
        // 设置当前页面的指示器为活跃状态
        switch (position) {
            case 0:
                indicator1.setBackgroundResource(R.drawable.indicator_active);
                break;
            case 1:
                indicator2.setBackgroundResource(R.drawable.indicator_active);
                break;
            case 2:
                indicator3.setBackgroundResource(R.drawable.indicator_active);
                break;
        }
    }
    
    private void populateUserData(String userName, String userStatus, String userAge, 
                                 String userLocation, String userDescription, int userAvatarResId) {
        if (userName != null) tvUserName.setText(userName);
        if (userStatus != null) tvUserStatus.setText(userStatus);
        if (userAge != null) tvUserAge.setText(userAge);
        if (userLocation != null) tvUserLocation.setText(userLocation);
        if (userDescription != null) tvUserDescription.setText(userDescription);
    }
    
    private void setupClickListeners() {
        llVideoButton.setOnClickListener(v -> {
            if (userToken == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (currentCallPrices == null) {
                Toast.makeText(this, "正在加载价格信息，请稍候...", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!currentCallPrices.getVideoCallEnabled()) {
                Toast.makeText(this, "对方未开启视频通话功能", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 发起视频通话
            initiateVideoCall();
        });
        
        llVoiceButton.setOnClickListener(v -> {
            if (userToken == null) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (currentCallPrices == null) {
                Toast.makeText(this, "正在加载价格信息，请稍候...", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!currentCallPrices.getVoiceCallEnabled()) {
                Toast.makeText(this, "对方未开启语音通话功能", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 发起语音通话
            initiateVoiceCall();
        });
        
        llMessageButton.setOnClickListener(v -> {
            Toast.makeText(this, "私信她", Toast.LENGTH_SHORT).show();
            // TODO: 实现私信功能
        });
        
        llLikeButton.setOnClickListener(v -> {
            Toast.makeText(this, "喜欢", Toast.LENGTH_SHORT).show();
            // TODO: 实现喜欢功能
        });
        
        llCopyIdButton.setOnClickListener(v -> {
            copyUserIdToClipboard();
        });
        
        llMenuButton.setOnClickListener(v -> {
            showMenuDialog();
        });
        
        // 礼物区域和礼物项目的点击监听器
        llGiftsSection.setOnClickListener(v -> {
            startGiftsActivity();
        });
        
        llGiftHeart.setOnClickListener(v -> {
            startGiftsActivity();
        });
        
        llGiftRose.setOnClickListener(v -> {
            startGiftsActivity();
        });
        
        llGiftCake.setOnClickListener(v -> {
            startGiftsActivity();
        });
        
        llGuardianSection.setOnClickListener(v -> {
            startGuardianActivity();
        });
    }
    
    // 图片适配器
    private class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {
        
        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ImageViewHolder(imageView);
        }
        
        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.imageView.setImageResource(userImages[position]);
        }
        
        @Override
        public int getItemCount() {
            return userImages.length;
        }
        
        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            
            ImageViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView;
            }
        }
    }
    
    private void copyUserIdToClipboard() {
        String userId = tvUserId.getText().toString();
        // 提取ID号码（去掉"ID: "前缀）
        String idNumber = userId.replace("ID: ", "");
        
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("用户ID", idNumber);
        clipboard.setPrimaryClip(clip);
        
        Toast.makeText(this, "ID已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }
 
    private void showMenuDialog() {
        // 创建底部弹出的菜单布局
        View menuView = getLayoutInflater().inflate(R.layout.bottom_menu_dialog, null);
        
        // 创建BottomSheetDialog
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = 
            new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        bottomSheetDialog.setContentView(menuView);
        
        // 设置菜单项的点击事件
        menuView.findViewById(R.id.ll_like_menu).setOnClickListener(v -> {
            Toast.makeText(this, "已喜欢", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_subscribe_menu).setOnClickListener(v -> {
            Toast.makeText(this, "订阅状态通知", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_query_status_menu).setOnClickListener(v -> {
            Toast.makeText(this, "查询账号状态", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_set_remark_menu).setOnClickListener(v -> {
            Toast.makeText(this, "设置备注", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_report_menu).setOnClickListener(v -> {
            Toast.makeText(this, "举报", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_blacklist_menu).setOnClickListener(v -> {
            Toast.makeText(this, "加入黑名单", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        
        menuView.findViewById(R.id.ll_cancel_menu).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        
        // 显示底部菜单
        bottomSheetDialog.show();
    }
 
    private void showGiftsDialog() {
        // 创建礼物弹窗布局
        View giftsView = getLayoutInflater().inflate(R.layout.gifts_dialog, null);
        
        // 创建AlertDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(giftsView);
        
        // 设置弹窗样式
        android.app.AlertDialog giftsDialog = builder.create();
        giftsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // 设置关闭按钮的点击事件
        giftsView.findViewById(R.id.iv_close_gifts).setOnClickListener(v -> {
            giftsDialog.dismiss();
        });
        
        // 显示弹窗
        giftsDialog.show();
    }
 
    private void startGiftsActivity() {
        // 跳转到礼物展示界面
        android.content.Intent intent = new android.content.Intent(this, GiftsActivity.class);
        startActivity(intent);
    }
 
    private void startGuardianActivity() {
        // 跳转到守护者界面
        android.content.Intent intent = new android.content.Intent(this, GuardianActivity.class);
        startActivity(intent);
    }
    
    /**
     * 加载用户通话价格信息
     */
    private void loadUserCallPrices() {
        if (userToken == null) {
            Log.w("UserDetailActivity", "用户未登录，无法获取通话价格");
            return;
        }
        
        // 模拟获取通话价格数据
        new AsyncTask<Void, Void, CallPrices>() {
            @Override
            protected CallPrices doInBackground(Void... voids) {
                try {
                    // 模拟网络延迟
                    Thread.sleep(1000);
                    
                    // 返回模拟的通话价格数据
                    return new CallPrices(
                        300.0,  // videoCallPrice
                        150.0,  // voiceCallPrice
                        1.0,    // messagePrice
                        true,   // videoCallEnabled
                        true,   // voiceCallEnabled
                        true    // messageChargeEnabled
                    );
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "获取通话价格异常", e);
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(CallPrices result) {
                if (result != null) {
                    currentCallPrices = result;
                    Log.d("UserDetailActivity", "通话价格获取成功: " + result.toString());
                    updateVideoButtonPrice();
                } else {
                    Log.e("UserDetailActivity", "获取通话价格失败");
                    Toast.makeText(UserDetailActivity.this, "获取通话价格失败", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
    
    /**
     * 更新视频和语音按钮价格显示
     */
    private void updateVideoButtonPrice() {
        if (currentCallPrices != null) {
            runOnUiThread(() -> {
                // 更新视频按钮价格显示
                updateButtonPrice(llVideoButton, "视频", currentCallPrices.getVideoCallPrice());
                
                // 更新语音按钮价格显示
                updateButtonPrice(llVoiceButton, "语音", currentCallPrices.getVoiceCallPrice());
                
                // 显示价格信息
                Toast.makeText(this, 
                    "视频通话: " + currentCallPrices.getVideoCallPrice() + "元/分钟\n" +
                    "语音通话: " + currentCallPrices.getVoiceCallPrice() + "元/分钟", 
                    Toast.LENGTH_LONG).show();
            });
        }
    }
    
    /**
     * 更新按钮价格显示
     */
    private void updateButtonPrice(LinearLayout button, String type, double price) {
        if (button != null) {
            // 查找按钮内的价格TextView并更新
            // 这里需要根据实际布局结构来查找价格TextView
            // 暂时通过Toast显示，实际项目中需要找到具体的TextView并更新
            Log.d("UserDetailActivity", type + "通话价格: " + price + "元/分钟");
        }
    }
    
    /**
     * 发起视频通话
     */
    private void initiateVideoCall() {
        if (receiverUserId == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 模拟发起视频通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                Toast.makeText(UserDetailActivity.this, "正在发起视频通话...", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // 模拟网络延迟
                    Thread.sleep(2000);
                    
                    // 模拟通话发起成功
                    return true;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "发起视频通话异常", e);
                    return false;
                }
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.d("UserDetailActivity", "视频通话发起成功");
                    Toast.makeText(UserDetailActivity.this, "视频通话已发起，等待对方接听...", Toast.LENGTH_LONG).show();
                    
                    // TODO: 这里可以跳转到通话界面或显示通话状态
                    // 例如：跳转到VideoCallActivity
                } else {
                    Log.e("UserDetailActivity", "发起视频通话失败");
                    Toast.makeText(UserDetailActivity.this, "发起通话失败，请稍后重试", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
    
    /**
     * 发起语音通话
     */
    private void initiateVoiceCall() {
        if (receiverUserId == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 模拟发起语音通话
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                Toast.makeText(UserDetailActivity.this, "正在发起语音通话...", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    // 模拟网络延迟
                    Thread.sleep(2000);
                    
                    // 模拟通话发起成功
                    return true;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "发起语音通话异常", e);
                    return false;
                }
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.d("UserDetailActivity", "语音通话发起成功");
                    Toast.makeText(UserDetailActivity.this, "语音通话已发起，等待对方接听...", Toast.LENGTH_LONG).show();
                    
                    // TODO: 这里可以跳转到通话界面或显示通话状态
                    // 例如：跳转到VoiceCallActivity
                } else {
                    Log.e("UserDetailActivity", "发起语音通话失败");
                    Toast.makeText(UserDetailActivity.this, "发起通话失败，请稍后重试", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

  
}
