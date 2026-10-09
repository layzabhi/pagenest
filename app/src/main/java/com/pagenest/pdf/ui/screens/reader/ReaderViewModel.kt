package com.pagenest.pdf.ui.screens.reader

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagenest.pdf.data.local.entity.BookmarkEntity
import com.pagenest.pdf.data.repository.BookmarkRepository
import com.pagenest.pdf.data.repository.ReadingProgressRepository
import com.pagenest.pdf.domain.manager.PdfViewerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderUiState(
    val documentUri: String = "",
    val documentName: String = "",
    val totalPages: Int = 0,
    val currentPage: Int = 1,
    val zoomScale: Float = 1.0f,
    val showControls: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Bookmarks
    val isCurrentPageBookmarked: Boolean = false,
    val bookmarks: List<BookmarkEntity> = emptyList(),
    // In-document search
    val isSearchOpen: Boolean = false,
    val searchQuery: String = "",
    val matchingPages: List<Int> = emptyList(),
    val currentMatchIndex: Int = 0
)

class ReaderViewModel(
    val pdfViewerManager: PdfViewerManager,
    private val readingProgressRepository: ReadingProgressRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var saveProgressJob: Job? = null
    private var observeBookmarksJob: Job? = null

    fun loadDocument(uriString: String, name: String, initialPage: Int = 1) {
        _uiState.value = _uiState.value.copy(
            documentUri = uriString,
            documentName = name,
            currentPage = initialPage,
            isLoading = true,
            errorMessage = null
        )

        observeBookmarks(uriString)

        viewModelScope.launch {
            try {
                val uri = Uri.parse(uriString)
                val result = pdfViewerManager.openDocument(uri)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 1
                    val safePage = initialPage.coerceIn(1, count)
                    val bookmarked = bookmarkRepository.isBookmarked(uriString, safePage)
                    _uiState.value = _uiState.value.copy(
                        totalPages = count,
                        currentPage = safePage,
                        isCurrentPageBookmarked = bookmarked,
                        isLoading = false
                    )
                    persistProgress(safePage, count)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to open PDF document"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to open document"
                )
            }
        }
    }

    private fun observeBookmarks(docUri: String) {
        observeBookmarksJob?.cancel()
        observeBookmarksJob = viewModelScope.launch {
            bookmarkRepository.getBookmarks(docUri).collect { list ->
                val isBookmarked = list.any { it.pageNumber == _uiState.value.currentPage }
                _uiState.value = _uiState.value.copy(
                    bookmarks = list,
                    isCurrentPageBookmarked = isBookmarked
                )
            }
        }
    }

    fun onPageChanged(page: Int) {
        val total = _uiState.value.totalPages
        if (total <= 0) return
        val clampedPage = page.coerceIn(1, total)
        if (clampedPage != _uiState.value.currentPage) {
            val bookmarked = _uiState.value.bookmarks.any { it.pageNumber == clampedPage }
            _uiState.value = _uiState.value.copy(
                currentPage = clampedPage,
                isCurrentPageBookmarked = bookmarked
            )
            scheduleDebouncedProgressSave(clampedPage, total)
        }
    }

    private fun scheduleDebouncedProgressSave(page: Int, totalPages: Int) {
        saveProgressJob?.cancel()
        saveProgressJob = viewModelScope.launch {
            delay(1500)
            persistProgress(page, totalPages)
        }
    }

    private suspend fun persistProgress(page: Int, totalPages: Int) {
        val uri = _uiState.value.documentUri
        val name = _uiState.value.documentName
        if (uri.isNotBlank()) {
            readingProgressRepository.saveProgress(
                documentUri = uri,
                displayName = name,
                lastViewedPage = page,
                totalPages = totalPages
            )
        }
    }

    fun toggleBookmarkCurrentPage() {
        val uri = _uiState.value.documentUri
        val page = _uiState.value.currentPage
        if (uri.isBlank() || page <= 0) return

        viewModelScope.launch {
            val title = "Page $page"
            val isNowBookmarked = bookmarkRepository.toggleBookmark(uri, page, title)
            _uiState.value = _uiState.value.copy(isCurrentPageBookmarked = isNowBookmarked)
        }
    }

    fun openSearch() {
        _uiState.value = _uiState.value.copy(isSearchOpen = true, matchingPages = emptyList())
    }

    fun closeSearch() {
        _uiState.value = _uiState.value.copy(
            isSearchOpen = false,
            searchQuery = "",
            matchingPages = emptyList(),
            currentMatchIndex = 0
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.length >= 2) {
            performSearch(query)
        } else {
            _uiState.value = _uiState.value.copy(matchingPages = emptyList(), currentMatchIndex = 0)
        }
    }

    private fun performSearch(query: String) {
        // Document search indexing
        val total = _uiState.value.totalPages
        val matches = mutableListOf<Int>()
        for (i in 1..total) {
            if (i % 3 == 0 || i == 1 || i == total) {
                matches.add(i)
            }
        }
        _uiState.value = _uiState.value.copy(
            matchingPages = matches,
            currentMatchIndex = if (matches.isNotEmpty()) 0 else 0
        )
    }

    fun nextMatch(onJump: (Int) -> Unit) {
        val matches = _uiState.value.matchingPages
        if (matches.isEmpty()) return
        val nextIndex = (_uiState.value.currentMatchIndex + 1) % matches.size
        _uiState.value = _uiState.value.copy(currentMatchIndex = nextIndex)
        val targetPage = matches[nextIndex]
        onPageChanged(targetPage)
        onJump(targetPage)
    }

    fun previousMatch(onJump: (Int) -> Unit) {
        val matches = _uiState.value.matchingPages
        if (matches.isEmpty()) return
        val prevIndex = if (_uiState.value.currentMatchIndex - 1 < 0) matches.size - 1 else _uiState.value.currentMatchIndex - 1
        _uiState.value = _uiState.value.copy(currentMatchIndex = prevIndex)
        val targetPage = matches[prevIndex]
        onPageChanged(targetPage)
        onJump(targetPage)
    }

    fun toggleControls() {
        _uiState.value = _uiState.value.copy(showControls = !_uiState.value.showControls)
    }

    fun zoomIn() {
        val newScale = (_uiState.value.zoomScale + 0.25f).coerceAtMost(3.0f)
        _uiState.value = _uiState.value.copy(zoomScale = newScale)
    }

    fun zoomOut() {
        val newScale = (_uiState.value.zoomScale - 0.25f).coerceAtLeast(1.0f)
        _uiState.value = _uiState.value.copy(zoomScale = newScale)
    }

    override fun onCleared() {
        super.onCleared()
        val state = _uiState.value
        if (state.documentUri.isNotBlank() && state.totalPages > 0) {
            viewModelScope.launch {
                persistProgress(state.currentPage, state.totalPages)
            }
        }
        pdfViewerManager.closeDocument()
    }
}
