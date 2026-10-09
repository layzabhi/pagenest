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

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.scanMode,
        settingsRepository.viewMode,
        settingsRepository.sortOption,
        settingsRepository.appearanceMode,
        settingsRepository.fullScreenReader,
        settingsRepository.selectedFolderName
    ) { scan, view, sort, appearance, fullScreen, folderName ->
        SettingsUiState(
            scanMode = scan,
            viewMode = view,
            sortOption = sort,
            appearanceMode = appearance,
            fullScreenReader = fullScreen,
            selectedFolderName = folderName
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
