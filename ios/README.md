# 🐼 拼音乐园 iOS 开发指南

> 基于 SwiftUI 的 iOS 端实现方案

---

## 📋 项目结构

```
ios/PinyinPark/
├── App/
│   └── PinyinParkApp.swift           # App 入口
├── Views/
│   ├── Home/
│   │   ├── HomeView.swift            # 首页
│   │   ├── UserProgressCard.swift   # 进度卡片
│   │   └── ChapterCard.swift        # 章节卡片
│   ├── Learn/
│   │   ├── LearnView.swift          # 学习页面
│   │   └── PinyinCard.swift         # 拼音卡片
│   └── Game/
│       ├── MatchGameView.swift       # 配对游戏
│       └── GameResultView.swift      # 结果页面
├── ViewModels/
│   ├── HomeViewModel.swift
│   ├── LearnViewModel.swift
│   └── MatchGameViewModel.swift
├── Models/
│   ├── PinyinModels.swift
│   ├── LessonChapter.swift
│   └── GameResult.swift
├── Resources/
│   ├── Audio/                        # 拼音音频
│   ├── Assets.xcassets/             # 图片资源
│   └── Localizable.strings          # 本地化
└── Info.plist
```

---

## 🎯 SwiftUI 核心实现

### 1. 数据模型 (Models/PinyinModels.swift)

```swift
import Foundation

enum PinyinType: String, CaseIterable {
    case shengMu = "声母"
    case yunMu = "韵母"
    case shengDiao = "声调"
    case zhengTi = "整体认读"
}

struct PinyinItem: Identifiable, Codable {
    let id: String
    let character: String
    let type: PinyinType
    let exampleWord: String
    let examplePinyin: String
    var isUnlocked: Bool = false
    var learnProgress: Int = 0
}

struct LessonChapter: Identifiable {
    let id: Int
    let title: String
    let description: String
    let iconEmoji: String
    let pinyinItems: [PinyinItem]
    var isUnlocked: Bool = false
    var completedCount: Int = 0

    var totalCount: Int { pinyinItems.count }
    var progressPercent: Float {
        totalCount == 0 ? 0 : Float(completedCount) / Float(totalCount)
    }
}

struct UserProgress: Codable {
    var userId: String = "local_user"
    var totalStars: Int = 0
    var streakDays: Int = 0
    var completedItems: Set<String> = []
    var unlockedBadges: Set<String> = []
    var petLevel: Int = 1
}

enum GameType: String, CaseIterable {
    case matching = "拼音配对"
    case whackMole = "拼音地鼠"
    case speakRepeat = "跟我读"
    case blockPuzzle = "拼音积木"
    case chainGame = "音节接龙"
    case mazeAdventure = "迷宫探险"
}
```

### 2. 主页视图 (Views/Home/HomeView.swift)

```swift
import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    @State private var selectedChapter: LessonChapter?
    @State private var selectedGame: GameType?

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 16) {
                    // 用户进度卡片
                    UserProgressCard(progress: viewModel.userProgress)

                    // 学习章节
                    SectionTitle("📚 学习章节")
                    LazyVStack(spacing: 12) {
                        ForEach(viewModel.chapters) { chapter in
                            ChapterCard(chapter: chapter)
                                .onTapGesture {
                                    if chapter.isUnlocked {
                                        selectedChapter = chapter
                                    }
                                }
                        }
                    }
                    .padding(.horizontal)

                    // 游戏入口
                    SectionTitle("🎮 趣味游戏")
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(GameType.allCases, id: \.self) { game in
                                GameCard(gameType: game)
                                    .onTapGesture {
                                        selectedGame = game
                                    }
                            }
                        }
                        .padding(.horizontal)
                    }

                    // 宠物养成
                    SectionTitle("🐰 我的小宠物")
                    PetCard(petLevel: viewModel.userProgress.petLevel)
                        .padding(.horizontal)
                }
                .padding(.bottom, 24)
            }
            .navigationTitle("🐼 拼音乐园")
            .navigationDestination(item: $selectedChapter) { chapter in
                LearnView(chapter: chapter)
            }
            .sheet(item: $selectedGame) { game in
                MatchGameView()
            }
        }
    }
}
```

### 3. 学习页面 (Views/Learn/LearnView.swift)

