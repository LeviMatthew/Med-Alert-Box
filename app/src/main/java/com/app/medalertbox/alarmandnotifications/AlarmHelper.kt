package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object AlarmHelper {

    private const val TAG = "AlarmHelper"

    /**
     * Set a one-time alarm (normal medication reminder).
     */
    fun setAlarm(context: Context, targetTimeMillis: Long, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("target_time", targetTimeMillis)
            action = "NORMAL_ALARM"
        }

        val pendingIntent = createPendingIntent(context, alarmId.hashCode(), intent)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            targetTimeMillis,
            pendingIntent
        )

        Log.d(TAG, "Alarm set for $targetTimeMillis with alarmId: $alarmId")
    }

    /**
     * Set a snoozed alarm triggered by the snooze button (5 minutes later).
     */
    fun setSnoozeAlarm(context: Context, snoozeTimeMillis: Long, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("target_time", snoozeTimeMillis)
            action = "SNOOZED_ALARM"
        }

        val snoozeRequestCode = alarmId.hashCode() + 1000 // Ensure different from normal alarm
        val pendingIntent = createPendingIntent(context, snoozeRequestCode, snoozeIntent)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTimeMillis,
            pendingIntent
        )

        Log.d(TAG, "Snooze alarm set for $snoozeTimeMillis with alarmId: $alarmId")
    }

    /**
     * Cancel a scheduled normal alarm.
     */
    fun cancelAlarm(context: Context, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "NORMAL_ALARM"
        }

        val pendingIntent = createPendingIntent(context, alarmId.hashCode(), intent)

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Alarm canceled for alarmId: $alarmId")
    }

    /**
     * Cancel a scheduled snoozed alarm.
     */
    fun cancelSnooze(context: Context, alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "SNOOZED_ALARM"
        }

        val snoozeRequestCode = alarmId.hashCode() + 1000
        val pendingIntent = createPendingIntent(context, snoozeRequestCode, intent)

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Snoozed alarm canceled for alarmId: $alarmId")
    }

    /**
     * Helper method to create a consistent PendingIntent for alarms.
     */
    private fun createPendingIntent(context: Context, requestCode: Int, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}