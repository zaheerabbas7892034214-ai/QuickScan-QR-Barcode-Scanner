package com.zaheer.quickscan.data.database

import androidx.room.*
import com.zaheer.quickscan.data.model.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    fun getScansWithLimit(limit: Int): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteScans(): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT * FROM scan_history WHERE content LIKE '%' || :query || '%' OR type LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>>
    
    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: ScanHistoryEntity): Long
    
    @Update
    suspend fun update(scan: ScanHistoryEntity)
    
    @Delete
    suspend fun delete(scan: ScanHistoryEntity)
    
    @Query("DELETE FROM scan_history")
    suspend fun deleteAll()
    
    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
