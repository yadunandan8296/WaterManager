package com.watermanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: String,       // "HH:mm" format
    val durationMinutes: Int,
    val endTime: String,         // calculated
    val timestamp: Long = System.currentTimeMillis(),  // epoch for sorting
    val sentToCount: Int = 0
)
