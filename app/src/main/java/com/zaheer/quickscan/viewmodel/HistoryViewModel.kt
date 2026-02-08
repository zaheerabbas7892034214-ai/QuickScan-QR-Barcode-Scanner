package com.zaheer.quickscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.quickscan.data.model.ScanHistoryEntity
import com.zaheer.quickscan.data.repository.EntitlementRepository
import com.zaheer.quickscan.data.repository.ScanHistoryRepository
import com.zaheer.quickscan.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val historyRepository: ScanHistoryRepository,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val isProActive = entitlementRepository.getEntitlement()
        .map { it?.isProActive ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val scans: StateFlow<List<ScanHistoryEntity>> = combine(
        _searchQuery,
        isProActive
    ) { query, isPro ->
        query to isPro
    }.flatMapLatest { (query, isPro) ->
        if (query.isEmpty()) {
            if (isPro) {
                historyRepository.getAllScans()
            } else {
                historyRepository.getScansWithLimit(Constants.FREE_HISTORY_LIMIT)
            }
        } else {
            historyRepository.searchScans(query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun deleteScan(scan: ScanHistoryEntity) {
        viewModelScope.launch {
            historyRepository.deleteScan(scan)
        }
    }
    
    fun toggleFavorite(scan: ScanHistoryEntity) {
        viewModelScope.launch {
            historyRepository.toggleFavorite(scan)
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
