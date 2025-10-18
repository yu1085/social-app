package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.content.Intent;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.LifecycleCoroutineScope;
import android.widget.Toast;
import android.util.Log;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import com.example.myapplication.network.NetworkConfig;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.dto.ApiResponse;
import com.example.myapplication.dto.UserDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_FILTER = 1001;
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1002;

    private LinearLayout tabActive, tabHot, tabNearby, tabNew, tabExclusive;
    private LinearLayout navHome, navSquare, navMessage, navProfile;
    private LinearLayout filterButton;
    private LinearLayout videoMatchButton;
    private LinearLayout voiceMatchButton;
    private ComposeView composeSquare, composeMessage, composeProfile;
    private TextView textActive, textHot, textNearby, textNew, textExclusive;

    // 用户卡片视图的引用
    private View userCard1, userCard2, userCard3, userCard4;
    private List<UserDTO> currentUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 检查是否首次启动，显示隐私政策
        checkFirstLaunchAndShowPrivacyPolicy();
    }
    
    private void checkFirstLaunchAndShowPrivacyPolicy() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("is_first_launch", true);
        
        if (isFirstLaunch) {
            // 首次启动，显示隐私政策弹窗
            showPrivacyPolicyDialog();
        } else {
            // 非首次启动，正常初始化
            initializeApp();
        }
    }
    
    private void showPrivacyPolicyDialog() {
        new AlertDialog.Builder(this)
            .setTitle("隐私政策")
            .setMessage("欢迎使用SocialMeet！\n\n为了向您提供更好的服务，我们需要收集和使用您的个人信息。请您仔细阅读并同意我们的隐私政策。\n\n我们承诺：\n• 严格按照相关法律法规收集使用您的个人信息\n• 不会向第三方出售、出租您的个人信息\n• 您有权随时撤回对个人信息处理的同意\n\n点击\"同意\"表示您已阅读并同意我们的隐私政策。")
            .setPositiveButton("同意", (dialog, which) -> {
                // 用户同意，保存状态并初始化应用
                SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("is_first_launch", false).apply();
                initializeApp();
            })
            .setNegativeButton("拒绝", (dialog, which) -> {
                // 用户拒绝，退出应用
                Toast.makeText(this, "您需要同意隐私政策才能使用本应用", Toast.LENGTH_LONG).show();
                finish();
            })
            .setCancelable(false) // 不允许点击外部关闭
            .show();
    }
    
    private void initializeApp() {
        setContentView(R.layout.activity_main);

        // 请求通知权限（Android 13+）
        requestNotificationPermission();

        // 启用GPU渲染优化
        try {
            com.example.myapplication.util.GPURenderingOptimizer.INSTANCE.optimizeActivityRendering(this);
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "GPU渲染优化失败", e);
        }

        // 初始化标签视图
        initTabViews();

        // 设置标签点击监听器
        setTabClickListeners();

        // 底部导航点击
        initBottomNav();

        // 筛选按钮点击
        initFilterButton();

        // 匹配按钮点击
        initMatchButtons();

        // 添加真人认证测试按钮
        setupTestButtons();

        // 初始化ProfileComposeHost
        if (composeProfile != null) {
            com.example.myapplication.compose.ProfileComposeHost.attach(composeProfile);
        }

        // 初始状态：所有标签保持相同样式与尺寸
        resetAllTabs();

        // 设置默认选中首页
        updateBottomNavSelection("home");

        // 加载用户列表
        loadUsers(null, null, null, null, null);
    }

    /**
     * 请求通知权限（Android 13+）
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "请求通知权限");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATION_PERMISSION);
            } else {
                Log.d(TAG, "通知权限已授予");
            }
        } else {
            Log.d(TAG, "Android版本 < 13，无需请求通知权限");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "✅ 通知权限已授予");
                Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ 通知权限被拒绝");
                Toast.makeText(this, "通知权限被拒绝，您可能无法接收来电通知", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 加载用户列表
     */
    private void loadUsers(String keyword, String gender, String location, Integer minAge, Integer maxAge) {
        Log.d(TAG, "开始加载用户列表...");

        ApiService apiService = NetworkConfig.getApiService();
        Call<ApiResponse<List<UserDTO>>> call = apiService.searchUsers(
                keyword, gender, location, minAge, maxAge, 0, 6
        );

        call.enqueue(new Callback<ApiResponse<List<UserDTO>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<UserDTO>>> call, Response<ApiResponse<List<UserDTO>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<UserDTO>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        currentUserList = apiResponse.getData();
                        Log.d(TAG, "成功加载 " + currentUserList.size() + " 个用户");

                        // 打印所有用户信息
                        for (int i = 0; i < currentUserList.size(); i++) {
                            UserDTO u = currentUserList.get(i);
                            Log.d(TAG, "  用户" + i + " - ID: " + u.getId() +
                                       ", 昵称: " + u.getNickname() +
                                       ", 用户名: " + u.getUsername());
                        }

                        updateUserCards();
                    } else {
                        Log.e(TAG, "API返回失败: " + apiResponse.getMessage());
                        Toast.makeText(MainActivity.this, "加载用户失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "请求失败: " + response.code());
                    Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<UserDTO>>> call, Throwable t) {
                Log.e(TAG, "加载用户列表失败", t);
                Toast.makeText(MainActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更新用户卡片UI
     */
    private void updateUserCards() {
        if (currentUserList == null || currentUserList.isEmpty()) {
            Log.w(TAG, "用户列表为空");
            return;
        }

        Log.d(TAG, "═══════════════════════════════════════");
        Log.d(TAG, "开始更新用户卡片UI");
        Log.d(TAG, "用户列表大小: " + currentUserList.size());
        for (int i = 0; i < currentUserList.size(); i++) {
            UserDTO user = currentUserList.get(i);
            Log.d(TAG, "  卡片" + i + " - ID: " + user.getId() + 
                       ", 用户名: " + user.getUsername() + 
                       ", 昵称: " + user.getNickname());
        }
        Log.d(TAG, "═══════════════════════════════════════");

        // 获取用户卡片视图（只有4个）
        userCard1 = findViewById(R.id.user_card_1);
        userCard2 = findViewById(R.id.user_card_2);
        userCard3 = findViewById(R.id.user_card_3);
        userCard4 = findViewById(R.id.user_card_4);

        View[] userCards = {userCard1, userCard2, userCard3, userCard4};

        for (int i = 0; i < userCards.length; i++) {
            if (i < currentUserList.size()) {
                UserDTO user = currentUserList.get(i);
                Log.d(TAG, "更新卡片" + i + " - 用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
                updateUserCard(userCards[i], user, i);
            } else {
                if (userCards[i] != null) {
                    userCards[i].setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 更新单个用户卡片
     */
    private void updateUserCard(View cardView, UserDTO user, int index) {
        if (cardView == null || user == null) return;

        cardView.setVisibility(View.VISIBLE);

        // 根据索引查找卡片内的视图
        String suffix = "_" + (index + 1);
        TextView nameView = findTextViewInCard(cardView, "user_name" + suffix);
        TextView statusView = findTextViewInCard(cardView, "user_status" + suffix);
        TextView priceView = findTextViewInCard(cardView, "user_price" + suffix);
        TextView locationView = findTextViewInCard(cardView, "user_location" + suffix);
        View statusIndicator = findViewInCard(cardView, "status_indicator" + suffix);

        // 更新用户名
        if (nameView != null) {
            String displayName = user.getNickname() != null ? user.getNickname() : user.getUsername();
            nameView.setText(displayName);
        }

        // 更新状态
        if (statusView != null && statusIndicator != null) {
            if (user.getIsOnline() != null && user.getIsOnline()) {
                statusView.setText("在线");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_green);
            } else {
                statusView.setText("离线");
                statusIndicator.setBackgroundResource(R.drawable.status_indicator_red);
            }
        }

        // 更新价格（暂时使用默认价格，如果DTO中有价格字段可以使用）
        if (priceView != null) {
            int[] prices = {300, 350, 500, 400, 600, 350};
            priceView.setText(prices[index] + "/分钟");
        }

        // 更新位置
        if (locationView != null) {
            String location = user.getLocation() != null ? user.getLocation() : "未知";
            locationView.setText(location);
        }

        // 设置点击事件
        final UserDTO finalUser = user;
        final int cardIndex = index;
        cardView.setOnClickListener(v -> {
            // 添加详细日志
            Log.d(TAG, "═══════════════════════════════════════");
            Log.d(TAG, "点击用户卡片 - 卡片索引: " + cardIndex);
            Log.d(TAG, "点击用户卡片 - ID: " + finalUser.getId() +
                       ", 昵称: " + (finalUser.getNickname() != null ? finalUser.getNickname() : finalUser.getUsername()) +
                       ", 用户名: " + finalUser.getUsername());
            Log.d(TAG, "═══════════════════════════════════════");

            Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
            intent.putExtra("user_id", finalUser.getId());
            intent.putExtra("user_name", finalUser.getNickname() != null ? finalUser.getNickname() : finalUser.getUsername());
            intent.putExtra("user_status", finalUser.getIsOnline() ? "在线" : "离线");
            // 暂时不传年龄,因为UserDTO中没有getAge()方法
            if (finalUser.getLocation() != null) {
                intent.putExtra("user_location", finalUser.getLocation());
            }
            if (finalUser.getSignature() != null) {
                intent.putExtra("user_description", finalUser.getSignature());
            }
            intent.putExtra("user_avatar", R.drawable.rectangle_411_1);
            startActivity(intent);
        });
    }

    /**
     * 在卡片中查找TextView
     */
    private TextView findTextViewInCard(View cardView, String textViewName) {
        try {
            // 使用反射查找资源ID
            int resId = getResources().getIdentifier(textViewName, "id", getPackageName());
            if (resId != 0) {
                View view = cardView.findViewById(resId);
                if (view instanceof TextView) {
                    return (TextView) view;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "查找TextView失败: " + textViewName, e);
        }
        return null;
    }

    /**
     * 在卡片中查找View
     */
    private View findViewInCard(View cardView, String viewName) {
        try {
            int resId = getResources().getIdentifier(viewName, "id", getPackageName());
            if (resId != 0) {
                return cardView.findViewById(resId);
            }
        } catch (Exception e) {
            Log.w(TAG, "查找View失败: " + viewName, e);
        }
        return null;
    }


    private void initTabViews() {
        tabActive = findViewById(R.id.tab_active);
        tabHot = findViewById(R.id.tab_hot);
        tabNearby = findViewById(R.id.tab_nearby);
        tabNew = findViewById(R.id.tab_new);
        tabExclusive = findViewById(R.id.tab_exclusive);

        // 底部导航 & Compose 容器
        navHome = findViewById(R.id.nav_home);
        navSquare = findViewById(R.id.nav_square);
        navMessage = findViewById(R.id.nav_message);
        navProfile = findViewById(R.id.nav_profile);
        composeSquare = findViewById(R.id.compose_square);
        composeMessage = findViewById(R.id.compose_message);
        composeProfile = findViewById(R.id.compose_profile);
        filterButton = findViewById(R.id.filter_button);
        videoMatchButton = findViewById(R.id.video_match_button);
        voiceMatchButton = findViewById(R.id.voice_match_button);
        
        // 获取每个标签容器中的TextView
        textActive = (TextView) tabActive.getChildAt(0);
        textHot = (TextView) tabHot.getChildAt(0);
        textNearby = (TextView) tabNearby.getChildAt(0);
        textNew = (TextView) tabNew.getChildAt(0);
        textExclusive = (TextView) tabExclusive.getChildAt(0);
    }
    
    private void setTabClickListeners() {
        tabActive.setOnClickListener(v -> selectTab(tabActive, textActive));
        tabHot.setOnClickListener(v -> selectTab(tabHot, textHot));
        tabNearby.setOnClickListener(v -> selectTab(tabNearby, textNearby));
        tabNew.setOnClickListener(v -> selectTab(tabNew, textNew));
        tabExclusive.setOnClickListener(v -> selectTab(tabExclusive, textExclusive));
    }

    private void initBottomNav() {
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                showHome();
                updateBottomNavSelection("home");
            });
        }
        if (navSquare != null) {
            navSquare.setOnClickListener(v -> {
                showSquare();
                updateBottomNavSelection("square");
            });
        }
        if (navMessage != null) {
            navMessage.setOnClickListener(v -> {
                showMessage();
                updateBottomNavSelection("message");
            });
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                showProfile();
                updateBottomNavSelection("profile");
            });
        }
    }
    
    private void initFilterButton() {
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showFilterDialog());
        }
    }
    
    
    private void showFilterDialog() {
        // 跳转到筛选Activity
        Intent intent = new Intent(this, FilterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK && data != null) {
            // 获取筛选条件
            String gender = data.getStringExtra("gender");
            String location = data.getStringExtra("location");
            Integer minAge = data.getIntExtra("minAge", -1);
            Integer maxAge = data.getIntExtra("maxAge", -1);

            // 处理-1值（未设置）
            if (minAge == -1) minAge = null;
            if (maxAge == -1) maxAge = null;

            Log.d(TAG, "筛选条件 - gender: " + gender + ", location: " + location + ", minAge: " + minAge + ", maxAge: " + maxAge);

            // 重新加载用户列表
            loadUsers(null, gender, location, minAge, maxAge);
        }
    }

    private void initMatchButtons() {
        // 视频速配按钮
        if (videoMatchButton != null) {
            videoMatchButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, VideoMatchActivity.class);
                // 传递视频速配相关参数 - 与UI中的价格区间匹配
                intent.putExtra("match_type", "VIDEO");
                intent.putExtra("min_price", 100.0);  // 活跃女生最低价格
                intent.putExtra("max_price", 500.0);  // 高颜女生最高价格
                intent.putExtra("default_price", 275.0); // 人气女生中点价格
                intent.putExtra("online_count", 13264); // 在线人数
                startActivity(intent);
            });
        }
        
        // 语音速配按钮
        if (voiceMatchButton != null) {
            voiceMatchButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, VoiceMatchActivity.class);
                // 传递语音速配相关参数 - 与UI中的价格区间匹配
                intent.putExtra("match_type", "VOICE");
                intent.putExtra("min_price", 50.0);   // 活跃女生最低价格
                intent.putExtra("max_price", 200.0);  // 高颜女生最高价格
                intent.putExtra("default_price", 125.0); // 人气女生中点价格
                intent.putExtra("online_count", 1153);  // 在线人数
                startActivity(intent);
            });
        }
    }

    private void hideAllContent() {
        if (composeSquare != null) composeSquare.setVisibility(View.GONE);
        if (composeMessage != null) composeMessage.setVisibility(View.GONE);
        if (composeProfile != null) composeProfile.setVisibility(View.GONE);
        View scroll = findViewById(R.id.user_cards_scroll);
        if (scroll != null) scroll.setVisibility(View.GONE);

        // 隐藏首页顶部两块
        View header1 = findViewById(R.id.function_entry_area);
        if (header1 != null) header1.setVisibility(View.GONE);
        View header2 = findViewById(R.id.category_tabs_area);
        if (header2 != null) header2.setVisibility(View.GONE);
    }

    private void showHome() {
        // 仅显示首页：顶部两块 + 滚动内容
        hideAllContent();
        View header1 = findViewById(R.id.function_entry_area);
        if (header1 != null) header1.setVisibility(View.VISIBLE);
        View header2 = findViewById(R.id.category_tabs_area);
        if (header2 != null) header2.setVisibility(View.VISIBLE);
        View scroll = findViewById(R.id.user_cards_scroll);
        if (scroll != null) scroll.setVisibility(View.VISIBLE);
    }

    private void showSquare() {
        hideAllContent();
        if (composeSquare != null) {
            composeSquare.setVisibility(View.VISIBLE);
            com.example.myapplication.compose.SquareComposeHost.attach(composeSquare);
        }
    }

    private void showMessage() {
        hideAllContent();
        if (composeMessage != null) {
            composeMessage.setVisibility(View.VISIBLE);
            com.example.myapplication.compose.MessageComposeHost.attach(composeMessage);
        }
    }

    private void showProfile() {
        hideAllContent();
        if (composeProfile != null) {
            composeProfile.setVisibility(View.VISIBLE);
            // ProfileComposeHost.attach() 已经在onCreate中调用过了，这里不需要重复调用
        }
    }
    
    /**
     * 更新底部导航选中状态
     */
    private void updateBottomNavSelection(String selectedTab) {
        // 重置所有按钮状态
        resetBottomNavSelection();
        
        // 设置选中按钮状态
        switch (selectedTab) {
            case "home":
                if (navHome != null) {
                    setBottomNavSelected(navHome, true);
                }
                break;
            case "square":
                if (navSquare != null) {
                    setBottomNavSelected(navSquare, true);
                }
                break;
            case "message":
                if (navMessage != null) {
                    setBottomNavSelected(navMessage, true);
                }
                break;
            case "profile":
                if (navProfile != null) {
                    setBottomNavSelected(navProfile, true);
                }
                break;
        }
    }
    
    /**
     * 重置所有底部导航按钮状态
     */
    private void resetBottomNavSelection() {
        if (navHome != null) setBottomNavSelected(navHome, false);
        if (navSquare != null) setBottomNavSelected(navSquare, false);
        if (navMessage != null) setBottomNavSelected(navMessage, false);
        if (navProfile != null) setBottomNavSelected(navProfile, false);
    }
    
    /**
     * 设置底部导航按钮选中状态
     */
    private void setBottomNavSelected(LinearLayout navItem, boolean isSelected) {
        if (navItem == null) return;
        
        // 获取按钮中的图标和文字
        View firstChild = navItem.getChildAt(0);
        TextView text = (TextView) navItem.getChildAt(1);
        
        ImageView icon = null;
        
        // 检查第一个子元素是ImageView还是FrameLayout
        if (firstChild instanceof ImageView) {
            // 首页、广场、我的：直接是ImageView
            icon = (ImageView) firstChild;
        } else if (firstChild instanceof FrameLayout) {
            // 消息：FrameLayout包含ImageView
            FrameLayout frameLayout = (FrameLayout) firstChild;
            if (frameLayout.getChildCount() > 0) {
                View frameChild = frameLayout.getChildAt(0);
                if (frameChild instanceof ImageView) {
                    icon = (ImageView) frameChild;
                }
            }
        }
        
        if (isSelected) {
            // 选中状态：深色图标和文字
            if (icon != null) {
                icon.setColorFilter(getResources().getColor(android.R.color.black));
            }
            if (text != null) {
                text.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else {
            // 未选中状态：浅色图标和文字
            if (icon != null) {
                icon.setColorFilter(getResources().getColor(android.R.color.darker_gray));
            }
            if (text != null) {
                text.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }
    
    private void selectTab(LinearLayout selectedTab, TextView selectedText) {
        // 重置所有标签状态
        resetAllTabs();
        
        // 设置选中标签状态
        selectedTab.setSelected(true);
        selectedText.setSelected(true);
        // 仅改变视觉高亮，不改变布局尺寸，避免文字下移
        selectedTab.setBackgroundResource(R.drawable.tab_selected_bg);
        selectedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
    }
    
    private void resetAllTabs() {
        // 重置所有标签为未选中状态
        tabActive.setSelected(false);
        tabHot.setSelected(false);
        tabNearby.setSelected(false);
        tabNew.setSelected(false);
        tabExclusive.setSelected(false);
        
        textActive.setSelected(false);
        textHot.setSelected(false);
        textNearby.setSelected(false);
        textNew.setSelected(false);
        textExclusive.setSelected(false);

        // 所有文本还原为 14sp
        textActive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textHot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textNearby.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textNew.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textExclusive.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);

        // 所有容器尺寸还原为 28dp × 20dp
        setTabSize(tabActive, 28, 20);
        setTabSize(tabHot, 28, 20);
        setTabSize(tabNearby, 28, 20);
        setTabSize(tabNew, 28, 20);
        setTabSize(tabExclusive, 28, 20);

        // 恢复默认背景（涟漪效果）
        tabActive.setBackgroundResource(R.drawable.tab_ripple_effect);
        tabHot.setBackgroundResource(R.drawable.tab_ripple_effect);
        tabNearby.setBackgroundResource(R.drawable.tab_ripple_effect);
        tabNew.setBackgroundResource(R.drawable.tab_ripple_effect);
        tabExclusive.setBackgroundResource(R.drawable.tab_ripple_effect);
    }

    private void setTabSize(LinearLayout tab, int widthDp, int heightDp) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.getLayoutParams();
        params.width = dpToPx(widthDp);
        params.height = dpToPx(heightDp);
        tab.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupTestButtons() {
        // 在首页添加身份证二要素核验按钮（长按功能区域触发）
        View testArea = findViewById(R.id.function_entry_area);
        if (testArea != null) {
            testArea.setOnLongClickListener(v -> {
                // 显示测试选项对话框
                showTestOptionsDialog();
                return true;
            });
        }
    }
    
    private void showTestOptionsDialog() {
        String[] options = {
            "身份证实名认证测试",
            "手机身份认证测试",
            "手机认证功能测试",
            "🔔 模拟接收来电通知 (JPush测试)"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择测试功能");
        builder.setItems(options, (dialog, which) -> {
            Intent intent;
            switch (which) {
                case 0:
                    intent = new Intent(MainActivity.this, IdCardVerifyActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(MainActivity.this, PhoneIdentityAuthActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    // PhoneAuthTestActivity 已删除，跳转到手机身份认证
                    intent = new Intent(MainActivity.this, PhoneIdentityAuthActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    // 手动触发来电通知界面（模拟JPush推送）
                    simulateIncomingCall();
                    break;
            }
        });
        builder.show();
    }

    /**
     * 模拟接收来电通知（用于测试JPush推送功能）
     */
    private void simulateIncomingCall() {
        // 创建测试数据
        String testSessionId = "TEST_SESSION_" + System.currentTimeMillis();
        String testCallerId = "23820512";  // video_caller 的用户ID
        String testCallerName = "测试用户";
        String testCallerAvatar = "";
        String testCallType = "VIDEO";  // 或 "VOICE"

        Log.d(TAG, "【测试】模拟接收来电通知 - sessionId: " + testSessionId);

        // 直接启动来电界面
        Intent intent = new Intent(this, IncomingCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sessionId", testSessionId);
        intent.putExtra("callerId", testCallerId);
        intent.putExtra("callerName", testCallerName);
        intent.putExtra("callerAvatar", testCallerAvatar);
        intent.putExtra("callType", testCallType);

        startActivity(intent);

        Toast.makeText(this, "模拟来电通知已触发", Toast.LENGTH_SHORT).show();
    }
    
    private long lastClickTime = 0;
    
}
