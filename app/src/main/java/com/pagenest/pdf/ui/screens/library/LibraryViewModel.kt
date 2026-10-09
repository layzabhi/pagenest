package com.pagenest.pdf.ui.screens.library

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagenest.pdf.data.repository.DocumentRepository
import com.pagenest.pdf.data.repository.ReadingProgressRepository
import com.pagenest.pdf.data.repository.SettingsRepository
import com.pagenest.pdf.domain.manager.FolderAccessManager
import com.pagenest.pdf.domain.manager.PdfViewerManager
import com.pagenest.pdf.domain.model.PdfDocument
import com.pagenest.pdf.domain.model.ReadingProgress
import com.pagenest.pdf.domain.model.ScanMode
import com.pagenest.pdf.domain.model.SortOption
import com.pagenest.pdf.domain.model.ViewMode
import com.pagenest.pdf.ui.components.DocumentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LibraryUiState(
    val documents: List<PdfDocument> = emptyList(),
    val progressMap: Map<String, ReadingProgress> = emptyMap(),
    val isLoading: Boolean = false,
    val selectedFolderUri: String? = null,
    val selectedFolderName: String? = null,
    val searchQuery: String = "",
    val activeFilter: DocumentFilter = DocumentFilter.ALL,
    val viewMode: ViewMode = ViewMode.GRID,
    val sortOption: SortOption = SortOption.NAME_ASC
)

class LibraryViewModel(
    private val documentRepository: DocumentRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val folderAccessManager: FolderAccessManager,
    private val pdfViewerManager: PdfViewerManager
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow(DocumentFilter.ALL)
    val activeFilter = _activeFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _thumbnails = MutableStateFlow<Map<String, Bitmap>>(emptyMap())
    val thumbnails = _thumbnails.asStateFlow()

    val uiState: StateFlow<LibraryUiState> = combine(
        documentRepository.getAllDocuments(),
        readingProgressRepository.getAllReadingProgress(),
        settingsRepository.selectedFolderUri,
        settingsRepository.selectedFolderName,
        _searchQuery,
        _activeFilter,
        settingsRepository.viewMode,
        settingsRepository.sortOption,
        _isLoading
    ) { rawDocs, progresses, folderUri, folderName, query, filter, viewMode, sortOption, loading ->
        val progressMap = progresses.associateBy { it.documentUri }

        // Filter by search query
        var filtered = if (query.isBlank()) {
            rawDocs
        } else {
            rawDocs.filter { it.displayName.contains(query, ignoreCase = true) }
        }

        // Filter by category
        filtered = when (filter) {
            DocumentFilter.ALL -> filtered
            DocumentFilter.IN_PROGRESS -> filtered.filter {
                val prog = progressMap[it.uriString]
                prog != null && prog.progressPercent in 1..99
            }
            DocumentFilter.UNREAD -> filtered.filter {
                val prog = progressMap[it.uriString]
                prog == null || prog.progressPercent == 0
            }
            DocumentFilter.COMPLETED -> filtered.filter {
                val prog = progressMap[it.uriString]
                prog != null && prog.progressPercent >= 100
            }
        }

        // Sort documents
        val sorted = when (sortOption) {
            SortOption.NAME_ASC -> filtered.sortedBy { it.displayName.lowercase() }
            SortOption.NAME_DESC -> filtered.sortedByDescending { it.displayName.lowercase() }
            SortOption.DATE_MODIFIED_DESC -> filtered.sortedByDescending { it.lastModified }
            SortOption.DATE_OPENED_DESC -> filtered.sortedByDescending {
                progressMap[it.uriString]?.lastOpenedTimestamp ?: 0L
            }
            SortOption.SIZE_DESC -> filtered.sortedByDescending { it.sizeBytes }
            SortOption.PROGRESS_DESC -> filtered.sortedByDescending {
                progressMap[it.uriString]?.progressPercent ?: 0
            }
        }

        LibraryUiState(
            documents = sorted,
            progressMap = progressMap,
            isLoading = loading,
            selectedFolderUri = folderUri,
            selectedFolderName = folderName,
            searchQuery = query,
            activeFilter = filter,
            viewMode = viewMode,
            sortOption = sortOption
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: DocumentFilter) {
        _activeFilter.value = filter
    }

    fun onFolderSelected(treeUri: Uri, folderName: String) {
        viewModelScope.launch {
            folderAccessManager.persistFolderPermission(treeUri)
            settingsRepository.setSelectedFolder(treeUri.toString(), folderName)
            scanCurrentFolder(treeUri)
        }
    }

    fun refreshFolder() {
        val folderUri = uiState.value.selectedFolderUri ?: return
        viewModelScope.launch {
            scanCurrentFolder(Uri.parse(folderUri))
        }
    }

    private suspend fun scanCurrentFolder(treeUri: Uri) {
        _isLoading.value = true
        try {
            // Read scan mode
            val scanMode = ScanMode.DIRECT // Default, or read from settings
            val docs = folderAccessManager.scanFolder(treeUri, scanMode)
            documentRepository.clearFolderDocuments(treeUri.toString())
            documentRepository.saveDocuments(docs)

            // Generate thumbnails for first few documents asynchronously
            docs.take(10).forEach { doc ->
                loadThumbnail(doc)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }

    fun loadThumbnail(doc: PdfDocument) {
        if (_thumbnails.value.containsKey(doc.uriString)) return
        viewModelScope.launch {
            val bitmap = pdfViewerManager.renderThumbnail(Uri.parse(doc.uriString))
            if (bitmap != null) {
                _thumbnails.value = _thumbnails.value + (doc.uriString to bitmap)
            }
        }
    }

    fun setViewMode(viewMode: ViewMode) {
        viewModelScope.launch {
            settingsRepository.setViewMode(viewMode)
        }
    }

    fun setSortOption(sortOption: SortOption) {
        viewModelScope.launch {
            settingsRepository.setSortOption(sortOption)
        }
    }
}
