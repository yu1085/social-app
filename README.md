# 心聊临摹 - Android应用

这是一个基于Figma设计稿"心聊临摹-广场总界面"开发的Android应用，使用Kotlin + Jetpack Compose技术栈实现。

## 项目特性

### 🎨 精确还原设计
- 完全按照Figma设计稿的布局、间距、颜色、字体大小
- 保持所有视觉元素的精确位置和比例
- 还原所有图标、按钮、文本的样式

### 🚀 技术实现
- **Kotlin + Jetpack Compose**: 现代化的Android UI开发
- **MVVM架构**: 使用ViewModel管理UI状态
- **响应式布局**: 适配不同屏幕尺寸
- **Material Design 3**: 遵循最新的设计规范

### ✨ 功能特性
- 广场动态展示
- 用户信息展示（头像、姓名、年龄、距离、状态）
- 标签页切换（附近、最新、知友、喜欢）
- 点赞功能
- 底部导航栏
- 通知徽章显示

## 项目结构

```
src/main/java/com/example/myapplication/
├── MainActivity.kt                 # 主Activity
├── model/                          # 数据模型
│   └── User.kt                    # 用户和动态数据模型
├── viewmodel/                      # ViewModel层
│   └── SquareViewModel.kt         # 广场界面数据管理
├── ui/                            # UI组件
│   ├── components/                # 可复用组件
│   │   ├── DynamicCard.kt        # 动态卡片组件
│   │   ├── TabRow.kt             # 标签页组件
│   │   └── BottomNavigation.kt   # 底部导航组件
│   ├── screens/                   # 界面
│   │   └── SquareScreen.kt       # 广场界面
│   └── theme/                     # 主题和样式
│       ├── Theme.kt              # 主题定义
│       └── Type.kt               # 字体样式
└── res/                           # 资源文件
    ├── values/
    │   ├── colors.xml            # 颜色定义
    │   ├── dimens.xml            # 尺寸定义
    │   ├── strings.xml           # 字符串资源
    │   └── themes.xml            # 主题样式
    └── drawable/
        └── default_avatar.xml    # 默认头像
```

## 设计还原说明

### 颜色系统
- **主色调**: #FF6B9D (粉色)
- **辅助色**: #4ECDC4 (青色)
- **状态色**: #9CE8AF (空闲), #FFB74D (忙碌)
- **文字色**: #212529 (主要), #6C757D (次要), #C8CAC9 (提示)

### 布局特点
- **顶部标题栏**: 渐变背景，动态图标和标题
- **标签页**: 圆角设计，选中状态高亮
- **动态卡片**: 圆角卡片，阴影效果
- **底部导航**: 图标+文字，当前页面高亮

### 交互功能
- 标签页切换
- 动态点赞
- 用户头像点击
- 底部导航切换

## 运行说明

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Android SDK 34
- Kotlin 1.9.0 或更高版本

### 构建步骤
1. 克隆项目到本地
2. 在Android Studio中打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 点击运行按钮

### 依赖说明
- **Jetpack Compose**: UI框架
- **Navigation Compose**: 导航组件
- **ViewModel Compose**: 状态管理
- **Coil**: 图片加载
- **Material3**: 设计系统

## 设计来源

本应用基于Figma设计稿"心聊临摹-广场总界面"开发，设计稿包含：
- 完整的广场界面布局
- 用户动态卡片设计
- 标签页切换设计
- 底部导航设计
- 状态栏和通知设计

## 开发说明

### 架构模式
- **MVVM**: Model-View-ViewModel架构
- **单向数据流**: 数据从ViewModel流向UI
- **状态管理**: 使用StateFlow管理UI状态

### 组件化
- **可复用组件**: DynamicCard, TabRow, BottomNavigation
- **界面分离**: 每个界面独立成Screen
- **主题系统**: 统一的颜色和字体定义

### 响应式设计
- **适配不同屏幕**: 使用dp和sp单位
- **布局约束**: 合理的间距和尺寸定义
- **状态适配**: 加载、空状态、错误状态处理

## 后续开发计划

- [ ] 添加网络请求功能
- [ ] 实现用户详情页面
- [ ] 添加私信功能
- [ ] 实现个人中心页面
- [ ] 添加图片上传功能
- [ ] 实现搜索功能
- [ ] 添加推送通知
- [ ] 优化性能和内存使用

## 许可证

MIT License
