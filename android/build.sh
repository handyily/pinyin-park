#!/usr/bin/env bash
#
# 拼音乐园 Android 构建脚本 (Linux/macOS)
#

set -e

echo ""
echo "================================================"
echo "   🐼 拼音乐园 Android 快速构建脚本"
echo "================================================"
echo ""

# 检查 Java 环境
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到 Java 环境！"
    echo ""
    echo "请先安装 JDK 17:"
    echo "  macOS: brew install --cask temurin17"
    echo "  Linux: sudo apt install openjdk-17-jdk"
    echo ""
    exit 1
fi

# 检查 Android SDK
if [ -z "$ANDROID_HOME" ]; then
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
    elif [ -d "/usr/local/android-sdk" ]; then
        export ANDROID_HOME="/usr/local/android-sdk"
    fi
fi

if [ -z "$ANDROID_HOME" ]; then
    echo "[警告] 未检测到 Android SDK"
    echo "请设置 ANDROID_HOME 环境变量"
    echo ""
fi

# 进入脚本目录
cd "$(dirname "$0")"

echo "[1/3] 检查 Gradle Wrapper..."
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "        下载 Gradle Wrapper..."
    mkdir -p gradle/wrapper
    curl -L -o gradle/wrapper/gradle-wrapper.jar \
        "https://raw.githubusercontent.com/nicerobot/nicerobot.github.io/main/gradle/wrapper/gradle-wrapper.jar" 2>/dev/null || \
    curl -L -o gradle/wrapper/gradle-wrapper.jar \
        "https://github.com/nicerobot/nicerobot.github.io/raw/main/gradle/wrapper/gradle-wrapper.jar" 2>/dev/null
fi

if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "[错误] Gradle Wrapper 下载失败"
    echo "请手动下载 gradle-wrapper.jar 或使用 Android Studio"
    exit 1
fi

echo "[2/3] 清理旧构建..."
rm -rf app/build

echo "[3/3] 开始构建 Debug APK..."
echo ""
echo "================================================"
echo ""

# 执行 Gradle 构建
chmod +x gradlew
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo ""
    echo "[错误] 构建失败！"
    exit 1
fi

echo ""
echo "================================================"
echo ""
echo "   ✅ 构建成功！"
echo ""

# 显示 APK 位置
APK=$(find app/build/outputs/apk/debug -name "*.apk" 2>/dev/null | head -1)
if [ -n "$APK" ]; then
    SIZE=$(du -h "$APK" | cut -f1)
    echo "   APK 位置: $APK"
    echo "   文件大小: $SIZE"
fi

echo ""
echo "   下一步: 将 APK 传输到手机安装"
echo ""
echo "================================================"
echo ""
