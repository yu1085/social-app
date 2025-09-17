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
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.viewmodel.UserViewModel;
import com.example.myapplication.model.UserCard;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import kotlinx.coroutines.withContext;

public class MainActivity extends AppCompatActivity {
    
    private LinearLayout tabActive, tabHot, tabNearby, tabNew, tabExclusive;
    private LinearLayout navHome, navSquare, navMessage, navProfile;
    private LinearLayout filterButton;
    private LinearLayout videoMatchButton;
    private LinearLayout voiceMatchButton;
    private ComposeView composeSquare, composeMessage, composeProfile;
    private TextView textActive, textHot, textNearby, textNew, textExclusive;
    
    // 用户数据管理
    private UserViewModel userViewModel;
    private java.util.List<UserCard> userCards = new java.util.ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化用户ViewModel
        initUserViewModel();
        
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
        
        // 加载动态用户数据
        loadDynamicUserCards();

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
        // 用户卡片1
        View userCard1 = findViewById(R.id.user_card_1);
        if (userCard1 != null) {
            userCard1.setOnClickListener(v -> {
                if (userCards.size() > 0) {
                    openUserDetail(userCards.get(0));
                } else {
                    // 使用默认数据作为后备
                    openUserDetailWithDefaultData(1);
                }
            });
        }
        
        // 用户卡片2
        View userCard2 = findViewById(R.id.user_card_2);
        if (userCard2 != null) {
            userCard2.setOnClickListener(v -> {
                if (userCards.size() > 1) {
                    openUserDetail(userCards.get(1));
                } else {
                    // 使用默认数据作为后备
                    openUserDetailWithDefaultData(2);
                }
            });
        }
        
        // 用户卡片3
        View userCard3 = findViewById(R.id.user_card_3);
        if (userCard3 != null) {
            userCard3.setOnClickListener(v -> {
                if (userCards.size() > 2) {
                    openUserDetail(userCards.get(2));
                } else {
                    // 使用默认数据作为后备
                    openUserDetailWithDefaultData(3);
                }
            });
        }
        
