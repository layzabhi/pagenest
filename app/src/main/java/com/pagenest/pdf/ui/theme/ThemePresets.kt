package com.pagenest.pdf.ui.theme

import androidx.compose.ui.graphics.toArgb
import com.pagenest.pdf.domain.model.AppTheme

object ThemePresets {
    val Light = AppTheme(
        id = "builtin_light",
        name = "Light",
        isBuiltIn = true,
        isDark = false,
        backgroundColor = BackgroundLight.toArgb().toLong(),
        surfaceColor = SurfaceLight.toArgb().toLong(),
        primaryColor = IndigoPrimary.toArgb().toLong(),
        secondaryColor = IndigoPrimaryDark.toArgb().toLong(),
        primaryTextColor = TextPrimaryLight.toArgb().toLong(),
        secondaryTextColor = TextSecondaryLight.toArgb().toLong(),
        toolbarColor = SurfaceLight.toArgb().toLong(),
        navigationColor = SurfaceContainerLowLight.toArgb().toLong(),
        borderColor = OutlineLight.toArgb().toLong()
    )

    val Dark = AppTheme(
        id = "builtin_dark",
        name = "Dark",
        isBuiltIn = true,
        isDark = true,
        backgroundColor = DarkCanvas.toArgb().toLong(),
        surfaceColor = DarkSurface.toArgb().toLong(),
        primaryColor = DarkPrimaryAccent.toArgb().toLong(),
        secondaryColor = DarkPrimaryContainer.toArgb().toLong(),
        primaryTextColor = DarkPrimaryText.toArgb().toLong(),
        secondaryTextColor = DarkSecondaryText.toArgb().toLong(),
        toolbarColor = DarkSurface.toArgb().toLong(),
        navigationColor = DarkSurfaceContainer.toArgb().toLong(),
        borderColor = DarkDivider.toArgb().toLong()
    )

    val Midnight = AppTheme(
        id = "builtin_midnight",
        name = "Midnight",
        isBuiltIn = true,
        isDark = true,
        backgroundColor = MidnightBackground.toArgb().toLong(),
        surfaceColor = MidnightSurface.toArgb().toLong(),
        primaryColor = MidnightPrimary.toArgb().toLong(),
        secondaryColor = 0xFF9BAAC7,
        primaryTextColor = MidnightText.toArgb().toLong(),
        secondaryTextColor = MidnightSecondaryText.toArgb().toLong(),
        toolbarColor = MidnightBackground.toArgb().toLong(),
        navigationColor = 0xFF151B26,
        borderColor = 0xFF30394A
    )

    val Amoled = AppTheme(
        id = "builtin_amoled",
        name = "AMOLED Black",
        isBuiltIn = true,
        isDark = true,
        backgroundColor = AmoledBackground.toArgb().toLong(),
        surfaceColor = AmoledSurface.toArgb().toLong(),
        primaryColor = AmoledPrimary.toArgb().toLong(),
        secondaryColor = 0xFF455A64,
        primaryTextColor = AmoledText.toArgb().toLong(),
        secondaryTextColor = AmoledSecondaryText.toArgb().toLong(),
        toolbarColor = AmoledBackground.toArgb().toLong(),
        navigationColor = AmoledSurface.toArgb().toLong(),
        borderColor = 0xFF212121
    )

    val Forest = AppTheme(
        id = "builtin_forest",
        name = "Forest",
        isBuiltIn = true,
        isDark = true,
        backgroundColor = ForestBackground.toArgb().toLong(),
        surfaceColor = ForestSurface.toArgb().toLong(),
        primaryColor = ForestPrimary.toArgb().toLong(),
        secondaryColor = 0xFF2D3748,
        primaryTextColor = ForestText.toArgb().toLong(),
        secondaryTextColor = ForestSecondaryText.toArgb().toLong(),
        toolbarColor = ForestBackground.toArgb().toLong(),
        navigationColor = 0xFF18231C,
        borderColor = 0xFF24362B
    )

    val Ocean = AppTheme(
        id = "builtin_ocean",
        name = "Ocean",
        isBuiltIn = true,
        isDark = true,
        backgroundColor = OceanBackground.toArgb().toLong(),
        surfaceColor = OceanSurface.toArgb().toLong(),
        primaryColor = OceanPrimary.toArgb().toLong(),
        secondaryColor = 0xFF233B50,
        primaryTextColor = OceanText.toArgb().toLong(),
        secondaryTextColor = OceanSecondaryText.toArgb().toLong(),
        toolbarColor = OceanBackground.toArgb().toLong(),
        navigationColor = 0xFF13222F,
        borderColor = 0xFF21374A
    )

    val Paper = AppTheme(
        id = "builtin_paper",
        name = "Paper",
        isBuiltIn = true,
        isDark = false,
        backgroundColor = PaperBackground.toArgb().toLong(),
        surfaceColor = PaperSurface.toArgb().toLong(),
        primaryColor = PaperPrimary.toArgb().toLong(),
        secondaryColor = 0xFFBCAAA4,
        primaryTextColor = PaperText.toArgb().toLong(),
        secondaryTextColor = PaperSecondaryText.toArgb().toLong(),
        toolbarColor = PaperSurface.toArgb().toLong(),
        navigationColor = 0xFFEFE8E2,
        borderColor = 0xFFD7CCC8
    )

    val allPresets = listOf(Light, Dark, Midnight, Amoled, Forest, Ocean, Paper)

    fun getById(id: String): AppTheme {
        return allPresets.find { it.id == id } ?: Light
    }
}
