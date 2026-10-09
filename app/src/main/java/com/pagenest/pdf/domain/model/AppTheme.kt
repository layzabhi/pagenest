package com.pagenest.pdf.domain.model

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppTheme(
    val id: String,
    val name: String,
    val isBuiltIn: Boolean = false,
    val isDark: Boolean = false,
    val backgroundColor: Long,
    val surfaceColor: Long,
    val primaryColor: Long,
    val secondaryColor: Long,
    val primaryTextColor: Long,
    val secondaryTextColor: Long,
    val toolbarColor: Long,
    val navigationColor: Long,
    val borderColor: Long
) {
    fun toColorScheme(): ColorScheme {
        val bg = Color(backgroundColor)
        val surf = Color(surfaceColor)
        val prim = Color(primaryColor)
        val sec = Color(secondaryColor)
        val onSurf = Color(primaryTextColor)
        val onSurfVar = Color(secondaryTextColor)
        val outline = Color(borderColor)

        return if (isDark) {
            darkColorScheme(
                primary = prim,
                onPrimary = if (isLightColor(prim)) Color.Black else Color.White,
                primaryContainer = Color(toolbarColor),
                onPrimaryContainer = onSurf,
                secondary = sec,
                onSecondary = Color.White,
                background = bg,
                onBackground = onSurf,
                surface = surf,
                onSurface = onSurf,
                surfaceVariant = Color(navigationColor),
                onSurfaceVariant = onSurfVar,
                outline = outline,
                outlineVariant = outline.copy(alpha = 0.5f)
            )
        } else {
            lightColorScheme(
                primary = prim,
                onPrimary = if (isLightColor(prim)) Color.Black else Color.White,
                primaryContainer = Color(toolbarColor),
                onPrimaryContainer = onSurf,
                secondary = sec,
                onSecondary = Color.White,
                background = bg,
                onBackground = onSurf,
                surface = surf,
                onSurface = onSurf,
                surfaceVariant = Color(navigationColor),
                onSurfaceVariant = onSurfVar,
                outline = outline,
                outlineVariant = outline.copy(alpha = 0.5f)
            )
        }
    }

    private fun isLightColor(color: Color): Boolean {
        val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
        return luminance > 0.6
    }
}
