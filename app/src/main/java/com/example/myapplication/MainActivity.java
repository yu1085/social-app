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

        // 设置用户卡片点击事件
        setupUserCardClickListeners();
        
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
        startActivity(intent);
    }
    
    private void initMatchButtons() {
        // 视频速配按钮
        if (videoMatchButton != null) {
            videoMatchButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, VideoMatchActivity.class);
                startActivity(intent);
            });
        }
        
        // 语音速配按钮
        if (voiceMatchButton != null) {
            voiceMatchButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, VoiceMatchActivity.class);
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
    
    private void setupUserCardClickListeners() {
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
            "手机认证功能测试"
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
            }
        });
        builder.show();
    }
    
    private long lastClickTime = 0;
    
}
