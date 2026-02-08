package com.zaheer.quickscan.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: QuickScanDatabase? = null
    
    fun getDatabase(context: Context): QuickScanDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                QuickScanDatabase::class.java,
                "quickscan_database"
            )
                .fallbackToDestructiveMigration()
                .build()
            INSTANCE = instance
            instance
        }
    }
}
