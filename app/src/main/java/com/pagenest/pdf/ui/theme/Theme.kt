package com.pagenest.pdf.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.AppTheme

@Composable
fun PageNestTheme(
    activeTheme: AppTheme = ThemePresets.Light,
    appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (appearanceMode) {
        AppearanceMode.SYSTEM -> systemDark || activeTheme.isDark
        AppearanceMode.LIGHT -> false
        AppearanceMode.DARK -> true
    }

    val effectiveTheme = if (appearanceMode == AppearanceMode.LIGHT && activeTheme.isDark) {
        ThemePresets.Light
    } else if (appearanceMode == AppearanceMode.DARK && !activeTheme.isDark) {
        ThemePresets.Dark
    } else {
        activeTheme
    }

    val colorScheme = effectiveTheme.toColorScheme()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
