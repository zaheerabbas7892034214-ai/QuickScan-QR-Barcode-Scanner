package com.zaheer.quickscan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entitlement")
data class EntitlementEntity(
    @PrimaryKey
    val id: Int = 1,
    val isProActive: Boolean = false,
    val purchaseToken: String? = null,
    val expiryTime: Long? = null
)
