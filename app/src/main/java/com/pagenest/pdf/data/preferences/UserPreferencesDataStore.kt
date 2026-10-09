package com.pagenest.pdf.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.ScanMode
import com.pagenest.pdf.domain.model.SortOption
import com.pagenest.pdf.domain.model.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pagenest_preferences")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val SELECTED_FOLDER_URI = stringPreferencesKey("selected_folder_uri")
        val SELECTED_FOLDER_NAME = stringPreferencesKey("selected_folder_name")
        val ACTIVE_THEME_ID = stringPreferencesKey("active_theme_id")
        val APPEARANCE_MODE = stringPreferencesKey("appearance_mode")
        val VIEW_MODE = stringPreferencesKey("view_mode")
        val SCAN_MODE = stringPreferencesKey("scan_mode")
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val FULL_SCREEN_READER = booleanPreferencesKey("full_screen_reader")
    }

    val selectedFolderUri: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_FOLDER_URI]
    }

    val selectedFolderName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_FOLDER_NAME]
    }

    val activeThemeId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_THEME_ID] ?: "builtin_light"
    }

    val appearanceMode: Flow<AppearanceMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.APPEARANCE_MODE] ?: AppearanceMode.SYSTEM.name
        try { AppearanceMode.valueOf(raw) } catch (e: Exception) { AppearanceMode.SYSTEM }
    }

    val viewMode: Flow<ViewMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.VIEW_MODE] ?: ViewMode.GRID.name
        try { ViewMode.valueOf(raw) } catch (e: Exception) { ViewMode.GRID }
    }

    val scanMode: Flow<ScanMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.SCAN_MODE] ?: ScanMode.DIRECT.name
        try { ScanMode.valueOf(raw) } catch (e: Exception) { ScanMode.DIRECT }
    }

    val sortOption: Flow<SortOption> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.SORT_OPTION] ?: SortOption.NAME_ASC.name
        try { SortOption.valueOf(raw) } catch (e: Exception) { SortOption.NAME_ASC }
    }

    val fullScreenReader: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.FULL_SCREEN_READER] ?: false
    }

    suspend fun setSelectedFolder(uri: String, name: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_FOLDER_URI] = uri
            prefs[Keys.SELECTED_FOLDER_NAME] = name
        }
    }

    suspend fun clearSelectedFolder() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.SELECTED_FOLDER_URI)
            prefs.remove(Keys.SELECTED_FOLDER_NAME)
        }
    }

    suspend fun setActiveThemeId(themeId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACTIVE_THEME_ID] = themeId
        }
    }

    suspend fun setAppearanceMode(mode: AppearanceMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APPEARANCE_MODE] = mode.name
        }
    }

    suspend fun setViewMode(mode: ViewMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VIEW_MODE] = mode.name
        }
    }

    suspend fun setScanMode(mode: ScanMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SCAN_MODE] = mode.name
        }
    }

    suspend fun setSortOption(option: SortOption) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SORT_OPTION] = option.name
        }
    }

    suspend fun setFullScreenReader(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FULL_SCREEN_READER] = enabled
        }
    }
}
