package com.pagenest.pdf.data.repository

import com.pagenest.pdf.data.local.dao.CustomThemeDao
import com.pagenest.pdf.data.local.entity.CustomThemeEntity
import com.pagenest.pdf.domain.model.AppTheme
import com.pagenest.pdf.ui.theme.ThemePresets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ThemeRepository(private val customThemeDao: CustomThemeDao) {

    fun getAllThemes(): Flow<List<AppTheme>> {
        return customThemeDao.getAllCustomThemes().map { customList ->
            ThemePresets.allPresets + customList.map { it.toDomain() }
        }
    }

    suspend fun getThemeById(id: String): AppTheme {
        val preset = ThemePresets.allPresets.find { it.id == id }
        if (preset != null) return preset

        val custom = customThemeDao.getThemeById(id)
        return custom?.toDomain() ?: ThemePresets.Light
    }

    suspend fun saveCustomTheme(theme: AppTheme) {
        val entity = CustomThemeEntity.fromDomain(theme)
        customThemeDao.insertTheme(entity)
    }

    suspend fun duplicateTheme(sourceTheme: AppTheme, newName: String): AppTheme {
        val newTheme = sourceTheme.copy(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            name = newName,
            isBuiltIn = false
        )
        saveCustomTheme(newTheme)
        return newTheme
    }

    suspend fun deleteCustomTheme(id: String) {
        // Built-in presets are protected from deletion
        if (id.startsWith("builtin_")) return
        customThemeDao.deleteTheme(id)
    }
}
