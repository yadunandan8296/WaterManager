package com.watermanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.watermanager.data.db.AppDatabase
import com.watermanager.data.model.Tenant
import com.watermanager.data.repository.TenantRepository
import kotlinx.coroutines.launch

class TenantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TenantRepository
    val allTenants: LiveData<List<Tenant>>

    init {
        val dao = AppDatabase.getDatabase(application).tenantDao()
        repository = TenantRepository(dao)
        allTenants = repository.allTenants
    }

    fun insert(tenant: Tenant) = viewModelScope.launch {
        repository.insert(tenant)
    }

    fun update(tenant: Tenant) = viewModelScope.launch {
        repository.update(tenant)
    }

    fun delete(tenant: Tenant) = viewModelScope.launch {
        repository.delete(tenant)
    }

    suspend fun getAllOnce(): List<Tenant> = repository.getAllOnce()
}
