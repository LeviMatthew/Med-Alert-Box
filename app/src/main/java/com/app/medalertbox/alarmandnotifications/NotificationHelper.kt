package com.app.medalertbox.alarmandnotifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "ALARM_CHANNEL"
        const val CHANNEL_NAME = "Medication Alerts"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE_STOP_OFFSET = 2000
        const val REQUEST_CODE_SNOOZE_OFFSET = 3000
    }

    init {
        createNotificationChannel()
    }

    /**
     * Creates the notification channel required for Android O and above.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies users about their medication schedules."
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a full-screen alarm notification with STOP and SNOOZE actions.
     */
    fun showAlarmNotification(alarm: AlarmNotification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Full-screen intent to open the alarm activity
        val fullScreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medicationName", alarm.medicationName)
            putExtra("notificationId", alarm.id)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarm.id,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // STOP action
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notificationId", alarm.id)
            putExtra("alarm_action", "STOP")
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id + REQUEST_CODE_STOP_OFFSET,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // SNOOZE action
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notificationId", alarm.id)
            putExtra("alarm_action", "SNOOZE")
            putExtra("medicationName", alarm.medicationName)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id + REQUEST_CODE_SNOOZE_OFFSET,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take: ${alarm.medicationName}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(android.R.drawable.ic_delete, "Stop", stopPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Snooze", snoozePendingIntent)

        notificationManager.notify(alarm.id, builder.build())
        Log.d("NotificationHelper", "Alarm notification shown with STOP and SNOOZE for ID: ${alarm.id}")
    }

    /**
     * Cancels the alarm and associated notification.
     */
    fun cancelNotification(alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId)

        Log.d("NotificationHelper", "Canceled alarm and notification for ID: $alarmId")
    }

    /**
     * Schedules an alarm notification based on date and time.
     */
    fun scheduleNotification(alarm: AlarmNotification) {
        val alarmTime = getAlarmTimeInMillis(alarm.date, alarm.time)
        if (alarmTime <= System.currentTimeMillis()) {
            Log.w("NotificationHelper", "Attempted to schedule alarm in the past. Skipping.")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("NotificationHelper", "Exact alarm permission not granted. Requesting...")
            requestExactAlarmPermission()
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicationName", alarm.medicationName)
            putExtra("notificationId", alarm.id)
            putExtra("alarm_action", "START")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

        Log.d("NotificationHelper", "Scheduled alarm for ${alarm.date} ${alarm.time} (ID: ${alarm.id})")
    }

    /**
     * Converts date and time to milliseconds.
     */
    private fun getAlarmTimeInMillis(date: String, time: String): Long {
        return try {
            val format = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
            format.parse("$date $time")?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Failed to parse alarm time: ${e.message}")
            System.currentTimeMillis()
        }
    }

    /**
     * Opens system settings to request exact alarm permission (Android S+).
     */
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}