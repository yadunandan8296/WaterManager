package com.watermanager.data.repository

import androidx.lifecycle.LiveData
import com.watermanager.data.db.WaterLogDao
import com.watermanager.data.model.WaterLog

class WaterLogRepository(private val dao: WaterLogDao) {

    val allLogs: LiveData<List<WaterLog>> = dao.getAllLogs()

    suspend fun insert(log: WaterLog): Long = dao.insert(log)

    suspend fun getLatest(): WaterLog? = dao.getLatestLog()

    suspend fun delete(log: WaterLog) = dao.delete(log)
}
