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
    private TextView tvVideoPrice; // 视频通话价格显示
    private TextView tvVoicePrice; // 语音通话价格显示
    
    // 通话相关
    private CallService callService;
    private AuthManager authManager;
    private String userToken;
    private Long currentUserId; // 当前查看的用户ID
    private Long receiverUserId; // 接收方用户ID(仅用于通话)
    private CallPrices currentCallPrices; // 当前通话价格信息
    private boolean isLiked = false; // 是否已喜欢
    private boolean isSubscribed = false; // 是否已订阅状态通知
    private boolean isBlacklisted = false; // 是否在黑名单
    private String userRemark = ""; // 用户备注

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

        // 获取传递的用户ID
        currentUserId = getIntent().getLongExtra("user_id", -1L);

        Log.d("UserDetailActivity", "═══════════════════════════════════════");
        Log.d("UserDetailActivity", "onCreate - 接收到的user_id: " + currentUserId);
        Log.d("UserDetailActivity", "Intent中的所有extras:");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d("UserDetailActivity", "  " + key + " = " + extras.get(key));
            }
        }
        Log.d("UserDetailActivity", "═══════════════════════════════════════");

        // 初始化通话相关服务
        initCallServices(currentUserId);

        initViews();
        setupViewPager();
        setupClickListeners();

        // 从后端加载用户详细信息
        if (currentUserId != null && currentUserId != -1L) {
            // 有用户ID，从后端API加载
            loadUserDetail(currentUserId);
        } else {
            // ✅ 修复：没有用户ID时，使用Intent传递的数据显示（兜底方案）
            Log.w("UserDetailActivity", "未传递用户ID，使用Intent中的数据显示");
            loadUserFromIntent();
        }

        // 加载用户通话价格信息
        loadUserCallPrices();

        // 加载喜欢状态
        loadLikeStatus();

        // 加载订阅状态
        loadSubscribeStatus();

        // 加载黑名单状态
        loadBlacklistStatus();

        // 加载备注
        loadUserRemark();
    }

    /**
     * ✅ 新增：从Intent加载用户信息（兜底方案）
     */
    private void loadUserFromIntent() {
        Log.d("UserDetailActivity", "从Intent加载用户信息");

        // 从Intent获取传递的数据
        String userName = getIntent().getStringExtra("user_name");
        String userStatus = getIntent().getStringExtra("user_status");
        String userAge = getIntent().getStringExtra("user_age");
        String userLocation = getIntent().getStringExtra("user_location");
        String userDescription = getIntent().getStringExtra("user_description");

        // 显示用户信息
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("未知用户");
        }

        if (userStatus != null && !userStatus.isEmpty()) {
            tvUserStatus.setText(userStatus);
        } else {
            tvUserStatus.setText("离线");
        }

        if (userAge != null && !userAge.isEmpty()) {
            tvUserAge.setText(userAge);
            tvUserAge.setVisibility(View.VISIBLE);
        } else {
            tvUserAge.setVisibility(View.GONE);
        }

        if (userLocation != null && !userLocation.isEmpty()) {
            tvUserLocation.setText(userLocation);
        } else {
            tvUserLocation.setText("未知");
        }

        if (userDescription != null && !userDescription.isEmpty()) {
            tvUserDescription.setText(userDescription);
        } else {
            tvUserDescription.setText("这个人很懒，什么都没写");
        }

        // 显示默认ID
        tvUserId.setText("ID: " + (receiverUserId != null ? receiverUserId : "未知"));

        Log.d("UserDetailActivity", "Intent数据加载完成 - " +
                "姓名: " + userName + ", 状态: " + userStatus + ", 位置: " + userLocation);
    }

    /**
     * 从后端加载用户详细信息
     */
    private void loadUserDetail(Long userId) {
        Log.d("UserDetailActivity", "═══════════════════════════════════════");
        Log.d("UserDetailActivity", "开始加载用户详情,userId: " + userId);
        Log.d("UserDetailActivity", "当前currentUserId: " + currentUserId);
        Log.d("UserDetailActivity", "当前receiverUserId: " + receiverUserId);
        Log.d("UserDetailActivity", "═══════════════════════════════════════");

        // 在后台线程调用API
        new AsyncTask<Void, Void, com.example.myapplication.dto.UserDTO>() {
            @Override
            protected com.example.myapplication.dto.UserDTO doInBackground(Void... voids) {
                try {
                    Log.d("UserDetailActivity", "API调用开始 - 请求用户ID: " + userId);
                    
                    // 获取Token
                    String token = authManager.getAuthHeader();
                    Log.d("UserDetailActivity", "Token状态: " + (token != null ? "已获取" : "未获取"));

                    // 调用API获取用户详情
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<com.example.myapplication.dto.UserDTO>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService().getUserById(userId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<com.example.myapplication.dto.UserDTO>> response =
                        call.execute();

                    Log.d("UserDetailActivity", "API响应状态: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.myapplication.dto.ApiResponse<com.example.myapplication.dto.UserDTO> apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            com.example.myapplication.dto.UserDTO userData = apiResponse.getData();
                            Log.d("UserDetailActivity", "API返回成功 - 用户ID: " + userData.getId() + 
                                 ", 用户名: " + userData.getUsername() + 
                                 ", 昵称: " + userData.getNickname());
                            return userData;
                        } else {
                            Log.e("UserDetailActivity", "API返回失败: " + apiResponse.getMessage());
                            return null;
                        }
                    } else {
                        Log.e("UserDetailActivity", "请求失败,code: " + response.code());
                        return null;
                    }
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加载用户详情异常", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(com.example.myapplication.dto.UserDTO user) {
                Log.d("UserDetailActivity", "═══════════════════════════════════════");
                Log.d("UserDetailActivity", "onPostExecute - 接收到的用户数据:");
                if (user != null) {
                    Log.d("UserDetailActivity", "  用户ID: " + user.getId());
                    Log.d("UserDetailActivity", "  用户名: " + user.getUsername());
                    Log.d("UserDetailActivity", "  昵称: " + user.getNickname());
                    Log.d("UserDetailActivity", "  在线状态: " + user.getIsOnline());
                    Log.d("UserDetailActivity", "  位置: " + user.getLocation());
                    Log.d("UserDetailActivity", "  签名: " + user.getSignature());
                    Log.d("UserDetailActivity", "═══════════════════════════════════════");

                    // 更新UI显示用户信息
                    String displayName = user.getNickname() != null ? user.getNickname() : user.getUsername();
                    tvUserName.setText(displayName);
                    Log.d("UserDetailActivity", "设置显示名称: " + displayName);

                    String status = (user.getIsOnline() != null && user.getIsOnline()) ? "在线" : "离线";
                    tvUserStatus.setText(status);
                    Log.d("UserDetailActivity", "设置在线状态: " + status);

                    // 年龄处理:如果有birthday,可以计算年龄;目前暂时隐藏
                    tvUserAge.setVisibility(View.GONE);

                    String location = user.getLocation() != null ? user.getLocation() : "未知";
                    tvUserLocation.setText(location);
                    Log.d("UserDetailActivity", "设置位置: " + location);

                    String description = user.getSignature() != null ? user.getSignature() : "这个人很懒,什么都没写";
                    tvUserDescription.setText(description);
                    Log.d("UserDetailActivity", "设置描述: " + description);

                    // 显示用户ID
                    tvUserId.setText("ID: " + user.getId());
                    Log.d("UserDetailActivity", "设置用户ID显示: ID: " + user.getId());
                } else {
                    // ✅ 修复：API加载失败时，使用Intent传递的数据（兜底方案）
                    Log.e("UserDetailActivity", "加载用户详情失败 - 用户数据为null，使用Intent数据");
                    Toast.makeText(UserDetailActivity.this, "无法从服务器加载用户信息，显示本地数据", Toast.LENGTH_SHORT).show();
                    loadUserFromIntent();
                }
            }
        }.execute();
    }

    private void initCallServices(Long userId) {
        callService = CallService.Companion.getInstance();
        authManager = AuthManager.getInstance(this);
        userToken = authManager.getToken();

        // ✅ 修复：使用从Intent传递的真实用户ID，不再使用默认测试ID
        if (userId != null && userId != -1L) {
            receiverUserId = userId;
            Log.d("UserDetailActivity", "接收到有效用户ID: " + receiverUserId);
        } else {
            receiverUserId = null;
            Log.w("UserDetailActivity", "⚠️ 未传递用户ID，通话功能将不可用");
        }

        Log.d("UserDetailActivity", "初始化通话服务，Token: " + (userToken != null ? "已获取" : "未获取") + ", receiverUserId: " + receiverUserId);
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

        // 初始化价格TextView - 通过遍历按钮布局找到价格TextView
        tvVideoPrice = findPriceTextView(llVideoButton);
        tvVoicePrice = findPriceTextView(llVoiceButton);
    }

    /**
     * 递归查找布局中的价格TextView(显示"X/分钟"的TextView)
     */
    private TextView findPriceTextView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                String text = tv.getText().toString();
                if (text.contains("/分钟")) {
                    return tv;
                }
            } else if (child instanceof ViewGroup) {
                TextView result = findPriceTextView((ViewGroup) child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
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
            // ✅ 实现私信功能 - 跳转到ChatActivity
            if (currentUserId == null || currentUserId == -1L) {
                Toast.makeText(this, "用户信息错误，无法发起私信", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ 检查是否是自己
            Long loginUserId = authManager != null ? AuthManager.getInstance(this).getUserId() : null;
            if (loginUserId != null && loginUserId.equals(currentUserId)) {
                Toast.makeText(this, "不能给自己发送私信", Toast.LENGTH_SHORT).show();
                Log.w("UserDetailActivity", "尝试给自己发送私信，已拦截 - userId: " + currentUserId);
                return;
            }

            android.content.Intent intent = new android.content.Intent(this, ChatActivity.class);
            intent.putExtra("user_id", currentUserId);
            intent.putExtra("user_name", tvUserName.getText().toString());
            intent.putExtra("user_avatar", ""); // TODO: 从用户信息获取头像URL
            startActivity(intent);
            Log.d("UserDetailActivity", "跳转到聊天界面 - userId: " + currentUserId + ", name: " + tvUserName.getText());
        });
        
        llLikeButton.setOnClickListener(v -> {
            toggleLike();
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

        // 更新菜单项文本
        TextView tvLikeMenu = menuView.findViewById(R.id.ll_like_menu).findViewById(android.R.id.text1);
        if (tvLikeMenu == null) {
            tvLikeMenu = (TextView) ((android.view.ViewGroup) menuView.findViewById(R.id.ll_like_menu)).getChildAt(0);
        }
        if (tvLikeMenu != null) {
            tvLikeMenu.setText(isLiked ? "取消喜欢" : "喜欢");
        }

        TextView tvSubscribeMenu = menuView.findViewById(R.id.ll_subscribe_menu).findViewById(android.R.id.text1);
        if (tvSubscribeMenu == null) {
            tvSubscribeMenu = (TextView) ((android.view.ViewGroup) menuView.findViewById(R.id.ll_subscribe_menu)).getChildAt(0);
        }
        if (tvSubscribeMenu != null) {
            tvSubscribeMenu.setText(isSubscribed ? "取消订阅状态通知" : "订阅状态通知");
        }

        // 设置菜单项的点击事件
        menuView.findViewById(R.id.ll_like_menu).setOnClickListener(v -> {
            toggleLikeFromMenu();
            bottomSheetDialog.dismiss();
        });

        menuView.findViewById(R.id.ll_subscribe_menu).setOnClickListener(v -> {
            toggleSubscribe();
            bottomSheetDialog.dismiss();
        });

        menuView.findViewById(R.id.ll_query_status_menu).setOnClickListener(v -> {
            queryAccountStatus();
            bottomSheetDialog.dismiss();
        });

        menuView.findViewById(R.id.ll_set_remark_menu).setOnClickListener(v -> {
            showSetRemarkDialog();
            bottomSheetDialog.dismiss();
        });

        menuView.findViewById(R.id.ll_report_menu).setOnClickListener(v -> {
            showReportDialog();
            bottomSheetDialog.dismiss();
        });

        menuView.findViewById(R.id.ll_blacklist_menu).setOnClickListener(v -> {
            showBlacklistDialog();
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
     * 加载用户通话价格信息（从后端API获取）
     */
    private void loadUserCallPrices() {
        if (userToken == null) {
            Log.w("UserDetailActivity", "用户未登录，无法获取通话价格");
            return;
        }

        Log.d("UserDetailActivity", "开始获取用户通话价格");

        // 在后台线程调用同步API
        new AsyncTask<Void, Void, CallPricesResult>() {
            @Override
            protected CallPricesResult doInBackground(Void... voids) {
                try {
                    // 调用 CallService 的同步方法
                    return callService.getUserCallPricesSync(userToken);
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "获取通话价格异常", e);
                    return new CallPricesResult.Error("网络异常: " + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(CallPricesResult result) {
                if (result instanceof CallPricesResult.Success) {
                    CallPricesResult.Success successResult = (CallPricesResult.Success) result;
                    currentCallPrices = successResult.getPrices();
                    Log.d("UserDetailActivity", "通话价格获取成功: " + currentCallPrices.toString());

                    // 更新UI显示价格
                    if (tvVideoPrice != null) {
                        String videoPriceText = currentCallPrices.getVideoCallPrice() > 0
                            ? String.format("%.0f元/分钟", currentCallPrices.getVideoCallPrice())
                            : "免费";
                        tvVideoPrice.setText(videoPriceText);
                    }

                    if (tvVoicePrice != null) {
                        String voicePriceText = currentCallPrices.getVoiceCallPrice() > 0
                            ? String.format("%.0f元/分钟", currentCallPrices.getVoiceCallPrice())
                            : "免费";
                        tvVoicePrice.setText(voicePriceText);
                    }

                } else if (result instanceof CallPricesResult.Error) {
                    CallPricesResult.Error errorResult = (CallPricesResult.Error) result;
                    Log.e("UserDetailActivity", "获取通话价格失败: " + errorResult.getMessage());

                    // 即使失败也设置默认价格，让按钮可以使用
                    currentCallPrices = new CallPrices(300.0, 150.0, 1.0, true, true, true);

                    // 显示默认价格
                    if (tvVideoPrice != null) {
                        tvVideoPrice.setText("300元/分钟");
                    }
                    if (tvVoicePrice != null) {
                        tvVoicePrice.setText("150元/分钟");
                    }
                }
            }
        }.execute();
    }
    
    
    /**
     * 发起视频通话
     */
    private void initiateVideoCall() {
        if (receiverUserId == null) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示通话发起对话框
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("视频通话");
        builder.setMessage("正在发起视频通话，请稍候...");
        builder.setCancelable(false);
        android.app.AlertDialog progressDialog = builder.create();
        progressDialog.show();

        // 实际调用后端 API 发起通话
        new AsyncTask<Void, Void, CallInitiateResult>() {
            @Override
            protected CallInitiateResult doInBackground(Void... voids) {
                try {
                    // 调用 CallService 发起视频通话
                    return callService.initiateCall(
                        userToken,
                        receiverUserId,
                        "VIDEO"  // 通话类型：VIDEO 或 VOICE
                    );
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "发起视频通话异常", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(CallInitiateResult result) {
                progressDialog.dismiss();

                if (result instanceof CallInitiateResult.Success) {
                    CallInitiateResult.Success successResult = (CallInitiateResult.Success) result;
                    CallSession callSession = successResult.getCallSession();

                    Log.d("UserDetailActivity", "视频通话发起成功，会话ID: " + callSession.getCallSessionId());

                    // 跳转到等待接听界面（OutgoingCallActivity）
                    android.content.Intent intent = new android.content.Intent(UserDetailActivity.this, OutgoingCallActivity.class);
                    intent.putExtra("sessionId", callSession.getCallSessionId());
                    intent.putExtra("receiverId", String.valueOf(receiverUserId));
                    intent.putExtra("receiverName", tvUserName.getText().toString());
                    intent.putExtra("receiverAvatar", ""); // TODO: 从用户信息获取头像URL
                    intent.putExtra("callType", "VIDEO");
                    startActivity(intent);

                } else if (result instanceof CallInitiateResult.Error) {
                    CallInitiateResult.Error errorResult = (CallInitiateResult.Error) result;
                    String errorMsg = errorResult.getMessage();

                    Log.e("UserDetailActivity", "发起视频通话失败: " + errorMsg);
                    Toast.makeText(UserDetailActivity.this,
                        "发起通话失败: " + errorMsg,
                        Toast.LENGTH_LONG).show();
                } else {
                    Log.e("UserDetailActivity", "发起视频通话失败: 未知错误");
                    Toast.makeText(UserDetailActivity.this,
                        "发起通话失败，请稍后重试",
                        Toast.LENGTH_LONG).show();
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

        // 显示通话发起对话框
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("语音通话");
        builder.setMessage("正在发起语音通话，请稍候...");
        builder.setCancelable(false);
        android.app.AlertDialog progressDialog = builder.create();
        progressDialog.show();

        // 实际调用后端 API 发起通话
        new AsyncTask<Void, Void, CallInitiateResult>() {
            @Override
            protected CallInitiateResult doInBackground(Void... voids) {
                try {
                    // 调用 CallService 发起语音通话
                    return callService.initiateCall(
                        userToken,
                        receiverUserId,
                        "VOICE"  // 通话类型：VIDEO 或 VOICE
                    );
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "发起语音通话异常", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(CallInitiateResult result) {
                progressDialog.dismiss();

                if (result instanceof CallInitiateResult.Success) {
                    CallInitiateResult.Success successResult = (CallInitiateResult.Success) result;
                    CallSession callSession = successResult.getCallSession();

                    Log.d("UserDetailActivity", "语音通话发起成功，会话ID: " + callSession.getCallSessionId());

                    // 跳转到等待接听界面（OutgoingCallActivity）
                    android.content.Intent intent = new android.content.Intent(UserDetailActivity.this, OutgoingCallActivity.class);
                    intent.putExtra("sessionId", callSession.getCallSessionId());
                    intent.putExtra("receiverId", String.valueOf(receiverUserId));
                    intent.putExtra("receiverName", tvUserName.getText().toString());
                    intent.putExtra("receiverAvatar", ""); // TODO: 从用户信息获取头像URL
                    intent.putExtra("callType", "VOICE");
                    startActivity(intent);

                } else if (result instanceof CallInitiateResult.Error) {
                    CallInitiateResult.Error errorResult = (CallInitiateResult.Error) result;
                    String errorMsg = errorResult.getMessage();

                    Log.e("UserDetailActivity", "发起语音通话失败: " + errorMsg);
                    Toast.makeText(UserDetailActivity.this,
                        "发起通话失败: " + errorMsg,
                        Toast.LENGTH_LONG).show();
                } else {
                    Log.e("UserDetailActivity", "发起语音通话失败: 未知错误");
                    Toast.makeText(UserDetailActivity.this,
                        "发起通话失败，请稍后重试",
                        Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    /**
     * 加载喜欢状态
     */
    private void loadLikeStatus() {
        if (userToken == null || currentUserId == null || currentUserId == -1L) {
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<Boolean>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .isLiked(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<Boolean>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return response.body().getData();
                    }
                    return false;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加载喜欢状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean liked) {
                isLiked = liked;
                updateLikeButtonUI();
            }
        }.execute();
    }

    /**
     * 切换喜欢状态
     */
    private void toggleLike() {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查是否是自己
        Long loginUserId = authManager.getUserId();
        if (loginUserId != null && loginUserId.equals(currentUserId)) {
            Toast.makeText(this, "不能喜欢自己", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call;

                    if (isLiked) {
                        // 取消喜欢
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .removeLike(authManager.getAuthHeader(), currentUserId);
                    } else {
                        // 添加喜欢
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .addLike(authManager.getAuthHeader(), currentUserId);
                    }

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "切换喜欢状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    isLiked = !isLiked;
                    updateLikeButtonUI();
                    Toast.makeText(UserDetailActivity.this, isLiked ? "已喜欢" : "已取消喜欢", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 更新喜欢按钮UI
     * 如果已喜欢，隐藏按钮；未喜欢则显示按钮
     */
    private void updateLikeButtonUI() {
        if (isLiked) {
            // 已喜欢 - 隐藏按钮
            llLikeButton.setVisibility(View.GONE);
            Log.d("UserDetailActivity", "喜欢按钮已隐藏 - 已喜欢该用户");
        } else {
            // 未喜欢 - 显示按钮
            llLikeButton.setVisibility(View.VISIBLE);
            Log.d("UserDetailActivity", "喜欢按钮已显示 - 未喜欢该用户");
        }
    }

    /**
     * 从菜单切换喜欢状态
     */
    private void toggleLikeFromMenu() {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call;

                    if (isLiked) {
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .removeLike(authManager.getAuthHeader(), currentUserId);
                    } else {
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .addLike(authManager.getAuthHeader(), currentUserId);
                    }

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();
                    return response.isSuccessful() && response.body() != null && response.body().isSuccess();
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "切换喜欢状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    isLiked = !isLiked;
                    updateLikeButtonUI();
                    Toast.makeText(UserDetailActivity.this, isLiked ? "已加入喜欢列表" : "已从喜欢列表移出", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 加载订阅状态
     */
    private void loadSubscribeStatus() {
        if (userToken == null || currentUserId == null || currentUserId == -1L) {
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<Boolean>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .isSubscribed(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<Boolean>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return response.body().getData();
                    }
                    return false;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加载订阅状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean subscribed) {
                isSubscribed = subscribed;
                Log.d("UserDetailActivity", "订阅状态: " + isSubscribed);
            }
        }.execute();
    }

    /**
     * 切换订阅状态
     */
    private void toggleSubscribe() {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call;

                    if (isSubscribed) {
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .unsubscribeUser(authManager.getAuthHeader(), currentUserId);
                    } else {
                        call = com.example.myapplication.network.NetworkConfig.getApiService()
                                .subscribeUser(authManager.getAuthHeader(), currentUserId);
                    }

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();
                    return response.isSuccessful() && response.body() != null && response.body().isSuccess();
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "切换订阅状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    isSubscribed = !isSubscribed;
                    Toast.makeText(UserDetailActivity.this, isSubscribed ? "订阅成功，将收到TA的状态通知" : "已取消订阅状态通知", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 加载黑名单状态
     */
    private void loadBlacklistStatus() {
        if (userToken == null || currentUserId == null || currentUserId == -1L) {
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<Boolean>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .isBlacklisted(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<Boolean>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return response.body().getData();
                    }
                    return false;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加载黑名单状态失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean blacklisted) {
                isBlacklisted = blacklisted;
                Log.d("UserDetailActivity", "黑名单状态: " + isBlacklisted);
            }
        }.execute();
    }

    /**
     * 加载用户备注
     */
    private void loadUserRemark() {
        if (userToken == null || currentUserId == null || currentUserId == -1L) {
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .getUserRemark(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return response.body().getData();
                    }
                    return "";
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加载备注失败", e);
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String remark) {
                userRemark = remark != null ? remark : "";
                Log.d("UserDetailActivity", "用户备注: " + userRemark);
            }
        }.execute();
    }

    /**
     * 查询账号状态
     */
    private void queryAccountStatus() {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, java.util.Map<String, Object>>() {
            @Override
            protected java.util.Map<String, Object> doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<java.util.Map<String, Object>>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .getAccountStatus(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<java.util.Map<String, Object>>> response = call.execute();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        return response.body().getData();
                    }
                    return null;
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "查询账号状态失败", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(java.util.Map<String, Object> statusInfo) {
                if (statusInfo != null) {
                    String message = (String) statusInfo.get("message");
                    String status = (String) statusInfo.get("status");
                    Boolean isOnline = (Boolean) statusInfo.get("isOnline");
                    Boolean isVip = (Boolean) statusInfo.get("isVip");

                    StringBuilder sb = new StringBuilder();
                    sb.append("账号状态查询结果:\n\n");
                    sb.append("状态: ").append(status).append("\n");
                    sb.append("在线: ").append(isOnline ? "是" : "否").append("\n");
                    sb.append("VIP: ").append(isVip ? "是" : "否").append("\n");
                    sb.append("\n").append(message);

                    new android.app.AlertDialog.Builder(UserDetailActivity.this)
                            .setTitle("账号状态")
                            .setMessage(sb.toString())
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "查询失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 显示设置备注对话框
     */
    private void showSetRemarkDialog() {
        android.widget.EditText editText = new android.widget.EditText(this);
        editText.setText(userRemark);
        editText.setHint("请输入备注");

        new android.app.AlertDialog.Builder(this)
                .setTitle("设置备注")
                .setView(editText)
                .setPositiveButton("确定", (dialog, which) -> {
                    String remark = editText.getText().toString().trim();
                    setUserRemark(remark);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 设置用户备注
     */
    private void setUserRemark(String remark) {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .setUserRemark(authManager.getAuthHeader(), currentUserId, remark);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();
                    return response.isSuccessful() && response.body() != null && response.body().isSuccess();
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "设置备注失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    userRemark = remark;
                    Toast.makeText(UserDetailActivity.this, "备注设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserDetailActivity.this, "设置失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 显示举报对话框
     */
    private void showReportDialog() {
        View reportView = getLayoutInflater().inflate(R.layout.dialog_report, null);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(reportView)
                .create();

        // 设置举报选项点击事件
        reportView.findViewById(R.id.ll_report_spam).setOnClickListener(v -> {
            submitReport("垃圾广告");
            dialog.dismiss();
        });

        reportView.findViewById(R.id.ll_report_harassment).setOnClickListener(v -> {
            submitReport("骚扰信息");
            dialog.dismiss();
        });

        reportView.findViewById(R.id.ll_report_fraud).setOnClickListener(v -> {
            submitReport("欺诈行为");
            dialog.dismiss();
        });

        reportView.findViewById(R.id.ll_report_fake).setOnClickListener(v -> {
            submitReport("虚假信息");
            dialog.dismiss();
        });

        reportView.findViewById(R.id.ll_report_other).setOnClickListener(v -> {
            submitReport("其他");
            dialog.dismiss();
        });

        reportView.findViewById(R.id.btn_cancel_report).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * 提交举报
     */
    private void submitReport(String reason) {
        Toast.makeText(this, "举报成功，我们将尽快处理。举报原因: " + reason, Toast.LENGTH_SHORT).show();
        Log.d("UserDetailActivity", "举报用户 " + currentUserId + ", 原因: " + reason);
    }

    /**
     * 显示黑名单对话框
     */
    private void showBlacklistDialog() {
        View blacklistView = getLayoutInflater().inflate(R.layout.dialog_blacklist, null);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(blacklistView)
                .create();

        TextView tvMessage = blacklistView.findViewById(R.id.tv_blacklist_message);
        tvMessage.setText("加入黑名单后，你将不会收到TA的任何消息，TA也无法查看你的动态。");

        blacklistView.findViewById(R.id.btn_confirm_blacklist).setOnClickListener(v -> {
            addToBlacklist();
            dialog.dismiss();
        });

        blacklistView.findViewById(R.id.btn_cancel_blacklist).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * 加入黑名单
     */
    private void addToBlacklist() {
        if (userToken == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUserId == -1L) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    retrofit2.Call<com.example.myapplication.dto.ApiResponse<String>> call =
                        com.example.myapplication.network.NetworkConfig.getApiService()
                            .addToBlacklist(authManager.getAuthHeader(), currentUserId);

                    retrofit2.Response<com.example.myapplication.dto.ApiResponse<String>> response = call.execute();
                    return response.isSuccessful() && response.body() != null && response.body().isSuccess();
                } catch (Exception e) {
                    Log.e("UserDetailActivity", "加入黑名单失败", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    isBlacklisted = true;
                    Toast.makeText(UserDetailActivity.this, "已加入黑名单", Toast.LENGTH_SHORT).show();
                    // 返回上一页
                    finish();
                } else {
                    Toast.makeText(UserDetailActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

}
