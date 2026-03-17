package com.watermanager.utils

import android.telephony.SmsManager
import java.util.Calendar

object SmsUtils {

    fun buildSmsMessage(startTime: String, durationMinutes: Int, endTime: String): String {
        return """
Water Update 🚰

Tank filled at: $startTime
Duration: $durationMinutes minutes
Water available till: $endTime

Please use accordingly.
        """.trimIndent()
    }

    /**
     * Sends an SMS via the system SmsManager.
     * Returns true on success, false on failure.
     */
    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager: SmsManager = SmsManager.getDefault()
            // Split if message exceeds SMS limit
            val parts = smsManager.divideMessage(message)
            if (parts.size == 1) {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            } else {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

object TimeUtils {

    /**
     * Calculates end time given a start time string "HH:mm" and duration in minutes.
     * Returns "HH:mm" string.
     */
    fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        val parts = startTime.split(":")
        val hour = parts[0].toIntOrNull() ?: 0
        val minute = parts[1].toIntOrNull() ?: 0

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.add(Calendar.MINUTE, durationMinutes)

        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    fun formatTime(hour: Int, minute: Int): String =
        String.format("%02d:%02d", hour, minute)

    fun currentHourMinute(): Pair<Int, Int> {
        val cal = Calendar.getInstance()
        return Pair(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }
}
