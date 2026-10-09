package com.pagenest.pdf

import android.app.Application
import com.pagenest.pdf.data.local.AppDatabase
import com.pagenest.pdf.data.preferences.UserPreferencesDataStore
import com.pagenest.pdf.data.repository.DocumentRepository
import com.pagenest.pdf.data.repository.ReadingProgressRepository
import com.pagenest.pdf.data.repository.SettingsRepository
import com.pagenest.pdf.data.repository.ThemeRepository
import com.pagenest.pdf.domain.manager.FolderAccessManager
import com.pagenest.pdf.domain.manager.PdfViewerManager

class PageNestApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var preferencesDataStore: UserPreferencesDataStore
        private set

    lateinit var documentRepository: DocumentRepository
        private set
    lateinit var readingProgressRepository: ReadingProgressRepository
        private set
    lateinit var themeRepository: ThemeRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var folderAccessManager: FolderAccessManager
        private set
    lateinit var pdfViewerManager: PdfViewerManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferencesDataStore = UserPreferencesDataStore(this)

        documentRepository = DocumentRepository(database.documentDao())
        readingProgressRepository = ReadingProgressRepository(database.readingProgressDao())
        themeRepository = ThemeRepository(database.customThemeDao())
        settingsRepository = SettingsRepository(preferencesDataStore)

        folderAccessManager = FolderAccessManager(this)
        pdfViewerManager = PdfViewerManager(this)
    }

    companion object {
        lateinit var instance: PageNestApp
            private set
    }
}
