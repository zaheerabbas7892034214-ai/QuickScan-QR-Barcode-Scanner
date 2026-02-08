package com.zaheer.quickscan.util

object Constants {
    // Billing
    const val PRODUCT_ID = "quickscan_pro_yearly"
    const val BASE_PLAN_ID = "yearly_base"
    
    // Limits
    const val FREE_HISTORY_LIMIT = 20
    
    // Preferences
    const val PREF_NAME = "quickscan_prefs"
    const val KEY_PRO_STATUS = "pro_status"
    const val KEY_FIRST_LAUNCH = "first_launch"
    
    // Navigation Routes
    object Routes {
        const val SPLASH = "splash"
        const val SCANNER = "scanner"
        const val RESULT = "result"
        const val HISTORY = "history"
        const val QR_GENERATOR = "qr_generator"
        const val EXPORT = "export"
        const val PAYWALL = "paywall"
        const val SETTINGS = "settings"
        const val APP_LOCK = "app_lock"
    }
    
    // Intent Actions
    const val ACTION_SCAN = "com.zaheer.quickscan.ACTION_SCAN"
    
    // File Provider
    const val FILE_PROVIDER_AUTHORITY = "com.zaheer.quickscan.fileprovider"
}
