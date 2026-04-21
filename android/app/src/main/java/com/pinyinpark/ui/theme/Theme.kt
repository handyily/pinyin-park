package com.pinyinpark.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================
// 拼音乐园主题配置 - 儿童友好配色
// ============================================================

// 主色调
val PandaPrimary = Color(0xFFFF9800)        // 橙色主色
val PandaSecondary = Color(0xFF2196F3)      // 蓝色
val PandaTertiary = Color(0xFF4CAF50)       // 绿色
val PandaBackground = Color(0xFFFFF8E1)     // 暖黄背景

// 功能色
val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)
val ErrorRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)

// 游戏区域色
val GamePink = Color(0xFFFCE4EC)
val GamePurple = Color(0xFFE1BEE7)
val GameBlue = Color(0xFFE3F2FD)
val GameGreen = Color(0xFFF1F8E9)

private val LightColorScheme = lightColorScheme(
    primary = PandaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFF5D4037),

    secondary = PandaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBBDEFB),
    onSecondaryContainer = Color(0xFF0D47A1),

    tertiary = PandaTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),

    background = PandaBackground,
    onBackground = Color(0xFF3E2723),

    surface = Color.White,
    onSurface = Color(0xFF3E2723),
    surfaceVariant = Color(0xFFFFECB3),
    onSurfaceVariant = Color(0xFF5D4037),

    error = ErrorRed,
    onError = Color.White
)

@Composable
fun PinyinParkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // 儿童App默认浅色主题

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PandaPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// ============================================================
// 儿童友好字体规范
// ============================================================

val Typography = Typography(
    displayLarge = Typography().displayLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    displayMedium = Typography().displayMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    displaySmall = Typography().displaySmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold
    ),
    titleLarge = Typography().titleLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = Typography().titleSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = Typography().bodyLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
    ),
    bodySmall = Typography().bodySmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
    ),
    labelLarge = Typography().labelLarge.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = Typography().labelMedium.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = Typography().labelSmall.copy(
        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
        fontWeight = FontWeight.Medium
    )
)