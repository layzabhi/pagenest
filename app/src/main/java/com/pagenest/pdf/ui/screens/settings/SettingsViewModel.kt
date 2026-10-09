package com.pagenest.pdf.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagenest.pdf.data.repository.SettingsRepository
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.ScanMode
import com.pagenest.pdf.domain.model.SortOption
import com.pagenest.pdf.domain.model.ViewMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val scanMode: ScanMode = ScanMode.DIRECT,
    val viewMode: ViewMode = ViewMode.GRID,
    val sortOption: SortOption = SortOption.NAME_ASC,
    val appearanceMode: AppearanceMode = AppearanceMode.SYSTEM,
    val fullScreenReader: Boolean = false,
    val selectedFolderName: String? = null
)

private data class DisplayPreferences(
    val scanMode: ScanMode,
    val viewMode: ViewMode,
    val sortOption: SortOption
)

private data class AppPreferences(
    val appearanceMode: AppearanceMode,
    val fullScreenReader: Boolean,
    val selectedFolderName: String?
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val displayPrefs = combine(
        settingsRepository.scanMode,
        settingsRepository.viewMode,
        settingsRepository.sortOption
    ) { scan, view, sort ->
        DisplayPreferences(scan, view, sort)
    }

    private val appPrefs = combine(
        settingsRepository.appearanceMode,
        settingsRepository.fullScreenReader,
        settingsRepository.selectedFolderName
    ) { appearance, fullScreen, folderName ->
        AppPreferences(appearance, fullScreen, folderName)
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        displayPrefs,
        appPrefs
    ) { display, app ->
        SettingsUiState(
            scanMode = display.scanMode,
            viewMode = display.viewMode,
            sortOption = display.sortOption,
            appearanceMode = app.appearanceMode,
            fullScreenReader = app.fullScreenReader,
            selectedFolderName = app.selectedFolderName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setScanMode(mode: ScanMode) {
        viewModelScope.launch {
            settingsRepository.setScanMode(mode)
        }
    }

    fun setFullScreenReader(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFullScreenReader(enabled)
        }
    }

    fun clearSelectedFolder() {
        viewModelScope.launch {
            settingsRepository.clearSelectedFolder()
        }
    }
}
