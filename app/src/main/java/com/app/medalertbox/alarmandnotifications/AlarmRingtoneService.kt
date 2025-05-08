package com.app.medalertbox.alarmandnotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.medalertbox.R

class AlarmRingtoneService : Service() {

    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startAlarm()
    }

    private fun startForegroundService() {
        val channelId = "ALARM_RINGTONE_SERVICE"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Sound",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Playing")
            .setContentText("Alarm will stop in 3 minutes")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Auto cancel when alarm stops
            .build()

        startForeground(2, notification)
    }

    private fun startAlarm() {
        AlarmSoundPlayer.start(this) // Use helper class for alarm logic

        Log.d("AlarmRingtoneService", "Alarm sound started. Will stop in 3 minutes.")

        // Stop alarm automatically after 3 minutes
        handler.postDelayed({
            Log.d("AlarmRingtoneService", "3 minutes passed, stopping alarm.")
            stopSelf() // Stops the service and the alarm
        }, 180000) // 180,000 milliseconds = 3 minutes
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundPlayer.stop(this) // Stop sound and vibration
        handler.removeCallbacksAndMessages(null) // Clear any scheduled stop
        Log.d("AlarmRingtoneService", "Alarm stopped and service destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}