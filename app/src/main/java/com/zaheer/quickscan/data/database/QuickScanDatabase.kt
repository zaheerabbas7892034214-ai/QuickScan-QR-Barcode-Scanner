package com.zaheer.quickscan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zaheer.quickscan.data.model.EntitlementEntity
import com.zaheer.quickscan.data.model.ScanHistoryEntity

@Database(
    entities = [ScanHistoryEntity::class, EntitlementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QuickScanDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun entitlementDao(): EntitlementDao
}