```swift
import SwiftUI

struct LearnView: View {
    let chapter: LessonChapter
    @StateObject private var viewModel: LearnViewModel
    @Environment(\.dismiss) private var dismiss

    init(chapter: LessonChapter) {
        self.chapter = chapter
        _viewModel = StateObject(wrappedValue: LearnViewModel(chapter: chapter))
    }

    var body: some View {
        VStack(spacing: 24) {
            // 进度条
            ProgressView(value: viewModel.progressValue)
                .tint(Color.green)
                .padding(.horizontal)

            Text("\(viewModel.currentIndex + 1) / \(viewModel.totalItems)")
                .foregroundColor(.gray)

            Spacer()

            // 拼音卡片
            if let item = viewModel.currentItem {
                PinyinCard(item: item, isPlaying: viewModel.isPlayingAudio)
            }

            Spacer()

            // 操作按钮
            PlayAudioButton(isPlaying: viewModel.isPlayingAudio) {
                viewModel.playAudio()
            }

            HStack {
                Button(action: { viewModel.prevItem() }) {
                    Label("上一个", systemImage: "chevron.left")
                }
                .disabled(!viewModel.canGoPrev)
                .buttonStyle(.bordered)

                Button(action: { viewModel.nextItem() }) {
                    Label("下一个", systemImage: "chevron.right")
                }
                .buttonStyle(.borderedProminent)
                .disabled(!viewModel.canGoNext)
            }
        }
        .padding()
        .navigationTitle(chapter.title)
        .alert("太棒了！", isPresented: $viewModel.showCompletion) {
            Button("继续学习") {
                dismiss()
            }
        } message: {
            Text("完成了本章节学习，获得 \(viewModel.starsEarned) 颗星！")
        }
    }
}

struct PinyinCard: View {
    let item: PinyinItem
    let isPlaying: Bool

    var body: some View {
        ZStack {
            Circle()
                .fill(Color.blue)
                .frame(width: 200, height: 200)
                .shadow(radius: 8)

            VStack {
                Text(item.character)
                    .font(.system(size: 80, weight: .bold))
                    .foregroundColor(.white)

                Text(item.exampleWord)
                    .font(.title)
                    .foregroundColor(.white.opacity(0.8))
            }

            if isPlaying {
                Image(systemName: "speaker.wave.2.fill")
                    .foregroundColor(.white)
                    .font(.title)
                    .position(x: 170, y: 30)
            }
        }
        .scaleEffect(isPlaying ? 1.1 : 1.0)
        .animation(.spring(), value: isPlaying)
    }
}

struct PlayAudioButton: View {
    let isPlaying: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: isPlaying ? "waveform" : "speaker.wave.2.fill")
                Text(isPlaying ? "播放中..." : "点击听发音")
            }
            .font(.title3)
            .padding(.horizontal, 24)
            .padding(.vertical, 12)
        }
        .buttonStyle(.borderedProminent)
        .tint(.orange)
        .disabled(isPlaying)
    }
}
```

### 4. 配对游戏 (Views/Game/MatchGameView.swift)

