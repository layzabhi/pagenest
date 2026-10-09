package com.pagenest.pdf.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pagenest.pdf.domain.model.AppTheme

@Entity(tableName = "custom_themes")
data class CustomThemeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isDark: Boolean,
    val backgroundColor: Long,
    val surfaceColor: Long,
    val primaryColor: Long,
    val secondaryColor: Long,
    val primaryTextColor: Long,
    val secondaryTextColor: Long,
    val toolbarColor: Long,
    val navigationColor: Long,
    val borderColor: Long,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): AppTheme = AppTheme(
        id = id,
        name = name,
        isBuiltIn = false,
        isDark = isDark,
        backgroundColor = backgroundColor,
        surfaceColor = surfaceColor,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        primaryTextColor = primaryTextColor,
        secondaryTextColor = secondaryTextColor,
        toolbarColor = toolbarColor,
        navigationColor = navigationColor,
        borderColor = borderColor
    )

    companion object {
        fun fromDomain(theme: AppTheme): CustomThemeEntity = CustomThemeEntity(
            id = theme.id,
            name = theme.name,
            isDark = theme.isDark,
            backgroundColor = theme.backgroundColor,
            surfaceColor = theme.surfaceColor,
            primaryColor = theme.primaryColor,
            secondaryColor = theme.secondaryColor,
            primaryTextColor = theme.primaryTextColor,
            secondaryTextColor = theme.secondaryTextColor,
            toolbarColor = theme.toolbarColor,
            navigationColor = theme.navigationColor,
            borderColor = theme.borderColor
        )
    }
}
