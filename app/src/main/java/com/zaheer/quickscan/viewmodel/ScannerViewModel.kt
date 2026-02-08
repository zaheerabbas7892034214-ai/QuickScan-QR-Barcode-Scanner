package com.zaheer.quickscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.quickscan.data.model.ScanHistoryEntity
import com.zaheer.quickscan.data.model.ScanResult
import com.zaheer.quickscan.data.repository.EntitlementRepository
import com.zaheer.quickscan.data.repository.ScanHistoryRepository
import com.zaheer.quickscan.util.Constants
import com.zaheer.quickscan.util.ResultParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val historyRepository: ScanHistoryRepository,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {
    
    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()
    
    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled.asStateFlow()
    
    val isProActive = entitlementRepository.getEntitlement()
        .map { it?.isProActive ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    fun onBarcodeScanned(content: String, format: String) {
        viewModelScope.launch {
            val type = ResultParser.parseType(content)
            val result = ScanResult(content, type, format)
            _scanResult.value = result
            
            val entity = ScanHistoryEntity(
                content = content,
                type = type.name,
                format = format,
                timestamp = System.currentTimeMillis()
            )
            
            val count = historyRepository.getCount()
            val isPro = isProActive.value
            
            if (isPro || count < Constants.FREE_HISTORY_LIMIT) {
                historyRepository.insertScan(entity)
            }
        }
    }
    
    fun clearScanResult() {
        _scanResult.value = null
    }
    
    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
    }
}