```swift
import SwiftUI

struct MatchGameView: View {
    @StateObject private var viewModel = MatchGameViewModel()
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 16) {
            // 顶部状态栏
            HStack {
                Button("退出") { dismiss() }
                Spacer()
                Text("⏱️ \(viewModel.timeLeft)s")
                Text("得分: \(viewModel.score)")
                    .padding(.leading)
            }
            .padding(.horizontal)

            Text("把相同的拼音连在一起！")
                .foregroundColor(.gray)

            if !viewModel.isGameOver {
                // 配对区域
                HStack(spacing: 40) {
                    // 左侧
                    VStack(spacing: 16) {
                        ForEach(viewModel.leftItems) { item in
                            MatchItemView(
                                item: item,
                                isMatched: viewModel.matchedPairs.contains(item.id),
                                isSelected: viewModel.selectedLeft == item.id,
                                onTap: { viewModel.selectLeft(item.id) }
                            )
                        }
                    }

                    Text("🔗")
                        .font(.system(size: 40))

                    // 右侧（打乱顺序）
                    VStack(spacing: 16) {
                        ForEach(viewModel.rightItems) { item in
                            MatchItemView(
                                item: item,
                                isMatched: viewModel.matchedPairs.contains(item.id),
                                isSelected: viewModel.selectedRight == item.id,
                                onTap: { viewModel.selectRight(item.id) }
                            )
                        }
                    }
                }
            } else {
                // 游戏结果
                GameResultView(
                    result: viewModel.result,
                    onRestart: { viewModel.restartGame() },
                    onExit: { dismiss() }
                )
            }
        }
        .padding()
        .navigationBarHidden(true)
    }
}

struct MatchItemView: View {
    let item: PinyinItem
    let isMatched: Bool
    let isSelected: Bool
    let onTap: () -> Void

    var backgroundColor: Color {
        if isMatched { return .green }
        if isSelected { return .orange }
        return .white
    }

    var body: some View {
        Button(action: onTap) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(backgroundColor)
                    .frame(width: 80, height: 80)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(isSelected ? Color.orange : Color.gray, lineWidth: 3)
                    )

                if isMatched {
                    Image(systemName: "checkmark")
                        .foregroundColor(.white)
                        .font(.title)
                } else {
                    Text(item.character)
                        .font(.system(size: 32, weight: .bold))
                }
            }
        }
        .disabled(isMatched)
    }
}

struct GameResultView: View {
    let result: GameResult?
    let onRestart: () -> Void
    let onExit: () -> Void

    var body: some View {
        VStack(spacing: 20) {
            Text("🎮 游戏结束")
                .font(.largeTitle.bold())

            if let result = result {
                Text("得分: \(result.score) / \(result.maxScore)")
                    .font(.title)

                HStack(spacing: 8) {
                    ForEach(0..<3, id: \.self) { index in
                        Text(index < result.starsEarned ? "⭐" : "☆")
                            .font(.system(size: 40))
                    }
                }
            }

            HStack(spacing: 16) {
                Button("返回") { onExit() }
                    .buttonStyle(.bordered)
                Button("再玩一次") { onRestart() }
                    .buttonStyle(.borderedProminent)
            }
        }
        .padding()
        .background(Color.yellow.opacity(0.1))
        .cornerRadius(20)
    }
}
```

### 5. ViewModel 层 (ViewModels/)

```swift
// HomeViewModel.swift
import SwiftUI

class HomeViewModel: ObservableObject {
    @Published var userProgress = UserProgress()
    @Published var chapters: [LessonChapter] = []

    init() {
        loadData()
    }

    private func loadData() {
        // 构建章节数据
        chapters = [
            LessonChapter(
                id: 1,
                title: "声母家族",
                description: "认识21个声母小伙伴",
                iconEmoji: "🅱️",
                pinyinItems: PinyinData.shengMuList,
                isUnlocked: true
            ),
            // ... 其他章节
        ]
        userProgress = UserProgress(totalStars: 12, streakDays: 3)
    }
}

// LearnViewModel.swift
class LearnViewModel: ObservableObject {
    @Published var currentItem: PinyinItem?
    @Published var currentIndex: Int = 0
    @Published var totalItems: Int = 0
    @Published var isPlayingAudio: Bool = false
    @Published var showCompletion: Bool = false
    @Published var starsEarned: Int = 0

    var progressValue: Double {
        totalItems > 0 ? Double(currentIndex + 1) / Double(totalItems) : 0
    }

    var canGoPrev: Bool { currentIndex > 0 }
    var canGoNext: Bool { !showCompletion }

    private let items: [PinyinItem]

    init(chapter: LessonChapter) {
        items = chapter.pinyinItems
        totalItems = items.count
        if let first = items.first {
            currentItem = first
        }
    }

    func playAudio() {
        isPlayingAudio = true
        // 播放音频逻辑
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            self.isPlayingAudio = false
        }
    }

    func nextItem() {
        if currentIndex < items.count - 1 {
            currentIndex += 1
            currentItem = items[currentIndex]
        } else {
            showCompletion = true
            starsEarned = 3
        }
    }

    func prevItem() {
        if currentIndex > 0 {
            currentIndex -= 1
            currentItem = items[currentIndex]
        }
    }
}

// MatchGameViewModel.swift
class MatchGameViewModel: ObservableObject {
    @Published var leftItems: [PinyinItem] = []
    @Published var rightItems: [PinyinItem] = []
    @Published var selectedLeft: String?
    @Published var selectedRight: String?
    @Published var matchedPairs: Set<String> = []
    @Published var score: Int = 0
    @Published var timeLeft: Int = 60
    @Published var isGameOver: Bool = false
    @Published var result: GameResult?

    private var timer: Timer?

    init() {
        startGame()
    }

    private func startGame() {
        let shengMu = PinyinData.shengMuList.shuffled().prefix(5)
        leftItems = Array(shengMu)
        rightItems = shengMu.shuffled()
        startTimer()
    }

    private func startTimer() {
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            if self.timeLeft > 0 && !self.isGameOver {
                self.timeLeft -= 1
            } else {
                self.endGame()
            }
        }
    }

    func selectLeft(_ id: String) {
        selectedLeft = id
        checkMatch()
    }

    func selectRight(_ id: String) {
        selectedRight = id
        checkMatch()
    }

    private func checkMatch() {
        guard let left = selectedLeft, let right = selectedRight else { return }
        if left == right {
            matchedPairs.insert(left)
            score += 10
            if matchedPairs.count == leftItems.count {
                endGame()
            }
        }
        selectedLeft = nil
        selectedRight = nil
    }

    private func endGame() {
        timer?.invalidate()
        isGameOver = true
        let maxScore = leftItems.count * 10
        let stars = score >= maxScore ? 3 : (score >= maxScore * 0.6 ? 2 : 1)
        result = GameResult(gameType: .matching, score: score, maxScore: maxScore, starsEarned: stars)
    }

    func restartGame() {
        matchedPairs = []
        score = 0
        timeLeft = 60
        isGameOver = false
        result = nil
        startGame()
    }
}
```

