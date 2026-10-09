package com.pagenest.pdf.ui.screens.themes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagenest.pdf.data.repository.SettingsRepository
import com.pagenest.pdf.data.repository.ThemeRepository
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemesUiState(
    val themes: List<AppTheme> = emptyList(),
    val activeThemeId: String = "builtin_light",
    val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
    val isLoading: Boolean = false
)

class ThemesViewModel(
    private val themeRepository: ThemeRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<ThemesUiState> = combine(
        themeRepository.getAllThemes(),
        settingsRepository.activeThemeId,
        settingsRepository.appearanceMode
    ) { allThemes, activeId, appMode ->
        ThemesUiState(
            themes = allThemes,
            activeThemeId = activeId,
            appearanceMode = appMode,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemesUiState(isLoading = true)
    )

    fun setActiveTheme(themeId: String) {
        viewModelScope.launch {
            settingsRepository.setActiveThemeId(themeId)
        }
    }

    fun setAppearanceMode(mode: AppearanceMode) {
        viewModelScope.launch {
            settingsRepository.setAppearanceMode(mode)
        }
    }

    fun duplicateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val copy = themeRepository.duplicateTheme(theme, "${theme.name} (Copy)")
            settingsRepository.setActiveThemeId(copy.id)
        }
    }

    fun deleteTheme(themeId: String) {
        viewModelScope.launch {
            themeRepository.deleteCustomTheme(themeId)
            if (uiState.value.activeThemeId == themeId) {
                settingsRepository.setActiveThemeId("builtin_light")
            }
        }
    }

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.saveCustomTheme(theme)
            settingsRepository.setActiveThemeId(theme.id)
        }
    }
}