        // 用户卡片4
        View userCard4 = findViewById(R.id.user_card_4);
        if (userCard4 != null) {
            userCard4.setOnClickListener(v -> {
                if (userCards.size() > 3) {
                    openUserDetail(userCards.get(3));
                } else {
                    // 使用默认数据作为后备
                    openUserDetailWithDefaultData(4);
                }
            });
        }
    }
    
    /**
     * 打开用户详情页面（使用动态数据）
     */
    private void openUserDetail(UserCard userCard) {
        Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
        intent.putExtra("user_id", userCard.id);
        intent.putExtra("user_name", userCard.getDisplayName());
        intent.putExtra("user_status", userCard.getStatusText());
        intent.putExtra("user_age", userCard.getAgeText());
        intent.putExtra("user_location", userCard.location);
        intent.putExtra("user_description", userCard.bio);
        intent.putExtra("user_avatar", userCard.avatar);
        intent.putExtra("call_price", userCard.callPrice);
        intent.putExtra("message_price", userCard.messagePrice);
        intent.putExtra("is_online", userCard.isOnline);
        startActivity(intent);
    }
    
    /**
     * 打开用户详情页面（使用默认数据作为后备）
     */
    private void openUserDetailWithDefaultData(int cardIndex) {
        Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
        
        switch (cardIndex) {
            case 1:
                intent.putExtra("user_name", "不吃香菜");
                intent.putExtra("user_status", "空闲");
                intent.putExtra("user_age", "25岁");
                intent.putExtra("user_location", "北京");
                intent.putExtra("user_description", "我是一个活泼开朗的女孩，喜欢聊天和交朋友。希望能遇到有趣的人一起分享生活的美好。");
                intent.putExtra("user_avatar", R.drawable.rectangle_411_1);
                break;
            case 2:
                intent.putExtra("user_name", "你的菜");
                intent.putExtra("user_status", "忙碌");
                intent.putExtra("user_age", "23岁");
                intent.putExtra("user_location", "广州");
                intent.putExtra("user_description", "温柔可爱的女孩，喜欢听音乐和看电影。希望能找到志同道合的朋友。");
                intent.putExtra("user_avatar", R.drawable.rectangle_412_1);
                break;
            case 3:
                intent.putExtra("user_name", "小仙女");
                intent.putExtra("user_status", "在线");
                intent.putExtra("user_age", "26岁");
                intent.putExtra("user_location", "上海");
                intent.putExtra("user_description", "充满正能量的女孩，喜欢运动和旅行。希望能遇到有趣的人一起分享快乐。");
                intent.putExtra("user_avatar", R.drawable.rectangle_411_1);
                break;
            case 4:
                intent.putExtra("user_name", "甜心宝贝");
                intent.putExtra("user_status", "离线");
                intent.putExtra("user_age", "28岁");
                intent.putExtra("user_location", "深圳");
                intent.putExtra("user_description", "成熟稳重的姐姐，善解人意，喜欢读书和品茶。希望能找到真诚的朋友。");
                intent.putExtra("user_avatar", R.drawable.rectangle_412_1);
                break;
        }
        
        startActivity(intent);
    }
    
    /**
     * 初始化用户ViewModel
     */
    private void initUserViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // 观察用户卡片数据变化
        userViewModel.userCards.observe(this, cards -> {
            if (cards != null) {
                userCards = cards;
                updateUserCardsUI();
            }
        });
        
        // 观察加载状态
        userViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                Log.d("MainActivity", "正在加载用户数据...");
            } else {
                Log.d("MainActivity", "用户数据加载完成");
            }
        });
        
        // 观察错误信息
        userViewModel.errorMessage.observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log.e("MainActivity", "用户数据加载错误: " + errorMessage);
                Toast.makeText(this, "加载用户数据失败: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 加载动态用户卡片数据
     */
    private void loadDynamicUserCards() {
        Log.d("MainActivity", "开始加载动态用户卡片数据...");
        userViewModel.loadUserCards();
    }
    
    /**
     * 更新用户卡片UI
     */
    private void updateUserCardsUI() {
        if (userCards.isEmpty()) {
            Log.d("MainActivity", "用户卡片数据为空，使用默认数据");
            return;
        }
        
        Log.d("MainActivity", "更新用户卡片UI，用户数量: " + userCards.size());
        
        // 更新前4个用户卡片
        updateUserCard(1, userCards.size() > 0 ? userCards.get(0) : null);
        updateUserCard(2, userCards.size() > 1 ? userCards.get(1) : null);
        updateUserCard(3, userCards.size() > 2 ? userCards.get(2) : null);
        updateUserCard(4, userCards.size() > 3 ? userCards.get(3) : null);
    }
    
    /**
     * 更新单个用户卡片
     */
    private void updateUserCard(int cardIndex, UserCard userCard) {
        if (userCard == null) return;
        
        try {
            // 获取卡片视图
            View cardView = findViewById(getCardViewId(cardIndex));
            if (cardView == null) return;
            
            // 更新用户名
            TextView nameView = cardView.findViewById(getUserNameId(cardIndex));
            if (nameView != null) {
                nameView.setText(userCard.getDisplayName());
            }
            
            // 更新状态
            TextView statusView = cardView.findViewById(getUserStatusId(cardIndex));
            if (statusView != null) {
                statusView.setText(userCard.getStatusText());
            }
            
            // 更新价格
            TextView priceView = cardView.findViewById(getUserPriceId(cardIndex));
            if (priceView != null) {
                priceView.setText(userCard.getPriceText());
            }
            
            // 更新位置
            TextView locationView = cardView.findViewById(getUserLocationId(cardIndex));
            if (locationView != null) {
                locationView.setText(userCard.location);
            }
            
            // 更新状态指示器颜色
            View statusIndicator = cardView.findViewById(getUserStatusIndicatorId(cardIndex));
            if (statusIndicator != null) {
                int color = android.graphics.Color.parseColor(userCard.getStatusColor());
                statusIndicator.setBackgroundColor(color);
            }
            
            Log.d("MainActivity", "更新用户卡片 " + cardIndex + ": " + userCard.getDisplayName());
            
        } catch (Exception e) {
            Log.e("MainActivity", "更新用户卡片失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取卡片视图ID
     */
    private int getCardViewId(int cardIndex) {
        switch (cardIndex) {
            case 1: return R.id.user_card_1;
            case 2: return R.id.user_card_2;
            case 3: return R.id.user_card_3;
            case 4: return R.id.user_card_4;
            default: return 0;
        }
    }
    
    /**
     * 获取用户名TextView ID
     */
    private int getUserNameId(int cardIndex) {
        // 这里需要根据实际的布局文件来设置
        // 由于布局文件中可能没有直接的ID，我们可能需要通过其他方式获取
        return 0; // 暂时返回0，需要根据实际布局调整
    }
    
    /**
     * 获取用户状态TextView ID
     */
    private int getUserStatusId(int cardIndex) {
        return 0; // 暂时返回0，需要根据实际布局调整
    }
    
    /**
     * 获取用户价格TextView ID
     */
    private int getUserPriceId(int cardIndex) {
        return 0; // 暂时返回0，需要根据实际布局调整
    }
    
    /**
     * 获取用户位置TextView ID
     */
    private int getUserLocationId(int cardIndex) {
        return 0; // 暂时返回0，需要根据实际布局调整
    }
    
    /**
     * 获取用户状态指示器View ID
     */
    private int getUserStatusIndicatorId(int cardIndex) {
        return 0; // 暂时返回0，需要根据实际布局调整
    }
    
    /**
     * 刷新用户数据
     */
    public void refreshUserData() {
        Log.d("MainActivity", "刷新用户数据");
        userViewModel.refreshUserCards();
    }
    
}
