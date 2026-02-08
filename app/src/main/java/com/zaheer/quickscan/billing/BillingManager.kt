package com.zaheer.quickscan.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.zaheer.quickscan.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(
    private val context: Context,
    private val onPurchaseSuccess: (String, Long?) -> Unit
) : PurchasesUpdatedListener {
    
    private val _isProActive = MutableStateFlow(false)
    val isProActive: StateFlow<Boolean> = _isProActive.asStateFlow()
    
    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()
    
    private var billingClient: BillingClient? = null
    
    init {
        setupBillingClient()
    }
    
    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        
        connectToPlayBilling()
    }
    
    private fun connectToPlayBilling() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "Billing client connected")
                    queryProductDetails()
                    queryPurchases()
                } else {
                    Log.e("BillingManager", "Billing setup failed: ${billingResult.debugMessage}")
                }
            }
            
            override fun onBillingServiceDisconnected() {
                Log.d("BillingManager", "Billing service disconnected")
                connectToPlayBilling()
            }
        })
    }
    
    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(Constants.PRODUCT_ID)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        
        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (productDetailsList.isNotEmpty()) {
                    _productDetails.value = productDetailsList[0]
                    Log.d("BillingManager", "Product details loaded: ${productDetailsList[0].name}")
                } else {
                    Log.w("BillingManager", "No product details found")
                }
            } else {
                Log.e("BillingManager", "Query product details failed: ${billingResult.debugMessage}")
            }
        }
    }
    
    fun launchBillingFlow(activity: Activity) {
        val productDetails = _productDetails.value
        if (productDetails == null) {
            Log.e("BillingManager", "Product details not available")
            return
        }
        
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull {
            it.basePlanId == Constants.BASE_PLAN_ID
        }?.offerToken
        
        if (offerToken == null) {
            Log.e("BillingManager", "Offer token not found")
            return
        }
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )
        
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        
        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }
    
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BillingManager", "User canceled the purchase")
        } else {
            Log.e("BillingManager", "Purchase failed: ${billingResult.debugMessage}")
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                val expiryTime = getExpiryTime(purchase)
                onPurchaseSuccess(purchase.purchaseToken, expiryTime)
                _isProActive.value = true
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d("BillingManager", "Purchase acknowledged")
                val expiryTime = getExpiryTime(purchase)
                onPurchaseSuccess(purchase.purchaseToken, expiryTime)
                _isProActive.value = true
            } else {
                Log.e("BillingManager", "Acknowledge failed: ${billingResult.debugMessage}")
            }
        }
    }
    
    fun queryPurchases() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var hasActivePurchase = false
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasActivePurchase = true
                        val expiryTime = getExpiryTime(purchase)
                        if (expiryTime == null || expiryTime > System.currentTimeMillis()) {
                            onPurchaseSuccess(purchase.purchaseToken, expiryTime)
                        }
                    }
                }
                _isProActive.value = hasActivePurchase
                Log.d("BillingManager", "Active purchases: ${purchases.size}, Pro active: $hasActivePurchase")
            } else {
                Log.e("BillingManager", "Query purchases failed: ${billingResult.debugMessage}")
            }
        }
    }
    
    private fun getExpiryTime(purchase: Purchase): Long? {
        return try {
            val accountIdentifiers = purchase.accountIdentifiers
            null
        } catch (e: Exception) {
            null
        }
    }
    
    fun restorePurchases() {
        queryPurchases()
    }
    
    fun endConnection() {
        billingClient?.endConnection()
    }
}
