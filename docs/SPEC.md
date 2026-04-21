# 🐼 拼音乐园 - 跨平台拼音学习应用技术方案

> **项目代号**: PinyinPark
> **目标用户**: 3-8 岁幼儿（幼小衔接阶段）
> **平台**: Android + iOS 原生双端
> **文档版本**: v1.0
> **最后更新**: 2026-04-21

---

## 📋 目录

1. [项目概述](#1-项目概述)
2. [跨平台架构](#2-跨平台架构)
3. [功能模块设计](#3-功能模块设计)
4. [Android 实现方案](#4-android-实现方案)
5. [iOS 实现方案](#5-ios-实现方案)
6. [游戏化设计](#6-游戏化设计)
7. [UI/UX 设计规范](#7-uiux-设计规范)
8. [资源清单](#8-资源清单)
9. [开发计划](#9-开发计划)

---

## 1. 项目概述

### 1.1 产品定位

类似《洪恩拼音》的趣味拼音学习应用，专为幼小衔接阶段的儿童设计。核心特点：

- 🎮 **趣味性**: 游戏化学习，让孩子在玩中学
- 📚 **系统性**: 完整的拼音知识体系（声母、韵母、声调、拼读）
- 🏆 **激励性**: 星星、徽章、宠物养成多维激励
- 🔄 **互动性**: 多种小游戏巩固知识

### 1.2 核心数据

| 类别 | 数量 |
|------|------|
| 声母 | 21 个 |
| 韵母 | 24 个 |
| 声调 | 4 声 + 轻声 |
| 整体认读 | 16 个 |
| 知识点总计 | ~60+ |
| 游戏类型 | 6 款 |
| 徽章成就 | 20+ 个 |

### 1.3 技术选型

| 平台 | 技术栈 | 版本 |
|------|--------|------|
| Android | Kotlin + Jetpack Compose | API 23+ |
| iOS | Swift + SwiftUI | iOS 14+ |
| 共享资源 | 图片、音频、Lottie动画 | - |

**为什么选择原生开发而非 React Native/Flutter？**

1. 儿童 App 对流畅度要求极高，原生性能最优
2. 大量自定义动画和触控交互，原生支持更好
3. iOS/Android 设计规范差异大，原生更易适配
4. 团队已有 Android 原生开发经验（参考 USER.md）

---

## 2. 跨平台架构

### 2.1 项目结构

```
PinyinPark/
├── android/                    # Android 原生工程
│   ├── app/src/main/
│   │   ├── java/com/pinyinpark/
│   │   │   ├── MainActivity.kt          # 入口
│   │   │   ├── model/                  # 数据模型
│   │   │   ├── viewmodel/              # ViewModel
│   │   │   └── ui/
│   │   │       ├── screens/            # 页面
│   │   │       ├── components/         # 组件
│   │   │       └── theme/              # 主题
│   │   └── res/                        # 资源
│   ├── build.gradle.kts
│   └── settings.gradle.kts
├── ios/                         # iOS 原生工程
│   └── PinyinPark/
│       ├── App/
│       ├── Views/
│       ├── ViewModels/
│       └── Models/
├── assets/                      # 共享素材（Git 管理）
│   ├── audio/                  # 拼音音频
│   ├── animations/             # Lottie 动画
│   └── images/                 # 图标、图片
└── docs/                       # 技术文档
    ├── SPEC.md                 # 本文档
    ├── README.md               # Android README
    └── ios/README.md           # iOS README
```

### 2.2 共享资源策略

- **音频资源**: 统一存放于 `assets/audio/`，格式为 MP3
- **动画资源**: Lottie JSON 文件，存放于 `assets/animations/`
- **图片资源**: PNG/WebP 格式，按分辨率组织
- **数据模型**: Android(iOS 略有差异的命名)共享同一份数据定义

---

## 3. 功能模块设计

### 3.1 功能矩阵

| 模块 | 功能点 | 优先级 | 备注 |
|------|--------|--------|------|
| 学习殿堂 | 声母学习 | P0 | 21个声母 |
| 学习殿堂 | 韵母学习 | P0 | 24个韵母 |
| 学习殿堂 | 声调学习 | P1 | 4声+轻声 |
| 学习殿堂 | 拼读练习 | P1 | 声母+韵母组合 |
| 游戏中心 | 拼音配对 | P0 | 连线匹配 |
| 游戏中心 | 拼音地鼠 | P1 | 快速点击 |
| 游戏中心 | 跟我读 | P2 | 语音识别 |
| 游戏中心 | 拼音积木 | P1 | 拖拽组合 |
| 游戏中心 | 音节接龙 | P2 | 同韵接龙 |
| 游戏中心 | 迷宫探险 | P2 | 拼音寻路 |
| 奖励系统 | 星星积分 | P0 | 核心激励 |
| 奖励系统 | 徽章成就 | P1 | 20+ 徽章 |
| 奖励系统 | 宠物养成 | P2 | 宠物喂食、升级 |
| 课后巩固 | 每日5题 | P1 | 智能出题 |
| 课后巩固 | 错题本 | P1 | 自动收录 |
| 课后巩固 | 周测挑战 | P2 | 阶段性测验 |

### 3.2 学习路径图

```
📖 声母家族 (21个) → 🔓 解锁
        ↓
📖 韵母乐园 (24个) → 🔓 解锁
        ↓
📖 四声宫殿 (4声) → 🔓 解锁
        ↓
📖 拼读城堡 (组合) → 🔓 解锁
        ↓
🏆 毕业测试
```

### 3.3 数据模型

```kotlin
// 核心数据结构（Android Kotlin）
enum class PinyinType { SHENG_MU, YUN_MU, SHENG_DIAO, ZHENG_TI }

data class PinyinItem(
    val id: String,              // 唯一标识 "b"
    val character: String,       // 字符 "b"
    val type: PinyinType,        // 类型
    val audioResId: String,      // 音频资源名
    val exampleWord: String,     // 示例词 "爸"
    val examplePinyin: String    // 示例拼音 "bà"
)

data class LessonChapter(
    val id: Int,
    val title: String,          // "声母家族"
    val description: String,    // "认识21个声母小伙伴"
    val iconEmoji: String,       // "🅱️"
    val pinyinItems: List<PinyinItem>,
    val isUnlocked: Boolean,   // 是否解锁
    val completedCount: Int     // 已完成数
)

data class UserProgress(
    val totalStars: Int = 0,
    val streakDays: Int = 0,    // 连续学习天数
    val completedItems: Set<String> = emptySet(),
    val unlockedBadges: Set<String> = emptySet(),
    val petLevel: Int = 1
)

enum class GameType {
    MATCHING, WHACK_MOLE, SPEAK_REPEAT,
    BLOCK_PUZZLE, CHAIN_GAME, MAZE_ADVENTURE
}
```

---

## 4. Android 实现方案

### 4.1 项目配置

**gradle.properties**
```properties
android.useAndroidX=true
android.enableJetifier=true
org.gradle.parallel=true
kotlin.code.style=official
```

**app/build.gradle.kts**
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.pinyinpark"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
        targetSdk = 34
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
```

### 4.2 核心页面

| 页面 | 文件 | 说明 |
|------|------|------|
| 首页 | `HomeScreen.kt` | 章节列表、游戏入口、宠物卡片 |
| 学习页 | `LearnScreen.kt` | 拼音学习、口型动画、进度条 |
| 游戏页 | `GameScreen.kt` | 配对游戏实现 |
| 徽章墙 | `BadgesScreen.kt` | 成就展示 |
| 个人中心 | `ProfileScreen.kt` | 用户信息 |

### 4.3 已实现的代码文件

| 文件路径 | 说明 |
|----------|------|
| `model/PinyinModels.kt` | 数据模型 + 预置数据 |
| `viewmodel/PinyinViewModels.kt` | HomeViewModel / LearnViewModel / MatchGameViewModel |
| `ui/screens/HomeScreen.kt` | 首页 UI (Compose) |
| `ui/screens/GameScreen.kt` | 学习页 + 游戏页 UI |
| `ui/theme/Theme.kt` | 儿童友好配色主题 |
| `MainActivity.kt` | 应用入口 + 导航 |

---

## 5. iOS 实现方案

### 5.1 项目配置

**Package.swift (SPM 依赖)**
```swift
dependencies: [
    .package(url: "https://github.com/airbnb/lottie-ios", from: "4.3.0"),
]
```

### 5.2 核心页面

| 页面 | 文件 | 说明 |
|------|------|------|
| 首页 | `Views/Home/HomeView.swift` | SwiftUI 实现 |
| 学习页 | `Views/Learn/LearnView.swift` | SwiftUI 实现 |
| 游戏页 | `Views/Game/MatchGameView.swift` | 配对游戏 |

### 5.3 详细文档

参见 `ios/README.md`

---

## 6. 游戏化设计

### 6.1 激励体系

```
┌─────────────────────────────────────────────────┐
│                    学习行为                        │
│         ↓                    ↓                   │
│    完成知识点 ───→ 获得星星 ───→ 宠物食物          │
│         ↓                    ↓                   │
│    解锁新章节      解锁成就徽章                    │
│         ↓                    ↓                   │
│    宠物升级 ←←←←← 积分消耗                        │
└─────────────────────────────────────────────────┘
```

### 6.2 奖励规则

| 行为 | 奖励 | 说明 |
|------|------|------|
| 完成单个拼音学习 | ⭐ 1-3 颗 | 根据完成度评分 |
| 游戏通关 (满分) | ⭐ 3 颗 | 每局最多3颗 |
| 游戏通关 (良好) | ⭐ 2 颗 | 60%+ 正确率 |
| 连续学习 3 天 | 🔥 徽章 | "三天小勇士" |
| 连续学习 7 天 | 👑 徽章 | "一周超人" |
| 累计 50 星 | ⭐ 徽章 | "星光闪闪" |

### 6.3 游戏类型详解

| 游戏 | 规则 | 时长 | 难度 |
|------|------|------|------|
| 拼音配对 | 声母与汉字/拼音连线 | 60s | ⭐ |
| 拼音地鼠 | 看到拼音快速点击对应文字 | 45s | ⭐⭐ |
| 跟我读 | 语音识别模仿发音 | 90s | ⭐⭐ |
| 拼音积木 | 拖拽声母+韵母组成音节 | 120s | ⭐⭐⭐ |
| 音节接龙 | 找同韵母的字接龙 | 60s | ⭐⭐ |
| 迷宫探险 | 按拼音指示走迷宫 | 90s | ⭐⭐⭐ |

### 6.4 成就徽章 (部分)

| ID | 名称 | 条件 | 图标 |
|----|------|------|------|
| first_speak | 初次发声 | 首次语音成功 | 🎤 |
| streak_3 | 三天小勇士 | 连续3天学习 | 🔥 |
| streak_7 | 一周超人 | 连续7天学习 | 👑 |
| stars_50 | 星光闪闪 | 累计50星 | ⭐ |
| perfect_3 | 完美三连 | 3次满分 | 💯 |
| game_king | 游戏小王 | 游戏获胜10次 | 🎮 |

### 6.5 防沉迷系统

为保护幼儿身心健康，防止过度游戏，集成防沉迷验证系统。

#### 6.5.1 时间限制策略

| 限制类型 | 默认值 | 说明 |
|----------|--------|------|
| 每日游戏时长 | 30 分钟 | 当日累计游戏时间上限 |
| 单次会话时长 | 15 分钟 | 连续游戏时间上限 |
| 休息提醒间隔 | 10 分钟 | 弹出休息提醒的间隔 |
| 验证触发间隔 | 3 局 | 每完成N局游戏触发验证 |

#### 6.5.2 验证机制

采用**4位数字转大写汉字点选验证**，适合幼儿认知水平：

```
验证码示例：
- 原始数字：1234
- 显示汉字：壹贰叁肆
- 干扰选项：叁肆壹贰、贰叁肆壹、肆壹贰叁
```

**数字到大写汉字映射：**
| 数字 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
|------|---|---|---|---|---|---|---|---|---|---|
| 汉字 | 零 | 壹 | 贰 | 叁 | 肆 | 伍 | 陆 | 柒 | 扒 | 玖 |

#### 6.5.3 验证流程

```
┌──────────────────────────────────────────────────────┐
│                   防沉迷验证流程                        │
├──────────────────────────────────────────────────────┤
│ 1. 游戏完成 → 累计局数 +1                              │
│ 2. 检查是否达到验证间隔（默认每3局）                      │
│ 3. 弹出验证弹窗，显示大写汉字                           │
│ 4. 用户点选正确答案                                     │
│    ├─ 正确 → 继续游戏                                 │
│    └─ 错误 → 重新验证（最多3次）                        │
│        ├─ 3次失败 → 强制休息                          │
│        └─ 超时 → 强制休息                             │
│ 5. 每10分钟弹出休息提醒                                │
│ 6. 达到30分钟上限 → 当日禁止游戏                       │
└──────────────────────────────────────────────────────┘
```

#### 6.5.4 防沉迷状态

| 状态 | 触发条件 | 处理方式 |
|------|----------|----------|
| Normal | 正常状态 | 无需干预 |
| ShowReminder | 连续游戏10分钟 | 弹出休息提醒弹窗 |
| ShowVerification | 每3局游戏 | 弹出验证码验证 |
| VerificationFailed | 验证失败3次或超时 | 强制休息 |
| TimeLimitReached | 今日游戏30分钟用完 | 当日禁止游戏 |

#### 6.5.5 实现文件

| 文件 | 说明 |
|------|------|
| `model/PinyinModels.kt` | AntiAddictionSettings, UserGameData, VerificationChallenge |
| `viewmodel/AntiAddictionManager.kt` | 防沉迷状态管理、验证逻辑 |
| `ui/components/AntiAddictionComponents.kt` | 验证弹窗、提醒弹窗、指示器 |
| `ui/screens/GameScreen.kt` | MatchGameScreen 集成防沉迷 |

---

## 7. UI/UX 设计规范

### 7.1 配色方案

| 用途 | 色值 | 说明 |
|------|------|------|
| 主色 (PandaOrange) | `#FF9800` | 温暖、友好 |
| 次色 (PandaBlue) | `#2196F3` | 稳重、可信 |
| 强调色 (PandaGreen) | `#4CAF50` | 成功、激励 |
| 背景色 (WarmYellow) | `#FFF8E1` | 温馨、护眼 |
| 错误色 (ErrorRed) | `#F44336` | 温和提示 |
| 游戏粉 | `#FCE4EC` | 游戏区域 |
| 游戏蓝 | `#E3F2FD` | 学习区域 |

### 7.2 字体规范

| 场景 | 字号 | 字重 |
|------|------|------|
| 拼音大字 (卡片) | 80sp | Bold |
| 标题 | 24-32sp | Bold |
| 副标题 | 18-20sp | SemiBold |
| 正文 | 16sp | Regular |
| 标签/提示 | 12-14sp | Medium |

### 7.3 触控规范

| 类型 | 最小尺寸 | 说明 |
|------|----------|------|
| 最小触控 | 48×48dp | Android 标准 |
| 儿童推荐 | 56×56dp | 更易点击 |
| 按钮高度 | 48dp | 标准按钮 |
| 间距基数 | 8dp | 8dp 网格 |

### 7.4 动画时长

| 类型 | 时长 | 说明 |
|------|------|------|
| 微交互 (涟漪) | 50-100ms | 即时反馈 |
| 简单动画 | 100-200ms | 按钮、切换 |
| 中等动画 | 200-300ms | 展开、折叠 |
| 复杂动画 | 300-500ms | 页面切换、庆祝 |

### 7.5 界面示例

**首页布局**
```
┌─────────────────────────────┐
│  🐼 拼音乐园                 │  ← 顶栏 (橙色)
├─────────────────────────────┤
│  ⭐ 12 星星   🔥 连续3天     │  ← 进度卡片 (暖黄)
├─────────────────────────────┤
│  📚 学习章节                  │
│  ┌─────────────────────────┐│
│  │ 🅱️ 声母家族    3/21     ││  ← 章节卡片
│  └─────────────────────────┘│
│  ┌─────────────────────────┐│
│  │ 🎵 韵母乐园    🔒       ││
│  └─────────────────────────┘│
├─────────────────────────────┤
│  🎮 趣味游戏                 │
│  [🔗配对] [🐹地鼠] [🎤读]   │  ← 横向滚动
├─────────────────────────────┤
│  🐰 我的小宠物               │
│  ┌─────────────────────────┐│
│  │ 🐰 等级 Lv.1   [🍎喂食] ││  ← 宠物卡片
│  └─────────────────────────┘│
└─────────────────────────────┘
```

**学习页布局**
```
┌─────────────────────────────┐
│  ← 声母家族                  │  ← 顶栏
├─────────────────────────────┤
│  ████████░░░░░░░  5/21      │  ← 进度条
├─────────────────────────────┤
│                             │
│         ┌───────┐           │
│         │       │           │
│         │   b   │           │  ← 拼音卡片 (蓝色圆形)
│         │   爸   │           │
│         │       │           │
│         └───────┘           │
│                             │
│     [ 🔊 点击听发音 ]        │  ← 播放按钮 (橙色)
│                             │
│   👄 口型动画 [开关]         │  ← 口型开关
│                             │
├─────────────────────────────┤
│  [← 上一个]     [下一个 →]  │  ← 导航按钮
└─────────────────────────────┘
```

---

## 8. 资源清单

### 8.1 音频资源

| 类型 | 数量 | 格式 | 示例 |
|------|------|------|------|
| 声母发音 | 21 | MP3 | `b.mp3`, `p.mp3` |
| 韵母发音 | 24 | MP3 | `a.mp3`, `o.mp3` |
| 声调发音 | 5 | MP3 | `yin1.mp3` - `yin4.mp3` |
| 示例词发音 | 60+ | MP3 | `ba.mp3`, `ma.mp3` |
| 音效 | 10+ | MP3 | `correct.mp3`, `wrong.mp3`, `cheer.mp3` |

### 8.2 动画资源

| 名称 | 用途 | 格式 |
|------|------|------|
| 口型动画 | 发音口型展示 | Lottie JSON |
| 星星动画 | 获得星星庆祝 | Lottie JSON |
| 徽章动画 | 解锁徽章特效 | Lottie JSON |
| 宠物动画 | 宠物idle/开心/喂食 | Lottie JSON |

### 8.3 图片资源

| 类型 | 尺寸 | 格式 |
|------|------|------|
| App Icon | 1024×1024 | PNG |
| 启动页 | 1080×1920 | PNG/WebP |
| 章节图标 | 256×256 | PNG (含 @2x @3x) |
| 游戏素材 | 512×512 | PNG (含 @2x @3x) |
| 宠物精灵 | 256×256 | PNG 序列帧 |

---

## 9. 开发计划

### 9.1 阶段划分

| 阶段 | 内容 | Android | iOS |
|------|------|---------|-----|
| **Phase 1** | 基础框架 + 学习模块 | 3 天 | 3 天 |
| **Phase 2** | 游戏中心 (6款游戏) | 8 天 | 8 天 |
| **Phase 3** | 奖励系统 + 宠物 | 3 天 | 3 天 |
| **Phase 4** | 课后巩固 (每日5题等) | 3 天 | 3 天 |
| **Phase 5** | 资源制作 (音频/动画) | 5 天 | 5 天 |
| **Phase 6** | 测试调优 + 上架 | 4 天 | 4 天 |
| **总计** | - | **26 天** | **26 天** |

### 9.2 里程碑

- **M1 (Day 3)**: Android 首页 + 单个拼音学习页可运行
- **M2 (Day 11)**: 完成 2-3 款游戏可玩
- **M3 (Day 14)**: Android 完整学习流程可跑通
- **M4 (Day 20)**: 奖励系统 + 宠物系统上线
- **M5 (Day 26)**: 双端测试完成，准备上架

### 9.3 风险点

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 音频资源制作周期长 | 可能阻塞开发 | 先行使用占位音频 |
| 语音识别精度不足 | "跟我读"体验差 | 提供备选方案（按键确认） |
| 儿童注意力短 | 游戏时长控制 | 控制在 3-5 分钟内 |
| App Store 审核 | 上架周期 | 提前准备分级材料 |

---

## 📎 附录

### A. 已实现代码文件清单

| 平台 | 文件 | 状态 |
|------|------|------|
| Android | `android/app/build.gradle.kts` | ✅ |
| Android | `android/app/src/main/AndroidManifest.xml` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/MainActivity.kt` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/model/PinyinModels.kt` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/viewmodel/PinyinViewModels.kt` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/ui/screens/HomeScreen.kt` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/ui/screens/GameScreen.kt` | ✅ |
| Android | `android/app/src/main/java/com/pinyinpark/ui/theme/Theme.kt` | ✅ |
| Android | `android/gradle.properties` | ✅ |
| Android | `android/settings.gradle.kts` | ✅ |
| Android | `android/build.gradle.kts` | ✅ |
| iOS | `ios/README.md` | ✅ |

### B. 技术参考

- **Android**: Jetpack Compose, Material 3
- **iOS**: SwiftUI, Combine
- **动画**: Lottie
- **设计规范**: [Material Design 3](https://m3.material.io/), [Apple HIG](https://developer.apple.com/design/human-interface-guidelines/)

---

> 📝 **文档维护**: 本文档随项目迭代更新，如有问题请联系开发团队。