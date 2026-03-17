package com.watermanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.watermanager.data.db.AppDatabase
import com.watermanager.data.model.WaterLog
import com.watermanager.data.repository.WaterLogRepository
import com.watermanager.utils.TimeUtils
import kotlinx.coroutines.launch

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterLogRepository
    val allLogs: LiveData<List<WaterLog>>

    // Ephemeral state for current entry
    private val _currentLog = MutableLiveData<WaterLog?>()
    val currentLog: LiveData<WaterLog?> = _currentLog

    init {
        val dao = AppDatabase.getDatabase(application).waterLogDao()
        repository = WaterLogRepository(dao)
        allLogs = repository.allLogs
    }

    fun prepareEntry(startTime: String, durationMinutes: Int) {
        val endTime = TimeUtils.calculateEndTime(startTime, durationMinutes)
        _currentLog.value = WaterLog(
            startTime = startTime,
            durationMinutes = durationMinutes,
            endTime = endTime
        )
    }

    fun saveLog(sentToCount: Int) = viewModelScope.launch {
        _currentLog.value?.let { log ->
            val saved = log.copy(sentToCount = sentToCount)
            val id = repository.insert(saved)
            _currentLog.value = saved.copy(id = id)
        }
    }

    fun loadLatestLog() = viewModelScope.launch {
        _currentLog.value = repository.getLatest()
    }

    fun deleteLog(log: WaterLog) = viewModelScope.launch {
        repository.delete(log)
    }

    fun clearCurrentLog() {
        _currentLog.value = null
    }
}
