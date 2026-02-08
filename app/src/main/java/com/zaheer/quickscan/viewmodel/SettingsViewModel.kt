package com.zaheer.quickscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.quickscan.data.repository.EntitlementRepository
import com.zaheer.quickscan.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {
    
    val isProActive = entitlementRepository.getEntitlement()
        .map { it?.isProActive ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val isDarkMode = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val isAppLockEnabled = settingsRepository.isAppLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val useBiometric = settingsRepository.useBiometric
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val vibrateOnScan = settingsRepository.vibrateOnScan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val soundOnScan = settingsRepository.soundOnScan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }
    
    fun setAppLock(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAppLock(enabled)
        }
    }
    
    fun setUseBiometric(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseBiometric(enabled)
        }
    }
    
    fun setVibrateOnScan(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVibrateOnScan(enabled)
        }
    }
    
    fun setSoundOnScan(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundOnScan(enabled)
        }
    }
    
    fun setPinCode(pinOrNull: String?) {
        viewModelScope.launch {
            settingsRepository.setPinCode(pinOrNull)
        }
    }
}
