package com.zaheer.quickscan.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zaheer.quickscan.billing.BillingManager
import com.zaheer.quickscan.data.repository.EntitlementRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PaywallViewModel(
    application: Application,
    private val entitlementRepository: EntitlementRepository
) : AndroidViewModel(application) {
    
    private lateinit var billingManager: BillingManager
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _purchaseSuccess = MutableStateFlow(false)
    val purchaseSuccess: StateFlow<Boolean> = _purchaseSuccess.asStateFlow()
    
    val isProActive = entitlementRepository.getEntitlement()
        .map { it?.isProActive ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    fun initBillingManager() {
        billingManager = BillingManager(getApplication()) { token, expiryTime ->
            viewModelScope.launch {
                entitlementRepository.setProActive(token, expiryTime)
                _purchaseSuccess.value = true
            }
        }
    }
    
    val productDetails = if (::billingManager.isInitialized) {
        billingManager.productDetails
    } else {
        MutableStateFlow(null)
    }
    
    fun launchBillingFlow(activity: android.app.Activity) {
        if (::billingManager.isInitialized) {
            _isLoading.value = true
            billingManager.launchBillingFlow(activity)
            _isLoading.value = false
        }
    }
    
    fun restorePurchases() {
        if (::billingManager.isInitialized) {
            _isLoading.value = true
            billingManager.restorePurchases()
            _isLoading.value = false
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        if (::billingManager.isInitialized) {
            billingManager.endConnection()
        }
    }
}