---

## 🎨 设计规范

### 配色方案（与 Android 保持一致）

| 用途 | 颜色名称 | 色值 |
|------|---------|------|
| 主色 | PandaOrange | #FF9800 |
| 次色 | PandaBlue | #2196F3 |
| 强调色 | PandaGreen | #4CAF50 |
| 背景色 | WarmYellow | #FFF8E1 |
| 成功色 | Success | #4CAF50 |
| 警告色 | Warning | #FF9800 |
| 错误色 | Error | #F44336 |

### 字体规范

- **标题**: SF Pro Rounded Bold, 24-32pt
- **正文**: SF Pro Rounded, 16-18pt
- **拼音字符**: SF Pro Rounded, 80pt Bold（学习卡片）
- **标签**: SF Pro Rounded Medium, 14pt

### 触控规范

- 最小触控区域: 48×48 pt（儿童App建议 56×56 pt）
- 按钮高度: 48pt
- 间距: 8pt 的倍数

---

## 📱 平台特性

### iOS 特有功能

1. **Haptic 反馈**: 答对/答错时的震动反馈
   ```swift
   let generator = UINotificationFeedbackGenerator()
   generator.notificationOccurred(.success)
   ```

2. **语音识别 (Speech Framework)**:
   ```swift
   import Speech
   // 用于"跟我读"游戏
   ```

3. **家长控制 (Screen Time API)**:
   - 学习时间限制
   - 内容分级

4. **Widget 支持**:
   - 今日学习进度 Widget
   - 宠物状态 Widget

---

## 🚀 开发时间估算

| 模块 | Android | iOS |
|------|---------|-----|
| 基础框架 | 3 天 | 3 天 |
| 学习模块 | 5 天 | 5 天 |
| 游戏模块 (6款) | 8 天 | 8 天 |
| 奖励系统 | 3 天 | 3 天 |
| 音频资源 | 5 天 | 5 天 |
| 测试调优 | 4 天 | 4 天 |
| **总计** | **28 天** | **28 天** |

---

## 📦 依赖管理

推荐使用 Swift Package Manager：

```swift
// Package.swift
dependencies: [
    .package(url: "https://github.com/airbnb/lottie-ios", from: "4.3.0"),
    .package(url: "https://github.com/SnapKit/SnapKit", from: "5.6.0")
]
```

| 库 | 用途 | 版本 |
|----|------|-----|
| Lottie | 动画播放 | 4.3+ |
| SnapKit | 布局约束 | 5.6+ |
| ConfettiSwiftUI | 庆祝特效 | 1.0+ |