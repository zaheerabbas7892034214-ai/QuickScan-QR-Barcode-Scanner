package com.zaheer.quickscan.data.repository

import com.zaheer.quickscan.data.database.ScanHistoryDao
import com.zaheer.quickscan.data.model.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanHistoryRepository(private val dao: ScanHistoryDao) {
    
    fun getAllScans(): Flow<List<ScanHistoryEntity>> = dao.getAllScans()
    
    fun getScansWithLimit(limit: Int): Flow<List<ScanHistoryEntity>> = 
        dao.getScansWithLimit(limit)
    
    fun getFavoriteScans(): Flow<List<ScanHistoryEntity>> = dao.getFavoriteScans()
    
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>> = 
        dao.searchScans(query)
    
    suspend fun getCount(): Int = dao.getCount()
    
    suspend fun insertScan(scan: ScanHistoryEntity): Long = dao.insert(scan)
    
    suspend fun updateScan(scan: ScanHistoryEntity) = dao.update(scan)
    
    suspend fun deleteScan(scan: ScanHistoryEntity) = dao.delete(scan)
    
    suspend fun deleteAll() = dao.deleteAll()
    
    suspend fun deleteById(id: Long) = dao.deleteById(id)
    
    suspend fun toggleFavorite(scan: ScanHistoryEntity) {
        dao.update(scan.copy(isFavorite = !scan.isFavorite))
    }
}
