package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(alarm: AlarmNotification) {
        val alarmTime = getAlarmTimeInMillis(alarm.date, alarm.time)

        if (alarmTime <= System.currentTimeMillis()) {
            Log.e("AlarmScheduler", "Cannot schedule an alarm for a past time!")
            return
        }

        // Ensure exact alarm permissions for Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmScheduler", "Exact alarm permission required! Redirecting to settings...")
            requestExactAlarmPermission()
            return
        }

        // Schedule start alarm
        val startIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationName", alarm.medicationName)
            putExtra("notificationId", alarm.id)
            putExtra("alarm_action", "START")
        }

        val startPendingIntent = PendingIntent.getBroadcast(
            context, alarm.id, startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            startPendingIntent
        )

        // Automatically stop alarm after 3 minutes (180000 ms)
        scheduleAutoStopAlarm(alarm.id, alarmTime + 180000)

        Log.d("AlarmScheduler", "Alarm scheduled for ${alarm.date} at ${alarm.time}")
    }

    fun scheduleSnoozedAlarm(alarmId: Int, delayMillis: Long) {
        val snoozeTime = System.currentTimeMillis() + delayMillis

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notificationId", alarmId)
            putExtra("alarm_action", "SNOOZE")
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, alarmId + 2000, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeTime,
            snoozePendingIntent
        )

        Log.d("AlarmScheduler", "Snoozed alarm set for ${Date(snoozeTime)}")
    }

    fun scheduleAutoStopAlarm(alarmId: Int, stopTimeMillis: Long) {
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notificationId", alarmId)
            putExtra("alarm_action", "STOP")
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context, alarmId + 1000, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            stopTimeMillis,
            stopPendingIntent
        )

        Log.d("AlarmScheduler", "Auto-stop scheduled at ${Date(stopTimeMillis)}")
    }

    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        Log.d("AlarmScheduler", "Alarm with ID $alarmId canceled")
    }

    private fun getAlarmTimeInMillis(date: String, time: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
            dateFormat.parse("$date $time")?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error parsing date/time: ${e.message}")
            System.currentTimeMillis()
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}