@echo off
chcp 65001 >nul
echo.
echo ================================================
echo    🐼 拼音乐园 - 推送到 GitHub
echo ================================================
echo.

cd /d "%~dp0"

:: 检查 Git 状态
git status >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 不是 Git 仓库！
    pause
    exit /b 1
)

:: 检查远程仓库
git remote -v | findstr origin >nul
if %errorlevel% neq 0 (
    echo.
    echo [提示] 未设置远程仓库
    echo.
    echo 请选择以下方式之一：
    echo.
    echo 方式1: 在 GitHub 创建新仓库后，输入仓库 URL
    echo 方式2: 使用已有仓库
    echo.
    set /p REPO_URL="请输入 GitHub 仓库 URL (例如: https://github.com/handyily/pinyin-park.git): "
    
    if not defined REPO_URL (
        echo [错误] 未输入仓库 URL
        pause
        exit /b 1
    )
    
    git remote add origin %REPO_URL%
)

:: 推送到 GitHub
echo.
echo [1/1] 推送到 GitHub...
echo.

git push -u origin master --force

if %errorlevel% neq 0 (
    echo.
    echo [错误] 推送失败！
    echo.
    echo 可能原因:
    echo   1. GitHub 认证失败 - 请使用 Personal Access Token
    echo   2. 仓库不存在 - 请先在 GitHub 创建仓库
    echo.
    echo 解决方法:
    echo   1. 创建 GitHub Token: https://github.com/settings/tokens
    echo   2. 运行: git remote set-url origin https://你的TOKEN@github.com/handyily/pinyin-park.git
    echo.
    pause
    exit /b 1
)

echo.
echo ================================================
echo.
echo    ✅ 推送成功！
echo.
echo    下一步:
echo    1. 打开 GitHub 仓库页面
echo    2. 进入 Actions 页面查看构建状态
echo    3. 构建完成后，在 Artifacts 下载 APK
echo.
echo    APK 下载路径:
echo    Actions -> 构建任务 -> app-debug -> pinyin-park-apk
echo.
echo ================================================
echo.

pause
