package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.LifecycleCoroutineScope;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    
    private LinearLayout tabActive, tabHot, tabNearby, tabNew, tabExclusive;
    private LinearLayout navHome, navSquare, navMessage, navProfile;
    private LinearLayout filterButton;
    private LinearLayout videoMatchButton;
    private LinearLayout voiceMatchButton;
    private ComposeView composeSquare, composeMessage, composeProfile;
    private TextView textActive, textHot, textNearby, textNew, textExclusive;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 应用启动完成
        
        Log.d("MainActivity", "onCreate 开始执行");
        
        // 初始化标签视图
        initTabViews();
        Log.d("MainActivity", "initTabViews 完成");
        
        // 设置标签点击监听器
        setTabClickListeners();
        Log.d("MainActivity", "setTabClickListeners 完成");

        // 底部导航点击
        initBottomNav();
        Log.d("MainActivity", "initBottomNav 完成");
        
        // 筛选按钮点击
        initFilterButton();
        Log.d("MainActivity", "initFilterButton 完成");
        
        // 测试筛选按钮是否被找到
        testFilterButton();
        
        // 匹配按钮点击
        initMatchButtons();
        Log.d("MainActivity", "initMatchButtons 完成");

        // 设置用户卡片点击事件
        Log.d("MainActivity", "准备调用 setupUserCardClickListeners");
        setupUserCardClickListeners();
        Log.d("MainActivity", "setupUserCardClickListeners 调用完成");


        // 初始化ProfileComposeHost
        if (composeProfile != null) {
            com.example.myapplication.compose.ProfileComposeHost.attach(composeProfile);
            Log.d("MainActivity", "ProfileComposeHost 初始化完成");
        }

        // 初始状态：所有标签保持相同样式与尺寸
        resetAllTabs();
        Log.d("MainActivity", "onCreate 执行完成");
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
            navHome.setOnClickListener(v -> showHome());
        }
        if (navSquare != null) {
            navSquare.setOnClickListener(v -> showSquare());
        }
        if (navMessage != null) {
            navMessage.setOnClickListener(v -> showMessage());
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> showProfile());
        }
    }
    
    private void initFilterButton() {
        Log.d("MainActivity", "initFilterButton 开始执行");
        if (filterButton != null) {
            Log.d("MainActivity", "filterButton 不为空，设置点击监听器");
            filterButton.setOnClickListener(v -> {
                Log.d("MainActivity", "筛选按钮被点击");
                showFilterDialog();
            });
        } else {
            Log.e("MainActivity", "filterButton 为空！");
        }
        Log.d("MainActivity", "initFilterButton 执行完成");
    }
    
    private void testFilterButton() {
        Log.d("MainActivity", "testFilterButton 开始执行");
        View filterButton = findViewById(R.id.filter_button);
        if (filterButton != null) {
            Log.d("MainActivity", "筛选按钮找到，可见性: " + filterButton.getVisibility());
            Log.d("MainActivity", "筛选按钮可点击: " + filterButton.isClickable());
            Log.d("MainActivity", "筛选按钮可聚焦: " + filterButton.isFocusable());
        } else {
            Log.e("MainActivity", "筛选按钮未找到！");
        }
        Log.d("MainActivity", "testFilterButton 执行完成");
    }
    
    private void showFilterDialog() {
        Log.d("MainActivity", "showFilterDialog 开始执行");
        
        // 跳转到筛选Activity
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
        Log.d("MainActivity", "已启动筛选Activity");
    }
    
    private void initMatchButtons() {
        Log.d("MainActivity", "initMatchButtons 开始执行");
        
        // 视频速配按钮
        if (videoMatchButton != null) {
            Log.d("MainActivity", "videoMatchButton 不为空，设置点击监听器");
            videoMatchButton.setOnClickListener(v -> {
                Log.d("MainActivity", "视频速配按钮被点击");
                Intent intent = new Intent(this, VideoMatchActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("MainActivity", "videoMatchButton 为空！");
        }
        
        // 语音速配按钮
        if (voiceMatchButton != null) {
            Log.d("MainActivity", "voiceMatchButton 不为空，设置点击监听器");
            voiceMatchButton.setOnClickListener(v -> {
                Log.d("MainActivity", "语音速配按钮被点击");
                Intent intent = new Intent(this, VoiceMatchActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("MainActivity", "voiceMatchButton 为空！");
        }
        
        Log.d("MainActivity", "initMatchButtons 执行完成");
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
        Log.d("MainActivity", "showProfile 开始执行");
        hideAllContent();
        if (composeProfile != null) {
            Log.d("MainActivity", "composeProfile 不为空，设置可见");
            composeProfile.setVisibility(View.VISIBLE);
            // ProfileComposeHost.attach() 已经在onCreate中调用过了，这里不需要重复调用
            Log.d("MainActivity", "showProfile 执行完成");
        } else {
            Log.e("MainActivity", "composeProfile 为空！");
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
    
    private void setupUserCardClickListeners() {
        try {
            // 为首页的用户卡片设置点击事件
            // 用户卡片1 - 不吃香菜
            View userCard1 = findViewById(R.id.user_card_1);
            if (userCard1 != null) {
                userCard1.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    intent.putExtra("user_name", "不吃香菜");
                    intent.putExtra("user_status", "空闲");
                    intent.putExtra("user_age", "25岁");
                    intent.putExtra("user_location", "北京");
                    intent.putExtra("user_description", "我是一个活泼开朗的女孩，喜欢聊天和交朋友。希望能遇到有趣的人一起分享生活的美好。");
                    intent.putExtra("user_avatar", R.drawable.rectangle_411_1);
                    startActivity(intent);
                });
            }
            
            // 用户卡片2 - 你的菜
            View userCard2 = findViewById(R.id.user_card_2);
            if (userCard2 != null) {
                userCard2.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    intent.putExtra("user_name", "你的菜");
                    intent.putExtra("user_status", "忙碌");
                    intent.putExtra("user_age", "23岁");
                    intent.putExtra("user_location", "广州");
                    intent.putExtra("user_description", "温柔可爱的女孩，喜欢听音乐和看电影。希望能找到志同道合的朋友。");
                    intent.putExtra("user_avatar", R.drawable.rectangle_412_1);
                    startActivity(intent);
                });
            }
            
            // 用户卡片3 - 小仙女
            View userCard3 = findViewById(R.id.user_card_3);
            if (userCard3 != null) {
                userCard3.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    intent.putExtra("user_name", "小仙女");
                    intent.putExtra("user_status", "在线");
                    intent.putExtra("user_age", "26岁");
                    intent.putExtra("user_location", "上海");
                    intent.putExtra("user_description", "充满正能量的女孩，喜欢运动和旅行。希望能遇到有趣的人一起分享快乐。");
                    intent.putExtra("user_avatar", R.drawable.rectangle_411_1);
                    startActivity(intent);
                });
            }
            
            // 用户卡片4 - 甜心宝贝
            View userCard4 = findViewById(R.id.user_card_4);
            if (userCard4 != null) {
                userCard4.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    intent.putExtra("user_name", "甜心宝贝");
                    intent.putExtra("user_status", "离线");
                    intent.putExtra("user_age", "28岁");
                    intent.putExtra("user_location", "深圳");
                    intent.putExtra("user_description", "成熟稳重的姐姐，善解人意，喜欢读书和品茶。希望能找到真诚的朋友。");
                    intent.putExtra("user_avatar", R.drawable.rectangle_412_1);
                    startActivity(intent);
                });
            }
            
            // 测试所有用户卡片是否被找到
            // testUserCards(); // 删除这行，因为 testUserCards() 已经显示了结果
        } catch (Exception e) {
            Log.e("MainActivity", "setupUserCardClickListeners 执行失败", e);
        }
    }
    
}
