package com.watermanager.data.repository

import androidx.lifecycle.LiveData
import com.watermanager.data.db.TenantDao
import com.watermanager.data.model.Tenant

class TenantRepository(private val dao: TenantDao) {

    val allTenants: LiveData<List<Tenant>> = dao.getAllTenants()

    suspend fun insert(tenant: Tenant): Long = dao.insert(tenant)

    suspend fun update(tenant: Tenant) = dao.update(tenant)

    suspend fun delete(tenant: Tenant) = dao.delete(tenant)

    suspend fun getAllOnce(): List<Tenant> = dao.getAllTenantsOnce()
}
