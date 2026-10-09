package com.pagenest.pdf.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagenest.pdf.data.repository.ReadingProgressRepository
import com.pagenest.pdf.domain.model.ReadingProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val historyList: List<ReadingProgress> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val readingProgressRepository: ReadingProgressRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        readingProgressRepository.getAllReadingProgress(),
        _searchQuery
    ) { progressList, query ->
        val filtered = if (query.isBlank()) {
            progressList
        } else {
            progressList.filter { it.displayName.contains(query, ignoreCase = true) }
        }
        HistoryUiState(
            historyList = filtered,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun removeFromHistory(documentUri: String) {
        viewModelScope.launch {
            readingProgressRepository.deleteProgress(documentUri)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            readingProgressRepository.clearAllHistory()
        }
    }
}
