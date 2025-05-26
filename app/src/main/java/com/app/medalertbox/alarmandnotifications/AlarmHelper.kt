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
     * Sets a snoozed alarm to trigger after a specified delay.
     *
     * @param context Application context
     * @param timeInMillis Time when the alarm should go off (in milliseconds)
     * @param alarmId Unique identifier for the alarm
     * @param medicationName Medication name to be shown in notification and activity
     */
    fun setSnoozeAlarm(
        context: Context,
        timeInMillis: Long,
        alarmId: String,
        medicationName: String
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "SNOOZE_ALARM"
            putExtra("alarm_id", alarmId)
            putExtra("medication_name", medicationName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12+ requires special permission for exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Exact alarm permission not granted. Cannot schedule snooze alarm.")
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Log.d(TAG, "Snooze alarm set for $timeInMillis with medication: $medicationName")
    }
}
