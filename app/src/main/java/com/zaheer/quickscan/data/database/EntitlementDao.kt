package com.zaheer.quickscan.data.database

import androidx.room.*
import com.zaheer.quickscan.data.model.EntitlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntitlementDao {
    @Query("SELECT * FROM entitlement WHERE id = 1")
    fun getEntitlement(): Flow<EntitlementEntity?>
    
    @Query("SELECT * FROM entitlement WHERE id = 1")
    suspend fun getEntitlementOnce(): EntitlementEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entitlement: EntitlementEntity)
    
    @Update
    suspend fun update(entitlement: EntitlementEntity)
    
    @Query("UPDATE entitlement SET isProActive = :isActive WHERE id = 1")
    suspend fun updateProStatus(isActive: Boolean)
}
