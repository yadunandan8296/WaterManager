package com.watermanager.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watermanager.data.model.Tenant

@Dao
interface TenantDao {

    @Query("SELECT * FROM tenants ORDER BY name ASC")
    fun getAllTenants(): LiveData<List<Tenant>>

    @Query("SELECT * FROM tenants ORDER BY name ASC")
    suspend fun getAllTenantsOnce(): List<Tenant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tenant: Tenant): Long

    @Update
    suspend fun update(tenant: Tenant)

    @Delete
    suspend fun delete(tenant: Tenant)

    @Query("DELETE FROM tenants")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM tenants")
    suspend fun count(): Int
}
