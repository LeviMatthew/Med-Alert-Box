package com.app.medalertbox.alarmandnotifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "ALARM_CHANNEL"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    // Create notification channel (required for devices running Android O and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medication Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies users about their medication schedules."
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Schedule notification for an alarm
    fun scheduleNotification(alarm: AlarmNotification) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmTime = getAlarmTimeInMillis(alarm.date, alarm.time)

        if (alarmTime <= System.currentTimeMillis()) {
            Log.e("NotificationHelper", "Cannot schedule past alarms! Skipping...")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationName", alarm.medicationName)
            putExtra("notificationId", alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Check permission to schedule exact alarms on Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("NotificationHelper", "Exact alarm permission required! Redirecting to settings...")
            requestExactAlarmPermission()
            return
        }

        // Set the exact alarm to trigger at the specified time
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

        Log.d("NotificationHelper", "Alarm set for ${alarm.date} at ${alarm.time} (Timestamp: $alarmTime)")
    }

    // Cancel the scheduled alarm notification
    fun cancelNotification(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Log.d("NotificationHelper", "Alarm with ID $alarmId canceled")
    }

    // Convert date and time into milliseconds
    private fun getAlarmTimeInMillis(date: String, time: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
            dateFormat.parse("$date $time")?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error parsing date/time: ${e.message}")
            System.currentTimeMillis()
        }
    }

    // Request permission to schedule exact alarms on Android 12 and above
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    }
}
