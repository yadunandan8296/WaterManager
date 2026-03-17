package com.watermanager.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.watermanager.data.model.WaterLog

@Dao
interface WaterLogDao {

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): LiveData<List<WaterLog>>

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLog(): WaterLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: WaterLog): Long

    @Query("SELECT * FROM water_logs WHERE id = :id")
    suspend fun getById(id: Long): WaterLog?

    @Delete
    suspend fun delete(log: WaterLog)

    @Query("DELETE FROM water_logs")
    suspend fun deleteAll()
}
