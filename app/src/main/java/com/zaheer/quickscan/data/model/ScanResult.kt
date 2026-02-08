package com.zaheer.quickscan.data.model

data class ScanResult(
    val content: String,
    val type: ScanType,
    val format: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ScanType {
    URL,
    EMAIL,
    PHONE,
    SMS,
    WIFI,
    GEO,
    CONTACT,
    UPI,
    TEXT
}
