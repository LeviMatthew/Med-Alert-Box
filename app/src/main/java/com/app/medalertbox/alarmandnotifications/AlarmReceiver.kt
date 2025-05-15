package com.app.medalertbox.alarmandnotifications

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.medalertbox.R

class AlarmReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null
    private val handlerThread = HandlerThread("AlarmStopHandler").apply { start() }
    private val stopAlarmHandler = Handler(handlerThread.looper)

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getStringExtra("alarm_id") ?: "default_id"
        val medicationName = intent.getStringExtra("medicationName") ?: "Medication Reminder"
        val notificationId = alarmId.hashCode()

        Log.d("AlarmReceiver", "Alarm received - action: $action, alarmId: $alarmId")

        if (action == "STOP_ALARM") {
            stopAlarmSound(context, notificationId)
            return
        }

        createNotificationChannel(context)
        showNotificationWithActions(context, medicationName, notificationId, alarmId)
        playAlarmSound(context, notificationId, alarmId)

        val fullScreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notificationId", notificationId)
            putExtra("alarm_id", alarmId)
        }
        context.startActivity(fullScreenIntent)

        Log.d("AlarmReceiver", "Launched FullScreenAlarmActivity for $action")
    }

    private fun showNotificationWithActions(context: Context, medicationName: String, notificationId: Int, alarmId: String) {
        val stopIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "STOP"
            putExtra("alarm_id", alarmId)
            putExtra("notificationId", notificationId)
        }

        val snoozeIntent = Intent(context, AlarmActionReceiver::class.java).apply {
            action = "SNOOZE"
            putExtra("alarm_id", alarmId)
            putExtra("notificationId", notificationId)
        }

        val stopPendingIntent = PendingIntent.getBroadcast(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, 1, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra("notificationId", notificationId)
            putExtra("alarm_id", alarmId)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 2, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take your $medicationName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .setAutoCancel(true)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun playAlarmSound(context: Context, notificationId: Int, alarmId: String) {
        stopAlarmHandler.removeCallbacksAndMessages(null)

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_clock).apply {
            isLooping = true
            start()
        }

        Log.d("AlarmReceiver", "Alarm sound started")

        // Auto-stop after 3 minutes
        stopAlarmHandler.postDelayed({
            stopAlarmSound(context, notificationId)
        }, 3 * 60 * 1000)
    }

    private fun stopAlarmSound(context: Context, notificationId: Int) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        NotificationManagerCompat.from(context).cancel(notificationId)

        Log.d("AlarmReceiver", "Alarm sound and notification stopped")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationHelper.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}