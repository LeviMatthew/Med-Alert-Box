package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object AlarmHelper {

    private const val TAG = "AlarmHelper" // For logging

    // Function to set an alarm with the provided target time
    fun setAlarm(context: Context, targetTimeMillis: Long, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create intent with additional alarm_id to track the alarm
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("target_time", targetTimeMillis)
        }

        // Create a PendingIntent with the alarmId to ensure uniqueness
        val pendingIntent = createPendingIntent(context, alarmId, intent)

        // Set the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            targetTimeMillis,
            pendingIntent
        )

        // Log for confirmation
        Log.d(TAG, "Alarm set for time: $targetTimeMillis with alarmId: $alarmId")
    }

    // Function to set a snooze alarm
    fun setSnoozeAlarm(context: Context, snoozeTimeMillis: Long, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create intent for snoozed alarm with alarm_id to track the snooze
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "SNOOZED_ALARM"
            putExtra("alarm_id", alarmId)
            putExtra("target_time", snoozeTimeMillis)
        }

        // Create a PendingIntent for the snooze
        val snoozePendingIntent = createPendingIntent(context, alarmId, snoozeIntent)

        // Set snooze alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTimeMillis,
            snoozePendingIntent
        )

        // Log for confirmation
        Log.d(TAG, "Snooze alarm set for time: $snoozeTimeMillis with alarmId: $alarmId")
    }

    // Helper function to create PendingIntent with correct flags
    private fun createPendingIntent(context: Context, alarmId: String, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
