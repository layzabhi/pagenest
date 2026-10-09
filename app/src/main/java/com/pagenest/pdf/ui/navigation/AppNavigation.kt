package com.pagenest.pdf.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pagenest.pdf.PageNestApp
import com.pagenest.pdf.ui.components.PageNestBottomBar
import com.pagenest.pdf.ui.screens.history.HistoryScreen
import com.pagenest.pdf.ui.screens.history.HistoryViewModel
import com.pagenest.pdf.ui.screens.library.LibraryScreen
import com.pagenest.pdf.ui.screens.library.LibraryViewModel
import com.pagenest.pdf.ui.screens.reader.ReaderScreen
import com.pagenest.pdf.ui.screens.reader.ReaderViewModel
import com.pagenest.pdf.ui.screens.settings.SettingsScreen
import com.pagenest.pdf.ui.screens.settings.SettingsViewModel
import com.pagenest.pdf.ui.screens.themes.ThemeEditorScreen
import com.pagenest.pdf.ui.screens.themes.ThemesLibraryScreen
import com.pagenest.pdf.ui.screens.themes.ThemesViewModel

@Composable
fun AppNavigation(
    app: PageNestApp
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ViewModels instantiated using app container dependencies
    val libraryViewModel = remember {
        LibraryViewModel(
            app.documentRepository,
            app.readingProgressRepository,
            app.settingsRepository,
            app.folderAccessManager,
            app.pdfViewerManager
        )
    }

    val historyViewModel = remember {
        HistoryViewModel(app.readingProgressRepository)
    }

    val themesViewModel = remember {
        ThemesViewModel(app.themeRepository, app.settingsRepository)
    }

    val settingsViewModel = remember {
        SettingsViewModel(app.settingsRepository)
    }

    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute in listOf(
        NavScreen.Library.route,
        NavScreen.History.route,
        NavScreen.Themes.route,
        NavScreen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PageNestBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(NavScreen.Library.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NavHost(
                navController = navController,
                startDestination = NavScreen.Library.route
            ) {
                composable(NavScreen.Library.route) {
                    LibraryScreen(
                        viewModel = libraryViewModel,
                        onOpenDocument = { doc, page ->
                            val route = NavScreen.Reader.createRoute(doc.uriString, doc.displayName, page)
                            navController.navigate(route)
                        }
                    )
                }

                composable(NavScreen.History.route) {
                    HistoryScreen(
                        viewModel = historyViewModel,
                        onResumeReading = { progress ->
                            val route = NavScreen.Reader.createRoute(
                                progress.documentUri,
                                progress.displayName,
                                progress.lastViewedPage
                            )
                            navController.navigate(route)
                        }
                    )
                }

                composable(NavScreen.Themes.route) {
                    ThemesLibraryScreen(
                        viewModel = themesViewModel,
                        onCreateTheme = {
                            navController.navigate(NavScreen.ThemeEditor.createRoute())
                        },
                        onEditTheme = { themeId ->
                            navController.navigate(NavScreen.ThemeEditor.createRoute(themeId))
                        }
                    )
                }

                composable(
                    route = NavScreen.ThemeEditor.route,
                    arguments = listOf(
                        navArgument("themeId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val themeId = backStackEntry.arguments?.getString("themeId")
                    val themesUiState by themesViewModel.uiState.collectAsState()
                    val themeToEdit = themesUiState.themes.find { it.id == themeId }

                    ThemeEditorScreen(
                        initialTheme = themeToEdit,
                        onSaveTheme = { newTheme ->
                            themesViewModel.saveTheme(newTheme)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(NavScreen.Settings.route) {
                    SettingsScreen(viewModel = settingsViewModel)
                }

                composable(
                    route = NavScreen.Reader.route,
                    arguments = listOf(
                        navArgument("docUri") { type = NavType.StringType },
                        navArgument("docName") { type = NavType.StringType },
                        navArgument("initialPage") {
                            type = NavType.IntType
                            defaultValue = 1
                        }
                    )
                ) { backStackEntry ->
                    val rawUri = backStackEntry.arguments?.getString("docUri") ?: ""
                    val rawName = backStackEntry.arguments?.getString("docName") ?: ""
                    val page = backStackEntry.arguments?.getInt("initialPage") ?: 1

                    val docUri = Uri.decode(rawUri)
                    val docName = Uri.decode(rawName)

                    val readerViewModel = remember(docUri) {
                        ReaderViewModel(
                            app.pdfViewerManager,
                            app.readingProgressRepository,
                            app.bookmarkRepository
                        )
                    }

                    ReaderScreen(
                        documentUri = docUri,
                        documentName = docName,
                        initialPage = page,
                        viewModel = readerViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
