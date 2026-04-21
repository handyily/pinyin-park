@echo off
chcp 65001 >nul
echo.
echo ================================================
echo    🐼 拼音乐园 Android 快速构建脚本
echo ================================================
echo.

:: 检查 Java 环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Java 环境！
    echo.
    echo 请先安装 JDK 17:
    echo   1. 下载地址: https://adoptium.net/temurin/releases/?version=17
    echo   2. 安装后重新运行此脚本
    echo.
    pause
    exit /b 1
)

:: 检查 Android SDK 环境
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
    ) else if exist "C:\Android\SDK" (
        set ANDROID_HOME=C:\Android\SDK
    )
)

if not defined ANDROID_HOME (
    echo [警告] 未检测到 Android SDK
    echo   请设置 ANDROID_HOME 环境变量
    echo.
)

:: 进入脚本目录
cd /d "%~dp0"

echo [1/3] 检查 Gradle Wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo        下载 Gradle Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/nicerobot/nicerobot.github.io/main/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'" 2>nul
    if not exist "gradle\wrapper\gradle-wrapper.jar" (
        powershell -Command "Start-BitsTransfer -Source 'https://github.com/nicerobot/nicerobot.github.io/raw/main/gradle/wrapper/gradle-wrapper.jar' -Destination 'gradle\wrapper\gradle-wrapper.jar'" 2>nul
    )
)

if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo [错误] Gradle Wrapper 下载失败
    echo 请手动下载 gradle-wrapper.jar 到 gradle\wrapper\ 目录
    echo 或使用 Android Studio 打开项目
    echo.
    pause
    exit /b 1
)

echo [2/3] 清理旧构建...
if exist "app\build" rd /s /q "app\build" 2>nul

echo [3/3] 开始构建 Debug APK...
echo.
echo ================================================
echo.

:: 执行 Gradle 构建
gradlew.bat assembleDebug

if %errorlevel% neq 0 (
    echo.
    echo [错误] 构建失败！
    echo 请检查上方错误信息
    echo.
    pause
    exit /b 1
)

echo.
echo ================================================
echo.
echo    ✅ 构建成功！
echo.

:: 显示 APK 位置
for /f "delims=" %%i in ('dir /s /b "app\build\outputs\apk\debug\*.apk" 2^>nul') do (
    echo    APK 位置: %%i
    echo    文件大小: %%~zi bytes ^(%%~zi / 1024 / 1024 MB^)
)

echo.
echo    下一步: 将 APK 传输到手机安装
echo.
echo ================================================
echo.

pause
