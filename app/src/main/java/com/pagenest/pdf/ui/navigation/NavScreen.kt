package com.pagenest.pdf.ui.navigation

import android.net.Uri

sealed class NavScreen(val route: String) {
    data object Library : NavScreen("library")
    data object History : NavScreen("history")
    data object Themes : NavScreen("themes")
    data object Settings : NavScreen("settings")

    data object ThemeEditor : NavScreen("theme_editor?themeId={themeId}") {
        fun createRoute(themeId: String? = null): String {
            return if (themeId != null) "theme_editor?themeId=$themeId" else "theme_editor"
        }
    }

    data object Reader : NavScreen("reader?docUri={docUri}&docName={docName}&initialPage={initialPage}") {
        fun createRoute(docUri: String, docName: String, initialPage: Int = 1): String {
            val encodedUri = Uri.encode(docUri)
            val encodedName = Uri.encode(docName)
            return "reader?docUri=$encodedUri&docName=$encodedName&initialPage=$initialPage"
        }
    }
}
