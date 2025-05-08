package com.app.medalertbox.alarmandnotifications

import android.app.NotificationChannel
import android.app.NotificationManager
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
        val notificationId = intent.getIntExtra("notificationId", System.currentTimeMillis().toInt())

        Log.d("AlarmReceiver", "Alarm received - action: $action, alarmId: $alarmId")

        createNotificationChannel(context)
        showNotification(context, medicationName, notificationId)
        playAlarmSound(context, notificationId, alarmId)
    }

    private fun showNotification(context: Context, medicationName: String, notificationId: Int) {
        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take your $medicationName")
            .setSmallIcon(R.drawable.ic_notif)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 500, 500))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun playAlarmSound(context: Context, notificationId: Int, alarmId: String) {
        stopAlarmHandler.removeCallbacksAndMessages(null)

        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_clock).apply {
            isLooping = true
            start()
        }

        Log.d("AlarmReceiver", "Alarm sound started.")

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver::WakeLock")
        wakeLock.acquire(180000) // 3 minutes max

        stopAlarmHandler.postDelayed({
            stopAlarm()
            wakeLock.release()
        }, 180000)

        val alarmIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            putExtra("notificationId", notificationId)
            putExtra("alarm_id", alarmId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(alarmIntent)
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AlarmReceiver", "Alarm sound stopped after 3 minutes.")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationHelper.CHANNEL_ID,
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
}