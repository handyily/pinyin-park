# 拼音乐园 - Android 安装指南

## 📦 安装方式一：使用 Android Studio 编译（推荐）

### 环境要求
- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **JDK**: 17 (Android Studio 自带)
- **Android SDK**: API 23+ (Android 6.0)

### 步骤 1: 打开项目
1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择项目路径: `c:\Users\Administrator\WorkBuddy\拼音\android`
4. 等待 Gradle 同步完成

### 步骤 2: 配置签名（可选，用于发布）
```kotlin
// android/app/build.gradle.kts 中添加
android {
    signingConfigs {
        create("release") {
            storeFile = file("your-keystore.jks")
            storePassword = "your-password"
            keyAlias = "your-alias"
            keyPassword = "your-key-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
        }
    }
}
```

### 步骤 3: 构建 Debug APK
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### 步骤 4: 安装到设备
1. 通过 USB 连接 Android 手机
2. 开启手机 `开发者选项` 和 `USB 调试`
3. 点击 `Run 'app'` 或通过 ADB 安装:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 安装方式二：使用命令行构建

### 环境准备

#### Windows - 安装 JDK 17
```powershell
# 使用 winget 安装
winget install Oracle.JDK.17

# 或下载手动安装: https://adoptium.net/temurin/releases/?version=17
```

#### Windows - 安装 Android SDK
```powershell
# 下载 Android command line tools
# https://developer.android.com/studio#command-line-tools-only

# 设置环境变量
$env:ANDROID_HOME = "C:\Android\SDK"
$env:PATH += ";$ANDROID_HOME\cmdline-tools\latest\bin;$ANDROID_HOME\platform-tools"
```

### 构建命令
```powershell
# 进入 android 目录
cd c:\Users\Administrator\WorkBuddy\拼音\android

# 使用 gradlew (自动下载 Gradle)
.\gradlew assembleDebug

# APK 输出位置
# android\app\build\outputs\apk\debug\app-debug.apk
```

---

## ☁️ 安装方式三：在线云构建（无需本地环境）

### 1. GitHub Actions 自动构建
项目已配置 GitHub Actions，push 代码后自动生成 APK:

```yaml
# .github/workflows/android.yml
name: Android CI
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build APK
        run: ./gradlew assembleDebug
      - uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
```

### 2. 免费云构建平台
| 平台 | 说明 | 免费额度 |
|------|------|----------|
| [GitHub Actions](https://github.com/features/actions) | 自动构建+下载 | 2000分钟/月 |
| [Bitrise](https://bitrise.io) | 移动端CI/CD | 1个App免费 |
| [AppCenter](https://appcenter.ms) | 微软云构建 | 240分钟/平台 |

---

## 📲 安装 APK 到手机

### 方法 1: USB 直接安装
```bash
# 启用手机开发者模式
# 设置 → 关于手机 → 连续点击"版本号"7次

# 开启 USB 调试
# 设置 → 系统 → 开发者选项 → USB 调试

# 连接电脑，执行安装
adb install -r app-debug.apk
```

### 方法 2: 局域网无线安装
```bash
# 通过 WiFi 连接（手机和电脑在同一网络）
adb tcpip 5555
adb connect <手机IP>:5555
adb install -r app-debug.apk
```

### 方法 3: 传输到手机手动安装
1. 将 `app-debug.apk` 复制到手机存储
2. 打开手机文件管理器
3. 点击 APK 文件安装
4. 如果提示"禁止安装未知应用"，先开启权限

---

## 🔧 常见问题

### Q1: Gradle 同步失败
```
解决方案: File → Invalidate Caches → Restart
或者删除 .gradle 和 .idea 目录后重新导入
```

### Q2: SDK 找不到
```
解决方案: Android Studio → File → Project Structure → SDK Location
设置正确的 Android SDK 路径
```

### Q3: 手机无法安装
```
检查: 设置 → 安全 → 允许未知来源应用
确保手机系统 >= Android 6.0 (API 23)
```

### Q4: 中文乱码
```
解决方案: 在 gradle.properties 中添加
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

---

## 📁 APK 输出位置

| 构建类型 | 路径 |
|----------|------|
| Debug | `android/app/build/outputs/apk/debug/app-debug.apk` |
| Release | `android/app/build/outputs/apk/release/app-release.apk` |

---

## 🎯 下一步

1. ✅ 按照上述方式构建 APK
2. ✅ 将 APK 安装到 Android 手机/平板
3. ✅ 享受拼音乐园学习之旅！
