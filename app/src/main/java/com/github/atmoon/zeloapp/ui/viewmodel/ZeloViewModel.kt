package com.github.atmoon.zeloapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.atmoon.zeloapp.data.model.Comment
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.data.model.IssueSeverity
import com.github.atmoon.zeloapp.data.model.IssueSort
import com.github.atmoon.zeloapp.data.repository.IssueRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ZeloUiState(
    val allIssues: List<Issue> = emptyList(),
    val filteredIssues: List<Issue> = emptyList(),
    val searchQuery: String = "",
    val selectedCategories: Set<String> = emptySet(),
    val selectedSeverities: Set<String> = emptySet(),
    val onlySupported: Boolean = false,
    val onlyWithNotice: Boolean = false,
    val sortOrder: IssueSort = IssueSort.RECENT,
    val isLoading: Boolean = true
) {
    val activeFilterCount: Int
        get() = selectedCategories.size + selectedSeverities.size +
                (if (onlySupported) 1 else 0) + (if (onlyWithNotice) 1 else 0)
}

class ZeloViewModel(private val repository: IssueRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategories = MutableStateFlow<Set<String>>(emptySet())
    private val _selectedSeverities = MutableStateFlow<Set<String>>(emptySet())
    private val _onlySupported = MutableStateFlow(false)
    private val _onlyWithNotice = MutableStateFlow(false)
    private val _sortOrder = MutableStateFlow(IssueSort.RECENT)

    private val _feedbackMessage = MutableSharedFlow<String>()
    val feedbackMessage = _feedbackMessage.asSharedFlow()

    val uiState: StateFlow<ZeloUiState> = combine(
        repository.observeIssues(),
        _searchQuery,
        _selectedCategories,
        _selectedSeverities,
        _onlySupported,
        _onlyWithNotice,
        _sortOrder
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val issues = values[0] as List<Issue>
        val query = values[1] as String
        @Suppress("UNCHECKED_CAST")
        val categories = values[2] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val severities = values[3] as Set<String>
        val supportedOnly = values[4] as Boolean
        val noticeOnly = values[5] as Boolean
        val sort = values[6] as IssueSort

        val q = query.trim().lowercase()
        val filtered = issues.filter { issue ->
            val matchesQuery = q.isEmpty() ||
                    issue.title.lowercase().contains(q) ||
                    issue.address.lowercase().contains(q)
            val matchesCategory = categories.isEmpty() || categories.contains(issue.category)
            val matchesSeverity = severities.isEmpty() || severities.contains(issue.severity)
            val matchesSupported = !supportedOnly || issue.supportedByMe
            val matchesNotice = !noticeOnly || issue.hasNotice
            matchesQuery && matchesCategory && matchesSeverity && matchesSupported && matchesNotice
        }

        val sorted = when (sort) {
            IssueSort.RECENT -> filtered.sortedByDescending { it.createdAt }
            IssueSort.MOST_SUPPORTED -> filtered.sortedByDescending { it.likes }
            IssueSort.NEAR_GOAL -> filtered.sortedBy { if (it.hasNotice) Int.MAX_VALUE else it.supportsMissing }
            IssueSort.URGENT -> filtered.sortedWith(
                compareByDescending<Issue> { IssueSeverity.rank(it.severity) }
                    .thenByDescending { it.likes }
            )
        }

        ZeloUiState(
            allIssues = issues,
            filteredIssues = sorted,
            searchQuery = query,
            selectedCategories = categories,
            selectedSeverities = severities,
            onlySupported = supportedOnly,
            onlyWithNotice = noticeOnly,
            sortOrder = sort,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ZeloUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategoryFilter(category: String) {
        _selectedCategories.update { current ->
            if (category in current) current - category else current + category
        }
    }

    fun toggleSeverityFilter(severity: String) {
        _selectedSeverities.update { current ->
            if (severity in current) current - severity else current + severity
        }
    }

    fun setOnlySupported(value: Boolean) {
        _onlySupported.value = value
    }

    fun setOnlyWithNotice(value: Boolean) {
        _onlyWithNotice.value = value
    }

    fun clearFilters() {
        _selectedCategories.value = emptySet()
        _selectedSeverities.value = emptySet()
        _onlySupported.value = false
        _onlyWithNotice.value = false
    }

    fun updateSortOrder(sort: IssueSort) {
        _sortOrder.value = sort
    }

    fun toggleSupport(issue: Issue) {
        viewModelScope.launch {
            val hadNotice = issue.hasNotice
            val updated = repository.toggleSupport(issue)
            val msg = when {
                !updated.supportedByMe -> "Apoio removido."
                !hadNotice && updated.hasNotice -> "Meta de 80% atingida! Ofício formal gerado."
                else -> "Obrigado pelo seu apoio comunitário!"
            }
            _feedbackMessage.emit(msg)
        }
    }

    fun createIssue(
        title: String,
        description: String,
        category: String,
        address: String,
        severity: String,
        onCreated: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createIssue(title, description, category, address, severity)
            _feedbackMessage.emit("Ocorrência publicada com sucesso!")
            onCreated()
        }
    }

    fun deleteIssue(issue: Issue, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteIssue(issue)
            _feedbackMessage.emit("Ocorrência excluída.")
            onDeleted()
        }
    }

    fun observeComments(issueId: String): Flow<List<Comment>> = repository.observeComments(issueId)

    fun addComment(issueId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(issueId, text)
            _feedbackMessage.emit("Comentário enviado.")
        }
    }

    class Factory(private val repository: IssueRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ZeloViewModel(repository) as T
        }
    }
}