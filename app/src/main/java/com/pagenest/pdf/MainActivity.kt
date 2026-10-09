package com.pagenest.pdf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pagenest.pdf.domain.model.AppearanceMode
import com.pagenest.pdf.domain.model.AppTheme
import com.pagenest.pdf.ui.navigation.AppNavigation
import com.pagenest.pdf.ui.navigation.NavScreen
import com.pagenest.pdf.ui.theme.PageNestTheme
import com.pagenest.pdf.ui.theme.ThemePresets
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as PageNestApp

        setContent {
            val activeThemeId by app.settingsRepository.activeThemeId.collectAsState(initial = "builtin_light")
            val appearanceMode by app.settingsRepository.appearanceMode.collectAsState(initial = AppearanceMode.SYSTEM)

            var currentTheme by remember { mutableStateOf<AppTheme>(ThemePresets.Light) }

            LaunchedEffect(activeThemeId) {
                currentTheme = app.themeRepository.getThemeById(activeThemeId)
            }

            PageNestTheme(
                activeTheme = currentTheme,
                appearanceMode = appearanceMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigation(app = app)
                }
            }
        }

        handleIncomingIntent(intent, app)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val app = application as PageNestApp
        handleIncomingIntent(intent, app)
    }

    private fun handleIncomingIntent(intent: Intent?, app: PageNestApp) {
        if (intent == null || intent.action != Intent.ACTION_VIEW) return
        val dataUri: Uri = intent.data ?: return

        lifecycleScope.launch {
            try {
                var fileName = "Document.pdf"
                contentResolver.query(dataUri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex) ?: fileName
                        }
                    }
                }

                // Retrieve last saved page if already in history
                val progress = app.readingProgressRepository.getProgress(dataUri.toString())
                val startPage = progress?.lastViewedPage ?: 1

                // Launch Reader
                // Note: The AppNavigation handles navigation, and if launched from external app,
                // the user lands directly into reading mode.
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
