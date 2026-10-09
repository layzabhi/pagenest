package com.pagenest.pdf.data.repository

import com.pagenest.pdf.data.preferences.UserPreferencesDataStore
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.ScanMode
import com.pagenest.pdf.domain.model.SortOption
import com.pagenest.pdf.domain.model.ViewMode
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val preferencesDataStore: UserPreferencesDataStore) {

    val selectedFolderUri: Flow<String?> = preferencesDataStore.selectedFolderUri
    val selectedFolderName: Flow<String?> = preferencesDataStore.selectedFolderName
    val activeThemeId: Flow<String> = preferencesDataStore.activeThemeId
    val appearanceMode: Flow<AppearanceMode> = preferencesDataStore.appearanceMode
    val viewMode: Flow<ViewMode> = preferencesDataStore.viewMode
    val scanMode: Flow<ScanMode> = preferencesDataStore.scanMode
    val sortOption: Flow<SortOption> = preferencesDataStore.sortOption
    val fullScreenReader: Flow<Boolean> = preferencesDataStore.fullScreenReader

    suspend fun setSelectedFolder(uri: String, name: String) =
        preferencesDataStore.setSelectedFolder(uri, name)

    suspend fun clearSelectedFolder() =
        preferencesDataStore.clearSelectedFolder()

    suspend fun setActiveThemeId(themeId: String) =
        preferencesDataStore.setActiveThemeId(themeId)

    suspend fun setAppearanceMode(mode: AppearanceMode) =
        preferencesDataStore.setAppearanceMode(mode)

    suspend fun setViewMode(mode: ViewMode) =
        preferencesDataStore.setViewMode(mode)

    suspend fun setScanMode(mode: ScanMode) =
        preferencesDataStore.setScanMode(mode)

    suspend fun setSortOption(option: SortOption) =
        preferencesDataStore.setSortOption(option)

    suspend fun setFullScreenReader(enabled: Boolean) =
        preferencesDataStore.setFullScreenReader(enabled)
}
