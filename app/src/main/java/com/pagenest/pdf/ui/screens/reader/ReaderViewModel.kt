package com.pagenest.pdf.ui.screens.reader

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val errorMessage: String? = null
)

class ReaderViewModel(
    val pdfViewerManager: PdfViewerManager,
    private val readingProgressRepository: ReadingProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var saveProgressJob: Job? = null

    fun loadDocument(uriString: String, name: String, initialPage: Int = 1) {
        _uiState.value = _uiState.value.copy(
            documentUri = uriString,
            documentName = name,
            currentPage = initialPage,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val uri = Uri.parse(uriString)
                val result = pdfViewerManager.openDocument(uri)
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 1
                    val safePage = initialPage.coerceIn(1, count)
                    _uiState.value = _uiState.value.copy(
                        totalPages = count,
                        currentPage = safePage,
                        isLoading = false
                    )
                    // Persist initial open
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

    fun onPageChanged(page: Int) {
        val total = _uiState.value.totalPages
        if (total <= 0) return
        val clampedPage = page.coerceIn(1, total)
        if (clampedPage != _uiState.value.currentPage) {
            _uiState.value = _uiState.value.copy(currentPage = clampedPage)
            scheduleDebouncedProgressSave(clampedPage, total)
        }
    }

    private fun scheduleDebouncedProgressSave(page: Int, totalPages: Int) {
        saveProgressJob?.cancel()
        saveProgressJob = viewModelScope.launch {
            delay(1500) // Debounce 1.5 seconds to avoid DB churn during rapid flings
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

    fun resetZoom() {
        _uiState.value = _uiState.value.copy(zoomScale = 1.0f)
    }

    override fun onCleared() {
        super.onCleared()
        // Flush final progress on exit
        val state = _uiState.value
        if (state.documentUri.isNotBlank() && state.totalPages > 0) {
            viewModelScope.launch {
                persistProgress(state.currentPage, state.totalPages)
            }
        }
        pdfViewerManager.closeDocument()
    }
}
