# 拼音乐园 - GitHub 自动构建指南

## 🚀 一键构建 APK（无需本地环境）

### 步骤 1: 创建 GitHub 仓库

1. 登录 GitHub: https://github.com
2. 点击右上角 `+` → `New repository`
3. 填写仓库信息:
   - **Repository name**: `pinyin-park`（或你喜欢的名字）
   - **Description**: 拼音乐园 - 趣味拼音学习 App
   - **Private/Public**: 选择 Public（公开）或 Private（私有）
4. 点击 `Create repository`

### 步骤 2: 推送代码

运行项目中的 `push-to-github.bat` 脚本，或手动执行:

```bash
cd c:\Users\Administrator\WorkBuddy\拼音
git remote add origin https://github.com/YOUR_USERNAME/pinyin-park.git
git push -u origin master
```

### 步骤 3: 查看构建状态

1. 打开你的 GitHub 仓库页面
2. 点击 `Actions` 标签页
3. 等待构建完成（约 3-5 分钟）
4. 构建完成后会显示 ✓ 绿色勾

### 步骤 4: 下载 APK

1. 点击构建任务（如 "Android CI/CD"）
2. 点击右侧的 `app-debug` 或 `pinyin-park-apk`
3. 点击 `Download` 下载 APK 文件
4. 将 APK 传输到手机安装

---

## 🔧 GitHub Actions 自动构建流程

```yaml
1. 检出代码 (actions/checkout@v4)
       ↓
2. 设置 JDK 17 (actions/setup-java@v4)
       ↓
3. 设置 Android SDK (android-actions/setup-android@v2)
       ↓
4. 执行 Gradle 构建 (./gradlew assembleDebug)
       ↓
5. 上传 APK 作为 Artifact
```

---

## 📱 安装 APK 到手机

APK 下载完成后：
1. 将 APK 文件传输到手机（通过微信、QQ、U盘等方式）
2. 在手机上打开文件管理器
3. 点击 APK 文件安装
4. 如果提示"禁止安装未知来源应用"：
   - 进入设置 → 安全 → 允许未知来源
   - 或在安装弹窗中点击"允许"

---

## ❓ 常见问题

### Q: 推送代码时认证失败？
**解决方案**：
1. 创建 Personal Access Token: https://github.com/settings/tokens
2. 设置凭据：
```bash
git remote set-url origin https://你的TOKEN@github.com/handyily/pinyin-park.git
```

### Q: Actions 构建失败？
**检查项**：
- 代码是否有语法错误
- Gradle 配置是否正确
- 可以点击失败的构建查看日志

### Q: 下载的 APK 无法安装？
**检查项**：
- 手机系统 >= Android 6.0
- 已开启"允许未知来源应用"
- APK 完整性是否正常

---

## 🎯 完成后

恭喜！你现在拥有：
- ✅ GitHub 仓库备份
- ✅ 自动构建系统
- ✅ 可下载的 APK 安装包
- ✅ 每次推送自动重新构建
