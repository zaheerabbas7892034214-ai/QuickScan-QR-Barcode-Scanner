package com.zaheer.quickscan.data.repository

import com.zaheer.quickscan.data.database.EntitlementDao
import com.zaheer.quickscan.data.model.EntitlementEntity
import kotlinx.coroutines.flow.Flow

class EntitlementRepository(private val dao: EntitlementDao) {
    
    fun getEntitlement(): Flow<EntitlementEntity?> = dao.getEntitlement()
    
    suspend fun getEntitlementOnce(): EntitlementEntity? = dao.getEntitlementOnce()
    
    suspend fun insertEntitlement(entitlement: EntitlementEntity) = 
        dao.insert(entitlement)
    
    suspend fun updateEntitlement(entitlement: EntitlementEntity) = 
        dao.update(entitlement)
    
    suspend fun updateProStatus(isActive: Boolean) = 
        dao.updateProStatus(isActive)
    
    suspend fun setProActive(token: String?, expiryTime: Long?) {
        val current = getEntitlementOnce()
        if (current != null) {
            updateEntitlement(
                current.copy(
                    isProActive = true,
                    purchaseToken = token,
                    expiryTime = expiryTime
                )
            )
        } else {
            insertEntitlement(
                EntitlementEntity(
                    isProActive = true,
                    purchaseToken = token,
                    expiryTime = expiryTime
                )
            )
        }
    }
}
